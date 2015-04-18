/**
 * 
 */
package pipeline.multi_issue_inorder;

import generic.Core;
import generic.GenericCircularQueue;
import generic.Instruction;
import generic.OperationType;

/**
 * @author dell
 *
 */
public class Toma_Issue {
	// TODO:--- check whether to extend simulation element
	// TODO: --- check do we need latch

	MultiIssueInorderExecutionEngine executionEngine;
	Core core;// TODO: core ko hatao agar ni chiye to

	public GenericCircularQueue<Instruction> inputToPipeline;

	// TODO: check this is a single queue rather a array as in OOO

	public Toma_Issue(Core core, MultiIssueInorderExecutionEngine executionEngine) {
		// TODO: check do we need "super(PortType.Unlimited, -1, -1, -1, -1);"... i think hona chahiye
		this.core = core;
		this.executionEngine = executionEngine;
	}

	public void performIssue() {
		// TODO: check do we need icacheBuffer
		// TODO: logic in inOrder & OOO -- sir se upar ja ra hai... check later

		if (inputToPipeline.isEmpty())
			return;

		Instruction ins = inputToPipeline.pollFirst();

		if (ins == null)
			return;

		if (ins.getOperationType() == OperationType.inValid) {
			executionEngine.setExecutionComplete(true);
			return;
		}

		// TODO: shall be some logic if instr is invalid

		// TODO: neeche vaala code..abi mein aise hi rakh rha hoon..delete later if required..I liked the code
		/*
		 * long fURequest = 0; if (OpTypeToFUTypeMapping.getFUType(ins.getOperationType()) != FunctionalUnitType.inValid) { fURequest = containingExecutionEngine.getExecutionCore
		 * ().requestFU(OpTypeToFUTypeMapping .getFUType(ins.getOperationType()));
		 * 
		 * if (fURequest > 0) { break; } }
		 */

		Toma_ReservationStation rs = executionEngine.getToma_ReservationStation();
		Toma_ROB rob = executionEngine.getToma_ROB();
		Toma_RegisterFile rf = executionEngine.getToma_RegisterFile_integer();// TODO: using abi integer registerFile

		Toma_ReservationStationEntry rs_freeEntry = rs.getFreeEntryIn_RS();
		if (rs_freeEntry == null) {
			return;
		}

		int rob_freeTail = rob.getROB_freeTail(); // b
		if (rob_freeTail == -1) {
			return;
		}

		int register_source1 = (int) ins.getSourceOperand1().getValue(); // rs
		int register_source2 = (int) ins.getSourceOperand2().getValue(); // rt
		int register_dest = (int) ins.getDestinationOperand().getValue(); // rd

		if (rf.isBusy(register_source1)) {
			int h = rf.getToma_ROBEntry(register_source1);

			Toma_ROBentry rob_h = rob.getRobEntries()[h];

			if (rob_h.isReady()) {
				rs_freeEntry.setSourceOperand1_value(rob_h.getResultValue());
				rs_freeEntry.setSourceOperand1_avaliability(0);
			}

			else {
				rs_freeEntry.setSourceOperand1_avaliability(h);
			}

		} else {
			rs_freeEntry.setSourceOperand1_value(rf.getValue(register_source1));
			rs_freeEntry.setSourceOperand1_avaliability(0);
		}

		if (rf.isBusy(register_source2)) {
			int h = rf.getToma_ROBEntry(register_source2);

			Toma_ROBentry rob_h = rob.getRobEntries()[h];

			if (rob_h.isReady()) {
				rs_freeEntry.setSourceOperand2_value(rob_h.getResultValue());
				rs_freeEntry.setSourceOperand2_avaliability(0);
			}

			else {
				rs_freeEntry.setSourceOperand2_avaliability(h);
			}

		} else {
			rs_freeEntry.setSourceOperand2_value(rf.getValue(register_source2));
			rs_freeEntry.setSourceOperand2_avaliability(0);
		}

		rs_freeEntry.setOperationType(ins.getOperationType());
		rs_freeEntry.setBusy(true);
		rs_freeEntry.setInst_entryNumber_ROB(rob_freeTail);

		rf.setToma_ROBEntry(rob_freeTail, register_dest);
		rf.setBusy(true, register_dest);

		Toma_ROBentry rob_freeTail_entry = rob.getRobEntries()[rob_freeTail];

		rob_freeTail_entry.setBusy(true);
		rob_freeTail_entry.getInstruction().setOperationType(ins.getOperationType());
		rob_freeTail_entry.setDestinationRegNumber(register_dest);
		rob_freeTail_entry.setReady(false);
	}

	/**
	 * @param inputToPipeline
	 *            the inputToPipeline to set
	 */
	public void setInputToPipeline(GenericCircularQueue<Instruction> inputToPipeline) {
		this.inputToPipeline = inputToPipeline;
	}

}
