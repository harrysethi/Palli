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
		Toma_ReservationStation rs = executionEngine.getToma_ReservationStation();

		for (Toma_ReservationStationEntry toma_RSentry : rs.getReservationStationEntries()) {

			if (!toma_RSentry.isCompletedExecution()) {
				continue;
			}

			if (toma_RSentry.getInstruction().getOperationType() == OperationType.inValid) {
				int b = toma_RSentry.getInst_entryNumber_ROB();
				Toma_ROB rob = executionEngine.getToma_ROB();
				rob.getRobEntries()[b].setReady(true);
			}

			if (toma_RSentry.getInstruction().getOperationType() != OperationType.store) {
				// TODO : handled in handleEvent of CDB..confirm fine
			}

			else {// store instruction
				if (toma_RSentry.getSourceOperand2_avaliability() != 0) {
					return;
				}

				// TODO:-imp => ROB[h].value = RS[r].VK;
			}

		}
	}
}
