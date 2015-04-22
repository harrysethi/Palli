/**
 * 
 */
package pipeline.multi_issue_inorder;

import generic.Core;
import generic.Instruction;
import generic.OperationType;

/**
 * @author dell
 *
 */
public class Toma_Issue {

	MultiIssueInorderExecutionEngine executionEngine;
	// GenericCircularQueue<Instruction> toma_fetchBuffer;

	StageLatch_MII ifIdLatch;

	public Toma_Issue(Core core, MultiIssueInorderExecutionEngine executionEngine) {
		this.executionEngine = executionEngine;
		// toma_fetchBuffer = executionEngine.getToma_fetchBuffer();
		ifIdLatch = executionEngine.getIfIdLatch();
	}

	public void performIssue() {

		// Instruction ins = toma_fetchBuffer.peek(0);

		while (ifIdLatch.isEmpty() == false) {
			Instruction ins = ifIdLatch.peek(0);

			if (ins == null) {
				return;
			}

			if (ins.getOperationType() == OperationType.load || ins.getOperationType() == OperationType.store) {
				if (executionEngine.getCoreMemorySystem().getToma_LSQ().isFull()) {
					// executionEngine.setToStall3(true);//TODO: commented..don't know whether to use
					break;// TODO: check yahaan "return" aayega ya "continue" ya "break"
				}
			}

			Toma_ReservationStation rs = executionEngine.getToma_ReservationStation();
			Toma_ROB rob = executionEngine.getToma_ROB();

			Toma_RegisterFile toma_RF = executionEngine.getToma_RegisterFile(ins.getSourceOperand1(),
					ins.getSourceOperand2());

			Toma_ReservationStationEntry rs_freeEntry = rs.getFreeEntryIn_RS();
			if (rs_freeEntry == null) {
				// TODO: check yahaan pe memStall ka kuch karna hai kya?
				break;
			}

			int rob_freeTail = rob.getROB_freeTail(); // b
			if (rob_freeTail == -1) {
				// TODO: check yahaan pe memStall ka kuch karna hai kya?
				break;
			}

			int register_source1 = -1;
			int register_source2 = -1;
			int register_dest = -1;

			if (ins.getSourceOperand1() != null) {
				register_source1 = (int) ins.getSourceOperand1().getValue(); // rs
				if (toma_RF.isBusy(register_source1)) {
					int h = toma_RF.getToma_ROBEntry(register_source1);

					Toma_ROBentry rob_h = rob.getRobEntries()[h];

					if (rob_h.isReady()) {
						rs_freeEntry.setSourceOperand1_value(rob_h.getResultValue());
						rs_freeEntry.setSourceOperand1_avaliability(0);
					}

					else {
						rs_freeEntry.setSourceOperand1_avaliability(h);
					}

				} else {// else for "if (rf.isBusy(register_source1)) {"
					rs_freeEntry.setSourceOperand1_value(toma_RF.getValue(register_source1));
					rs_freeEntry.setSourceOperand1_avaliability(0);
				}
			}

			else { // else for "if (ins.getSourceOperand1() != null) {"
					// sourceOperand1 is available if it is null
				rs_freeEntry.setSourceOperand1_avaliability(0);// TODO: check shall be fine
			}

			if (ins.getSourceOperand2() != null) {
				register_source2 = (int) ins.getSourceOperand2().getValue(); // rt

				if (toma_RF.isBusy(register_source2)) {
					int h = toma_RF.getToma_ROBEntry(register_source2);

					Toma_ROBentry rob_h = rob.getRobEntries()[h];

					if (rob_h.isReady()) {
						rs_freeEntry.setSourceOperand2_value(rob_h.getResultValue());
						rs_freeEntry.setSourceOperand2_avaliability(0);
					}

					else {
						rs_freeEntry.setSourceOperand2_avaliability(h);
					}

				} else {
					rs_freeEntry.setSourceOperand2_value(toma_RF.getValue(register_source2));
					rs_freeEntry.setSourceOperand2_avaliability(0);
				}
			} else { // else for "if (ins.getSourceOperand2() != null) {"
						// sourceOperand1 is available if it is null
				rs_freeEntry.setSourceOperand2_avaliability(0);
			}

			if (ins.getDestinationOperand() != null) {
				register_dest = (int) ins.getDestinationOperand().getValue(); // rd

				toma_RF.setToma_ROBEntry(rob_freeTail, register_dest);
				toma_RF.setBusy(true, register_dest);
			}

			rs_freeEntry.setInstruction(ins);
			rs_freeEntry.setBusy(true);
			rs_freeEntry.setInst_entryNumber_ROB(rob_freeTail);

			Toma_ROBentry rob_freeTail_entry = rob.getRobEntries()[rob_freeTail];

			rob_freeTail_entry.setBusy(true);
			rob_freeTail_entry.setInstruction(ins);

			rob_freeTail_entry.setDestinationRegNumber(register_dest);

			rob_freeTail_entry.setReady(false);

			if (ins.getOperationType() == OperationType.load) {
				rs_freeEntry.setAddress(ins.getSourceOperand2MemValue());// TODO. check imm is sourceOperand
				toma_RF.setToma_ROBEntry(rob_freeTail, register_source2);
				toma_RF.setBusy(true, register_source2);
				rob_freeTail_entry.setDestinationRegNumber(register_source2);
			}

			if (ins.getOperationType() == OperationType.store) {
				rs_freeEntry.setAddress(ins.getSourceOperand1MemValue());// TODO. check imm is sourceOperand
			}

			// adding instruction to LSQ in case of load/store
			if (ins.getOperationType() == OperationType.load || ins.getOperationType() == OperationType.store) {
				boolean isLoad;
				if (ins.getOperationType() == OperationType.load)
					isLoad = true;
				else
					isLoad = false;

				executionEngine.getCoreMemorySystem().allocateToma_LSQEntry(isLoad,
						rob_freeTail_entry.getInstruction().getSourceOperand1MemValue(), rob_freeTail_entry);
			}

			ifIdLatch.poll();
		}
	}

}
