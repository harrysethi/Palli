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
	// TODO:--- check whether to extend simulation element

	MultiIssueInorderExecutionEngine executionEngine;

	public Toma_WriteResult(MultiIssueInorderExecutionEngine executionEngine) {
		// TODO: check do we need "super(PortType.Unlimited, -1, -1, -1, -1);"... i think hona chahiye
		this.executionEngine = executionEngine;
	}

	public void performWriteResult() {
		// TODO: logic in inOrder & OOO -- sir se upar ja ra hai... check later

		// TO-DO: wait until execution complete complete at r...ho gya most probably

		/*
		 * boolean isStoreInstr = true;// TO-DO:imp-this shall be set
		 */
		Toma_ReservationStation rs = executionEngine.getToma_ReservationStation();
		Toma_ROB rob = executionEngine.getToma_ROB();
		/*
		 * Toma_ReservationStationEntry rs_r = null;// = rs.getAvailableEntryIn_RS();// TO-DO: IMP-yahaan pe available entry ni aana chiye shayad
		 */
		for (Toma_ReservationStationEntry toma_RSentry : rs.getReservationStationEntries()) {

			if (!toma_RSentry.isCompletedExecution()) {
				continue;
			}

			if (toma_RSentry.getInstruction().getOperationType() != OperationType.store) {
				//TODO : handled in handleEvent of CDB..confirm fine
				
				// TODO: Wait until CDB available

				/*int b = toma_RSentry.getInst_entryNumber_ROB();
				toma_RSentry.setBusy(false);
				toma_RSentry.setCompletedExecution(false);
				toma_RSentry.setStartedExecution(false);

				Toma_ReservationStationEntry[] reservationStationEntries = rs.getReservationStationEntries();

				Object result = null;// TODO: left | this shall come from somewhere
				// TODO: pallavi says: "vaise bi humein value se kuch lena dena to hai ni"

				for (Toma_ReservationStationEntry toma_RSentryentry : reservationStationEntries) {

					if (toma_RSentryentry.getSourceOperand1_avaliability() == b) {
						toma_RSentryentry.setSourceOperand1_value(result);
						toma_RSentryentry.setSourceOperand1_avaliability(0);
					}

					if (toma_RSentryentry.getSourceOperand2_avaliability() == b) {
						toma_RSentryentry.setSourceOperand2_value(result);
						toma_RSentryentry.setSourceOperand2_avaliability(0);
					}

				}

				rob.getRobEntries()[b].setReady(true);
				rob.getRobEntries()[b].setResultValue(result);*/
			}

			else {// store instruction
				if (toma_RSentry.getSourceOperand2_avaliability() != 0) {
					return;
				}

				// TODO:-imp => ROB[h].value = RS[r].VK;
			}

		}

		/*
		 * if (!isStoreInstr) {
		 * 
		 * // TODO:duplicate Wait until CDB available
		 * 
		 * // TODO:duplicate added as part of testing...need to confirm if (rs_r == null) return;
		 * 
		 * int b = rs_r.getInst_entryNumber_ROB(); rs_r.setBusy(false);
		 * 
		 * Toma_ReservationStationEntry[] reservationStationEntries = rs.getReservationStationEntries();
		 * 
		 * Object result = null;// TODO:duplicate left | this shall come from somewhere // TODO:duplicate pallavi says: "vaise bi humein value se kuch lena dena to hai ni"
		 * 
		 * for (Toma_ReservationStationEntry toma_RSentry : reservationStationEntries) {
		 * 
		 * if (toma_RSentry.getSourceOperand1_avaliability() == b) { toma_RSentry.setSourceOperand1_value(result); toma_RSentry.setSourceOperand1_avaliability(0); }
		 * 
		 * if (toma_RSentry.getSourceOperand2_avaliability() == b) { toma_RSentry.setSourceOperand2_value(result); toma_RSentry.setSourceOperand2_avaliability(0); }
		 * 
		 * }
		 * 
		 * rob.getRobEntries()[b].setReady(true); rob.getRobEntries()[b].setResultValue(result); }
		 * 
		 * else {// Store instruction if (rs_r.getSourceOperand2_avaliability() != 0) { return; }
		 * 
		 * // TODO:duplicate-imp => ROB[h].value = RS[r].VK; }
		 */
	}
}
