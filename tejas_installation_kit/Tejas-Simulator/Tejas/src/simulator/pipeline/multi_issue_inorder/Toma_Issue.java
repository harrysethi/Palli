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
	GenericCircularQueue<Instruction> toma_fetchBuffer;

	public Toma_Issue(Core core, MultiIssueInorderExecutionEngine executionEngine) {
		// TODO: check do we need "super(PortType.Unlimited, -1, -1, -1, -1);"... i think hona chahiye
		this.core = core;
		this.executionEngine = executionEngine;
		toma_fetchBuffer = executionEngine.getToma_fetchBuffer();
	}

	public void performIssue() {

		Instruction ins = toma_fetchBuffer.peek(0);

		if (ins == null) {
			return;
		}

		if (ins.getOperationType() == OperationType.load || ins.getOperationType() == OperationType.store) {
			if (executionEngine.getCoreMemorySystem().getToma_LSQ().isFull()) {
				// executionEngine.setToStall3(true);//TODO: commented..don't know whether to use
				return;
			}
		}

		Toma_ReservationStation rs = executionEngine.getToma_ReservationStation();
		Toma_ROB rob = executionEngine.getToma_ROB();
		Toma_RegisterFile rf = executionEngine.getToma_RegisterFile_integer();// TODO: using abi integer registerFile

		Toma_ReservationStationEntry rs_freeEntry = rs.getFreeEntryIn_RS();
		if (rs_freeEntry == null) {
			// TODO: check yahaan pe memStall ka kuch karna hai kya?
			return;
		}

		int rob_freeTail = rob.getROB_freeTail(); // b
		if (rob_freeTail == -1) {
			// TODO: check yahaan pe memStall ka kuch karna hai kya?
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

		if (ins.getOperationType() == OperationType.load) {
			rs_freeEntry.setAddress(ins.getSourceOperand2MemValue());// TODO. check imm is sourceOperand
			rf.setToma_ROBEntry(rob_freeTail, register_source2);
			rf.setBusy(true, register_source2);
			rob_freeTail_entry.setDestinationRegNumber(register_source2);
		}

		if (ins.getOperationType() == OperationType.store) {
			rs_freeEntry.setAddress(ins.getSourceOperand1MemValue());// TODO. check imm is sourceOperand
		}
	}

}
