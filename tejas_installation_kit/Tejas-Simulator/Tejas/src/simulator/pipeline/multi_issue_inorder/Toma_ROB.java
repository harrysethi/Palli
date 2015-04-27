/**
 * 
 */
package pipeline.multi_issue_inorder;

import generic.Core;
import generic.Event;
import generic.EventQueue;
import generic.GlobalClock;
import generic.Instruction;
import generic.OperationType;
import generic.PortType;
import generic.RequestType;
import generic.SimulationElement;
import main.CustomObjectPool;
import memorysystem.Toma_LSQ;
import config.SimulationConfig;

/**
 * @author dell
 *
 */
public class Toma_ROB extends SimulationElement {

	private MultiIssueInorderExecutionEngine containingExecutionEngine;
	private Core core;

	private Toma_ROBentry[] robEntries;
	private int head;
	private int tail;

	private int maxROBSize; // configurable

	// private long branchCount;
	// TO-DO:----check branchCount ko finally kahaan use kar re hain, ya fer iski zaroorat hi ni hai?

	private long lastValidIPSeen;

	public Toma_ROB(MultiIssueInorderExecutionEngine containingExecutionEngine, Core core) {
		super(PortType.Unlimited, -1, -1, -1, -1);

		this.maxROBSize = core.getToma_robBufferSize() + 1;
		// '1' added since we are starting the counter with 1
		head = -1;
		tail = -1;

		robEntries = new Toma_ROBentry[maxROBSize];
		for (int i = 1; i < maxROBSize; i++) {
			robEntries[i] = new Toma_ROBentry();
		}

		lastValidIPSeen = -1;

		// branchCount = 0;

		this.containingExecutionEngine = containingExecutionEngine;
		this.core = core;

	}

	public void performCommits() {

		if (containingExecutionEngine.isToma_stall_branchMisprediction()) {
			return;
		}

		boolean isAnyMispredictedBranch = false;
		while (true) {

			if (head == -1) {
				// ROB empty .. does not mean execution has completed
				break;
			}

			Toma_ROBentry firstRobEntry = robEntries[head];

			Instruction firstInst = firstRobEntry.getInstruction();
			OperationType operationType = firstInst.getOperationType();

			if (!firstRobEntry.isReady()) {
				// instruction at the head is not ready..stop commiting
				break;
			}

			if (firstInst.getOperationType() == OperationType.inValid) {
				this.core.currentThreads--;

				if (this.core.currentThreads < 0) {
					this.core.currentThreads = 0;
				}

				if (this.core.currentThreads == 0) {
					// set exec complete only if there are no other thread already assigned to this pipeline
					containingExecutionEngine.setExecutionComplete(true);
				}

			}

			int destinationRegNum = firstRobEntry.getDestinationRegNumber(); // d

			// update last valid IP seen
			if (firstInst.getCISCProgramCounter() != -1) {
				lastValidIPSeen = firstInst.getCISCProgramCounter();
			}

			// TO-DO:---- check whether something like below required...chhod de naaa (pallavi said)

			/*
			 * //if store, and if store not yet validated if(firstOpType == OperationType.store &&
			 * !first.getLsqEntry().isValid()) { break; }
			 */

			if (operationType == OperationType.branch) {
				isAnyMispredictedBranch = commitBranch(firstRobEntry, firstInst, destinationRegNum);
			}

			else if (operationType == OperationType.store) {

				boolean memReqIssued = containingExecutionEngine.multiIssueInorderCoreMemorySystem
						.issueRequestToL1Cache(RequestType.Cache_Write, firstRobEntry.getToma_lsqEntry().getAddress(),
								firstRobEntry.getToma_lsqEntry().getIndexInQ());

				if (memReqIssued == false) {
					break;
				}

				handleInstructionRetirement(firstRobEntry, firstInst, destinationRegNum);

			}

			else { // not a branch instruction
				commitNonBranch(firstRobEntry, firstInst, destinationRegNum);
			}

			if (head == tail) {
				head = -1;
				tail = -1;
			}

			else {
				head = (head + 1) % maxROBSize;
				if (head == 0)
					head = 1;
			}
		}

		if (isAnyMispredictedBranch) {
			System.out.println("\n" + GlobalClock.getCurrentTime() + ": " + "=====Mispredicted Branch===== :( :(");
			handleBranchMisprediction();
		}

	}

	@Override
	public void handleEvent(EventQueue eventQ, Event event) {

		if (event.getRequestType() == RequestType.MISPRED_PENALTY_COMPLETE) {
			System.out.println("\n" + GlobalClock.getCurrentTime() + ": " + "=====Branch Penalty Complete===== :) :)");
			completeMispredictionPenalty();
		}

	}

	void completeMispredictionPenalty() {
		containingExecutionEngine.setToma_stall_branchMisprediction(false);
	}

	private void handleBranchMisprediction() {
		// impose branch mis-prediction penalty
		containingExecutionEngine.setToma_stall_branchMisprediction(true);

		// set-up event that signals end of misprediction penalty period
		core.getEventQueue().addEvent(
				new Toma_branch_misprediction_completeEvent(GlobalClock.getCurrentTime()
						+ core.getBranchMispredictionPenalty() * core.getStepSize(), null, this,
						RequestType.MISPRED_PENALTY_COMPLETE));

	}

	private void handleInstructionRetirement(Toma_ROBentry firstRobEntry, Instruction firstInst, int destinationRegNum) {
		firstRobEntry.setBusy(false);
		firstRobEntry.setReady(false);
		firstRobEntry.setInstruction(null);
		firstRobEntry.setDestinationRegNumber(-1);

		if (firstInst.getOperationType() == OperationType.load || firstInst.getOperationType() == OperationType.store) {
			Toma_LSQ toma_LSQ = containingExecutionEngine.getCoreMemorySystem().getToma_LSQ();
			toma_LSQ.removeEntry(firstRobEntry.getToma_lsqEntry());
			firstRobEntry.setToma_lsqEntry(null);
		}

		// increment number of instructions executed
		core.incrementNoOfInstructionsExecuted();
		if (core.getNoOfInstructionsExecuted() % 1000000 == 0) {
			System.out.println(core.getNoOfInstructionsExecuted() / 1000000 + " million done on "
					+ core.getCore_number());
		}

		Toma_RegisterFile toma_RF = containingExecutionEngine.getToma_RegisterFile(firstInst.getDestinationOperand());
		if (destinationRegNum != -1 && toma_RF.getToma_ROBEntry(destinationRegNum) == head) {
			toma_RF.setBusy(false, destinationRegNum);
			toma_RF.setToma_ROBEntry(-1, destinationRegNum);
		}

		if (firstInst.getOperationType() == OperationType.xchg) {

			Toma_RegisterFile toma_RF_source1 = containingExecutionEngine.getToma_RegisterFile(firstInst
					.getSourceOperand1());
			int register_source1 = (int) firstInst.getSourceOperand1().getValue();
			if (register_source1 != -1 && toma_RF_source1.getToma_ROBEntry(register_source1) == head) {
				toma_RF_source1.setBusy(false, register_source1);
				toma_RF_source1.setToma_ROBEntry(-1, register_source1);
			}

			Toma_RegisterFile toma_RF_source2 = containingExecutionEngine.getToma_RegisterFile(firstInst
					.getSourceOperand2());
			int register_source2 = (int) firstInst.getSourceOperand2().getValue();
			if (register_source2 != -1 && toma_RF_source2.getToma_ROBEntry(register_source2) == head) {
				toma_RF_source2.setBusy(false, register_source2);
				toma_RF_source2.setToma_ROBEntry(-1, register_source2);
			}
		}

		// debug print
		if (SimulationConfig.debugMode) {
			System.out.println("\n" + GlobalClock.getCurrentTime() + ": " + "COMMIT | committed : " + " \n "
					+ firstInst);
		}

		returnInstructionToPool(firstInst);
	}

	private boolean performPredictionNtrain(Instruction firstInst) {
		boolean prediction = performPrediction(firstInst);
		trainPredictor(firstInst, prediction);
		return prediction;
	}

	private void trainPredictor(Instruction firstInst, boolean prediction) {
		this.containingExecutionEngine.getBranchPredictor().Train(lastValidIPSeen, firstInst.isBranchTaken(),
				prediction);
		this.containingExecutionEngine.getBranchPredictor().incrementNumAccesses(1);
	}

	private boolean performPrediction(Instruction firstInst) {
		boolean prediction = this.containingExecutionEngine.getBranchPredictor().predict(lastValidIPSeen,
				firstInst.isBranchTaken());
		this.containingExecutionEngine.getBranchPredictor().incrementNumAccesses(1);
		return prediction;
	}

	private boolean commitBranch(Toma_ROBentry firstRobEntry, Instruction firstInst, int destinationRegNum) {

		// branchCount++;
		boolean isMispredictedBranch = false;

		boolean prediction = performPredictionNtrain(firstInst);
		if (prediction != firstInst.isBranchTaken()) { // branch mispredicted

			isMispredictedBranch = true;

			// no need to clear the ROB or RF_ROBentries..just use stalls

			/*
			 * head = -1; tail = -1; Toma_RegisterFile toma_RF =
			 * containingExecutionEngine.getToma_RegisterFile(firstInst .getDestinationOperand());
			 * toma_RF.clearROBentries();
			 */

			// TO-DO: algo says --- "fetch branch destination"... I say::: it may not be required
		}

		handleInstructionRetirement(firstRobEntry, firstInst, destinationRegNum);

		return isMispredictedBranch;
	}

	private void commitNonBranch(Toma_ROBentry firstRobEntry, Instruction firstInst, int destinationRegNum) {

		Object robHead_value = firstRobEntry.getResultValue(); // ROB[h].value

		if (destinationRegNum != -1) {
			Toma_RegisterFile toma_RF = containingExecutionEngine.getToma_RegisterFile(firstInst
					.getDestinationOperand());
			toma_RF.setValue(robHead_value, destinationRegNum);
		}

		handleInstructionRetirement(firstRobEntry, firstInst, destinationRegNum);
	}

	private void returnInstructionToPool(Instruction instruction) {
		try {
			CustomObjectPool.getInstructionPool().returnObject(instruction);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// returns -1 if rob tail is not free
	public int getROB_freeTail() {
		if (tail == -1) {
			head = 1;
			tail = 1;
			return tail;
		}

		int nextTail = (tail + 1) % maxROBSize;

		if (nextTail == 0)
			nextTail = 1;

		if (!robEntries[nextTail].isBusy()) {
			tail = nextTail;
			return tail;
		}

		return -1;
	}

	/**
	 * @return the robEntries
	 */
	public Toma_ROBentry[] getRobEntries() {
		return robEntries;
	}

}
