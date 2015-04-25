/**
 * 
 */
package pipeline.multi_issue_inorder;

import generic.OperationType;

/**
 * @author dell
 *
 */
public class Toma_WriteResult {

	MultiIssueInorderExecutionEngine executionEngine;

	public Toma_WriteResult(MultiIssueInorderExecutionEngine executionEngine) {
		this.executionEngine = executionEngine;
	}

	public void performWriteResult() {
		if (executionEngine.isToma_stall_branchMisprediction())
		// || ROB.head == -1 /*ROB empty*/)//TODO: check if required
		{
			return;
		}

		Toma_ReservationStation rs = executionEngine.getToma_ReservationStation();

		for (Toma_ReservationStationEntry toma_RSentry : rs.getReservationStationEntries()) {

			if (!toma_RSentry.isCompletedExecution() && !toma_RSentry.isBusy()) {
				continue;
			}

			OperationType opType = toma_RSentry.getInstruction().getOperationType();

			if (opType == null) {
				continue;
			}

			if (opType == OperationType.inValid || opType == OperationType.nop) {
				int b = toma_RSentry.getInst_entryNumber_ROB();
				Toma_ROB rob = executionEngine.getToma_ROB();
				rob.getRobEntries()[b].setReady(true);

				toma_RSentry.setBusy(false);
				toma_RSentry.setCompletedExecution(false);
				toma_RSentry.setStartedExecution(false);
				toma_RSentry.setSourceOperand1_availability(-1);
				toma_RSentry.setSourceOperand2_availability(-1);

				continue;
			}

			if (opType != OperationType.store) {
				// issuing request to CDB
				executionEngine.getCoreMemorySystem().issueRequestToToma_CDB(toma_RSentry);
				continue;
			}

			// store instruction
			if (toma_RSentry.getSourceOperand2_availability() != 0) {
				return;
			}

			// TODO:-imp => ROB[h].value = RS[r].VK;...not required

		}
	}
}
