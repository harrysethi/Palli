/**
 * 
 */
package pipeline.multi_issue_inorder;

import config.SimulationConfig;
import generic.Core;
import generic.GlobalClock;
import generic.Instruction;
import generic.Operand;
import generic.OperandType;
import generic.OperationType;

/**
 * @author dell
 *
 */
public class Toma_Issue {

	MultiIssueInorderExecutionEngine executionEngine;
	Core core;
	// GenericCircularQueue<Instruction> toma_fetchBuffer;

	StageLatch_MII ifIdLatch;

	public Toma_Issue(Core core, MultiIssueInorderExecutionEngine executionEngine) {
		this.executionEngine = executionEngine;
		// toma_fetchBuffer = executionEngine.getToma_fetchBuffer();
		this.ifIdLatch = executionEngine.getIfIdLatch();
		this.core = core;
	}

	public void performIssue() {

		// Instruction ins = toma_fetchBuffer.peek(0);

		if (executionEngine.isToma_stall_branchMisprediction()) {
			return;
		}

		while (ifIdLatch.isEmpty() == false) {
			Instruction ins = ifIdLatch.peek(0);

			if (ins == null) {
				return;
			}

			if (ins.getOperationType() == OperationType.load || ins.getOperationType() == OperationType.store) {
				if (executionEngine.getCoreMemorySystem().getToma_LSQ().isFull()) {
					// executionEngine.setToStall3(true);
					break;
				}
			}

			Toma_ReservationStation rs = executionEngine.getToma_ReservationStation();
			Toma_ROB rob = executionEngine.getToma_ROB();

			Toma_ReservationStationEntry rs_freeEntry = rs.getFreeEntryIn_RS();
			if (rs_freeEntry == null) {
				// TO-DO: check yahaan pe memStall ka kuch karna hai kya?
				break;
			}

			int rob_freeTail = rob.getROB_freeTail(); // b
			if (rob_freeTail == -1) {
				// TO-DO: check yahaan pe memStall ka kuch karna hai kya?
				break;
			}

			if (ins.getSourceOperand1() != null && !isImmediateOperand(ins.getSourceOperand1())) {
				int register_source1;
				Toma_RegisterFile toma_RF_source1;
				if (ins.getSourceOperand1().getOperandType() == OperandType.memory) {
					register_source1 = (int) ins.getSourceOperand1().getMemoryLocationFirstOperand().getValue(); // rs
					toma_RF_source1 = executionEngine.getToma_RegisterFile(ins.getSourceOperand1()
							.getMemoryLocationFirstOperand());
				} else {
					register_source1 = (int) ins.getSourceOperand1().getValue(); // rs
					toma_RF_source1 = executionEngine.getToma_RegisterFile(ins.getSourceOperand1());
				}

				if (toma_RF_source1.isBusy(register_source1)) {
					int h = toma_RF_source1.getToma_ROBEntry(register_source1);

					Toma_ROBentry rob_h = rob.getRobEntries()[h];

					if (rob_h.isReady()) {
						rs_freeEntry.setSourceOperand1_value(rob_h.getResultValue());
						rs_freeEntry.setSourceOperand1_availability(0);
					}

					else {
						rs_freeEntry.setSourceOperand1_availability(h);
					}

				} else {// else for "if (rf.isBusy(register_source1)) {"
					rs_freeEntry.setSourceOperand1_value(toma_RF_source1.getValue(register_source1));
					rs_freeEntry.setSourceOperand1_availability(0);
				}

				if (ins.getOperationType() == OperationType.xchg) {
					toma_RF_source1.setToma_ROBEntry(rob_freeTail, register_source1);
					toma_RF_source1.setBusy(true, register_source1);
				}
			}

			else { // else for "if (ins.getSourceOperand1() != null) {"
					// sourceOperand1 is available if it is null
				rs_freeEntry.setSourceOperand1_availability(0);
			}

			if (ins.getSourceOperand2() != null && !isImmediateOperand(ins.getSourceOperand2())) {

				Toma_RegisterFile toma_RF_source2 = executionEngine.getToma_RegisterFile(ins.getSourceOperand2());
				int register_source2 = (int) ins.getSourceOperand2().getValue(); // rt

				if (toma_RF_source2.isBusy(register_source2)) {
					int h = toma_RF_source2.getToma_ROBEntry(register_source2);

					Toma_ROBentry rob_h = rob.getRobEntries()[h];

					if (rob_h.isReady()) {
						rs_freeEntry.setSourceOperand2_value(rob_h.getResultValue());
						rs_freeEntry.setSourceOperand2_availability(0);
					}

					else {
						rs_freeEntry.setSourceOperand2_availability(h);
					}

				} else {
					rs_freeEntry.setSourceOperand2_value(toma_RF_source2.getValue(register_source2));
					rs_freeEntry.setSourceOperand2_availability(0);
				}

				if (ins.getOperationType() == OperationType.xchg) {
					toma_RF_source2.setToma_ROBEntry(rob_freeTail, register_source2);
					toma_RF_source2.setBusy(true, register_source2);
				}
			} else { // else for "if (ins.getSourceOperand2() != null) {"
						// sourceOperand1 is available if it is null
				rs_freeEntry.setSourceOperand2_availability(0);
			}

			int register_dest = -1;
			if (ins.getDestinationOperand() != null) {
				Toma_RegisterFile toma_RF_dest = executionEngine.getToma_RegisterFile(ins.getDestinationOperand());

				register_dest = (int) ins.getDestinationOperand().getValue(); // rd

				toma_RF_dest.setToma_ROBEntry(rob_freeTail, register_dest);
				toma_RF_dest.setBusy(true, register_dest);
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
				rs_freeEntry.setAddress(ins.getSourceOperand1MemValue());
			}

			if (ins.getOperationType() == OperationType.store) {
				rs_freeEntry.setAddress(ins.getSourceOperand1MemValue());
			}

			// adding instruction to LSQ in case of load/store
			if (ins.getOperationType() == OperationType.load || ins.getOperationType() == OperationType.store) {
				boolean isLoad;
				if (ins.getOperationType() == OperationType.load)
					isLoad = true;
				else
					isLoad = false;

				executionEngine.getCoreMemorySystem().allocateToma_LSQEntry(isLoad,
						rob_freeTail_entry.getInstruction().getSourceOperand1MemValue(), rob_freeTail_entry,
						rs_freeEntry);
			}

			if (SimulationConfig.debugMode) {
				System.out.println("Issue | Issued : " + " \n " + ins);
			}

			ifIdLatch.poll();
		}
	}

	private boolean isImmediateOperand(Operand operand) {
		return operand.getOperandType() == OperandType.immediate;
	}

}
