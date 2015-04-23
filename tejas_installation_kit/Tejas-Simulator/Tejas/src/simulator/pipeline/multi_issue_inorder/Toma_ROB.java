/**
 * 
 */
package pipeline.multi_issue_inorder;

import generic.Core;
import generic.GlobalClock;
import generic.Instruction;
import generic.OperationType;
import main.CustomObjectPool;
import config.SimulationConfig;

/**
 * @author dell
 *
 */
public class Toma_ROB {

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

		while (true) {

			if (head == -1) {
				// ROB empty .. does not mean execution has completed
				return;
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
					System.out.println("num threads < 0");
				}

				if (this.core.currentThreads == 0) {
					// set exec complete only if there are no other thread already assigned to this pipeline
					containingExecutionEngine.setExecutionComplete(true);
				}

			}

			// update last valid IP seen
			if (firstInst.getCISCProgramCounter() != -1) {
				lastValidIPSeen = firstInst.getCISCProgramCounter();
			}

			// TODO:---- check whether something like below required

			/*
			 * //if store, and if store not yet validated if(firstOpType == OperationType.store &&
			 * !first.getLsqEntry().isValid()) { break; }
			 */

			if (operationType == OperationType.branch) {
				commitBranch(firstRobEntry, firstInst);
			}

			else if (operationType == OperationType.store) {
				// TODO:IMP to be implemented
			}

			else { // not a branch instruction
				commitNonBranch(firstRobEntry, firstInst);
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

		// TODO: check if below required
		/*
		 * if(anyMispredictedBranch) { handleBranchMisprediction(); }
		 */

	}

	private void handleInstructionRetirement(Toma_ROBentry firstRobEntry, Instruction firstInst, int destinationRegNum) {
		firstRobEntry.setBusy(false);
		firstRobEntry.setInstruction(null);

		// increment number of instructions executed
		core.incrementNoOfInstructionsExecuted();
		if (core.getNoOfInstructionsExecuted() % 1000000 == 0) {
			System.out.println(core.getNoOfInstructionsExecuted() / 1000000 + " million done on "
					+ core.getCore_number());
		}

		// debug print
		if (SimulationConfig.debugMode) {
			System.out.println("committed : " + GlobalClock.getCurrentTime() / core.getStepSize() + " : " + firstInst);
		}

		Toma_RegisterFile toma_RF = containingExecutionEngine.getToma_RegisterFile(firstInst.getDestinationOperand());
		if (destinationRegNum != -1 && toma_RF.getToma_ROBEntry(destinationRegNum) == head) {
			toma_RF.setBusy(false, destinationRegNum);
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

	private void commitBranch(Toma_ROBentry firstRobEntry, Instruction firstInst) {

		// branchCount++;
		int destinationRegNum = firstRobEntry.getDestinationRegNumber(); // d

		boolean prediction = performPredictionNtrain(firstInst);
		if (prediction != firstInst.isBranchTaken()) { // branch mispredicted

			// TODO: check branchmispredicted pe stalls aayein

			// no need to clear the ROB or RF_ROBentries..just use stalls

			/*
			 * head = -1; tail = -1; Toma_RegisterFile toma_RF =
			 * containingExecutionEngine.getToma_RegisterFile(firstInst .getDestinationOperand());
			 * toma_RF.clearROBentries();
			 */

			// TODO: algo says --- "fetch branch destination"... I say::: it may not be required
		}

		else { // branch is not mis-predicted
			handleInstructionRetirement(firstRobEntry, firstInst, destinationRegNum);
		}
	}

	private void commitNonBranch(Toma_ROBentry firstRobEntry, Instruction firstInst) {

		int destinationRegNum = firstRobEntry.getDestinationRegNumber(); // d
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
