/**
 * 
 */
package pipeline.multi_issue_inorder;

import config.SimulationConfig;
import generic.Core;
import generic.GlobalClock;
import generic.Instruction;
import generic.OperationType;

/**
 * @author dell
 *
 */
public class Toma_WriteResult {

	MultiIssueInorderExecutionEngine executionEngine;
	Core core;

	public Toma_WriteResult(MultiIssueInorderExecutionEngine executionEngine, Core core) {
		this.executionEngine = executionEngine;
		this.core = core;
	}

	private void rsFreeRobReady(Toma_ReservationStationEntry toma_RSentry) {
		int b = toma_RSentry.getInst_entryNumber_ROB();
		Toma_ROB rob = executionEngine.getToma_ROB();
		rob.getRobEntries()[b].setReady(true);

		Instruction ins = toma_RSentry.getInstruction();

		toma_RSentry.clearEntry();

		if (SimulationConfig.debugMode) {
			System.out.println("\n" + GlobalClock.getCurrentTime() + ": " + "WriteResult | RS free & ROB ready : "
					+ " \n " + ins);
		}
	}

	public void performWriteResult() {
		if (executionEngine.isToma_stall_branchMisprediction())
		// || ROB.head == -1 /*ROB empty*/)//TODO:(IMP) check if required
		{
			return;
		}

		Toma_ReservationStation rs = executionEngine.getToma_ReservationStation();

		for (Toma_ReservationStationEntry toma_RSentry : rs.getReservationStationEntries()) {

			if (!toma_RSentry.isCompletedExecution() || !toma_RSentry.isBusy() || toma_RSentry.isIssuedRequestToCDB()) {
				continue;
			}

			OperationType opType = toma_RSentry.getInstruction().getOperationType();

			if (opType == null) {
				continue;
			}

			if (opType == OperationType.inValid || opType == OperationType.nop || opType == OperationType.jump
					|| opType == OperationType.branch) {
				rsFreeRobReady(toma_RSentry);

				continue;
			}

			if (opType != OperationType.store) {
				// issuing request to CDB
				executionEngine.getCoreMemorySystem().issueRequestToToma_CDB(toma_RSentry);
				toma_RSentry.setIssuedRequestToCDB(true);
				if (SimulationConfig.debugMode) {
					System.out.println("\n" + GlobalClock.getCurrentTime() + ": "
							+ "WriteResult | Issued Request to CDB : " + " \n " + toma_RSentry.getInstruction());
				}
				continue;
			}

			// store instruction
			if (toma_RSentry.getSourceOperand2_availability() != 0) {
				continue;
			}

			rsFreeRobReady(toma_RSentry);
			// TO-DO:- => ROB[h].value = RS[r].VK;...not required

		}
	}
}
