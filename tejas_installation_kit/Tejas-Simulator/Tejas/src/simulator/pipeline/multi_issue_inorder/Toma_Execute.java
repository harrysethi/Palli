/**
 * 
 */
package pipeline.multi_issue_inorder;

/**
 * @author dell
 *
 */
public class Toma_Execute {
	// TODO:--- check whether to extend simulation element

	MultiIssueInorderExecutionEngine executionEngine;

	public Toma_Execute(MultiIssueInorderExecutionEngine executionEngine) {
		// TODO: check do we need "super(PortType.Unlimited, -1, -1, -1, -1);"... i think hona chahiye
		this.executionEngine = executionEngine;
	}

	public void performExecute() {
		Toma_ReservationStation rs = executionEngine.getToma_ReservationStation();
		Toma_ReservationStationEntry rs_availableEntry = rs.getAvailableEntryIn_RS();

		// TODO: logic in inOrder & OOO -- sir se upar ja ra hai... check later

		// TODO: left: compute result...check something shall be required
	}
}
