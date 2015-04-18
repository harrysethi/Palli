/**
 * 
 */
package pipeline.multi_issue_inorder;

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

		// TODO: wait until execution complete complete at r
		// TODO: Wait until CDB available

		Toma_ReservationStation rs = executionEngine.getToma_ReservationStation();
		Toma_ROB rob = executionEngine.getToma_ROB();
		Toma_ReservationStationEntry rs_availableEntry = rs.getAvailableEntryIn_RS();

		// TODO: added as part of testing...need to confirm
		if (rs_availableEntry == null)
			return;

		int b = rs_availableEntry.getInst_entryNumber_ROB();
		rs_availableEntry.setBusy(false);

		Toma_ReservationStationEntry[] reservationStationEntries = rs.getReservationStationEntries();

		Object result = null;// TODO: left | this shall come from somewhere
		// TODO: pallavi says: "vaise bi humein value se kuch lena dena to hai ni"

		for (Toma_ReservationStationEntry toma_RSentry : reservationStationEntries) {

			if (toma_RSentry.getSourceOperand1_avaliability() == b) {
				toma_RSentry.setSourceOperand1_value(result);
				toma_RSentry.setSourceOperand1_avaliability(0);
			}

			if (toma_RSentry.getSourceOperand2_avaliability() == b) {
				toma_RSentry.setSourceOperand2_value(result);
				toma_RSentry.setSourceOperand2_avaliability(0);
			}

		}

		rob.getRobEntries()[b].setReady(true);
		rob.getRobEntries()[b].setResultValue(result);
	}
}
