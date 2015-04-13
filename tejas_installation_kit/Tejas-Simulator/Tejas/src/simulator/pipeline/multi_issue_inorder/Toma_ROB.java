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

	// TODO:--- check whether to extend simulation element
	// TODO:--- check whether we need retireWidth

	private MultiIssueInorderExecutionEngine containingExecutionEngine;
	private Core core;

	private Toma_ROBentry[] robEntries;
	private int head;
	private int tail;
	// int size; //TODO:---check if this required

	private int maxROBSize; // configurable

	private long branchCount;// TODO:----check branchCount ko finally kahaan use kar re hain, ya fer iski zaroorat hi ni hai?

	private long lastValidIPSeen;

	public Toma_ROB(MultiIssueInorderExecutionEngine containingExecutionEngine, Core core) {

		head = -1;
		tail = -1;

		robEntries = new Toma_ROBentry[maxROBSize];
		for (int i = 0; i < maxROBSize; i++) {
			robEntries[i] = new Toma_ROBentry();
		}

		lastValidIPSeen = -1;

		branchCount = 0;

		this.containingExecutionEngine = containingExecutionEngine;
		this.core = core;
	}

	public void performCommits() {

		while (true) {// TODO:---chk this condition may need to be changed

			if (head == -1) {
				// ROB empty .. does not mean execution has completed
				return;
			}

			Toma_ROBentry firstRobEntry = robEntries[head];

			Instruction firstInst = firstRobEntry.getInstruction();
			OperationType operationType = firstInst.getOperationType();

			// TODO:---chk if this requires some change
			// "wait until instruction reaches head of ROB"
			if (firstRobEntry.isReady()) {

				// update last valid IP seen
				if (firstInst.getCISCProgramCounter() != -1) {
					lastValidIPSeen = firstInst.getCISCProgramCounter();
				}

				// TODO:--- chk "if(first.isWriteBackDone() == true)"

				// TODO:--- chk this is required....."if(firstOpType==OperationType.inValid)"

				// TODO:---- check whether something like below required

				/*
				 * //if store, and if store not yet validated if(firstOpType == OperationType.store && !first.getLsqEntry().isValid()) { break; }
				 */

				if (operationType == OperationType.branch) {
					commitBranch(firstRobEntry, firstInst, head);
				}

				else { // not a branch instruction
					commitNonBranch(firstRobEntry, firstInst, head);
				}

				// TODO: check if this is required
				/*
				 * //Signal LSQ for committing the Instruction at the queue head if(firstOpType == OperationType.load || firstOpType == OperationType.store) { if
				 * (!first.getLsqEntry().isValid()) { misc .Error.showErrorAndExit("The committed entry is not valid"); }
				 * 
				 * execEngine.getCoreMemorySystem().issueLSQCommit(first); }
				 */
			}

			else {// the firstInstruction is not ready..stop commiting
					// TODO:----check this is fine...i.e. ki yahaan break hi aayega }
				break;
			}
		}

		// TODO: check if below required
		/*
		 * if(anyMispredictedBranch) { handleBranchMisprediction(); }
		 */

	}

	private void handleInstructionRetirement(Toma_ROBentry firstRobEntry, Instruction firstInst, int destinationRegNum, int head) {
		firstRobEntry.setBusy(false);
		returnInstructionToPool(firstInst);

		// increment number of instructions executed
		core.incrementNoOfInstructionsExecuted();
		if (core.getNoOfInstructionsExecuted() % 1000000 == 0) {
			System.out.println(core.getNoOfInstructionsExecuted() / 1000000 + " million done on " + core.getCore_number());
		}

		// debug print
		if (SimulationConfig.debugMode) {
			System.out.println("committed : " + GlobalClock.getCurrentTime() / core.getStepSize() + " : " + firstInst);
		}

		Toma_RegisterFile toma_registerFile_integer = containingExecutionEngine.getToma_RegisterFile_integer();
		if (toma_registerFile_integer.getToma_ROBEntry(destinationRegNum) == head) {
			toma_registerFile_integer.setBusy(false, destinationRegNum);
		}
	}

	private boolean performPredictionNtrain(Instruction firstInst) {
		boolean prediction = performPrediction(firstInst);
		trainPredictor(firstInst, prediction);
		return prediction;
	}

	private void trainPredictor(Instruction firstInst, boolean prediction) {
		this.containingExecutionEngine.getBranchPredictor().Train(lastValidIPSeen, firstInst.isBranchTaken(), prediction);
		this.containingExecutionEngine.getBranchPredictor().incrementNumAccesses(1);
	}

	private boolean performPrediction(Instruction firstInst) {
		boolean prediction = this.containingExecutionEngine.getBranchPredictor().predict(lastValidIPSeen, firstInst.isBranchTaken());
		this.containingExecutionEngine.getBranchPredictor().incrementNumAccesses(1);
		return prediction;
	}

	private void commitBranch(Toma_ROBentry firstRobEntry, Instruction firstInst, int head) {
		boolean prediction = performPredictionNtrain(firstInst);

		branchCount++;
		int destinationRegNum = firstRobEntry.getDestinationRegNumber();

		if (prediction != firstInst.isBranchTaken()) { // branch mispredicted
			head = -1;
			tail = -1;

			// TODO: check whether we need to make all the instructions present to isBusy = false...not required intuitively
			// TODO: check we may need to return the instructions present to intructionPool

			// TODO: algo says --- "fetch branch destination"... I say::: it may not be required....ab kaun sahi hai?... aage dekhenge..HUM LOG
		}

		else { // branch is not mis-predicted
			handleInstructionRetirement(firstRobEntry, firstInst, destinationRegNum, head);
		}
	}

	private void commitNonBranch(Toma_ROBentry firstRobEntry, Instruction firstInst, int head) {

		int destinationRegNum = firstRobEntry.getDestinationRegNumber();
		int robHead_value = firstRobEntry.getResultValue();

		Toma_RegisterFile toma_registerFile_integer = containingExecutionEngine.getToma_RegisterFile_integer();
		toma_registerFile_integer.setValue(robHead_value, destinationRegNum);

		handleInstructionRetirement(firstRobEntry, firstInst, destinationRegNum, head);
	}

	private void returnInstructionToPool(Instruction instruction) {
		try {
			CustomObjectPool.getInstructionPool().returnObject(instruction);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
