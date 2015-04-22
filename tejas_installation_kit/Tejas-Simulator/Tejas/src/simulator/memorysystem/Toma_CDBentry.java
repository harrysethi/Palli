/**
 * 
 */
package memorysystem;

import pipeline.multi_issue_inorder.MultiIssueInorderExecutionEngine;
import pipeline.multi_issue_inorder.Toma_ReservationStationEntry;

/**
 * @author dell
 *
 */
public class Toma_CDBentry {

	MultiIssueInorderExecutionEngine executionEngine;
	Toma_ReservationStationEntry toma_ReservationStationEntry;

	public Toma_CDBentry(MultiIssueInorderExecutionEngine executionEngine,
			Toma_ReservationStationEntry toma_ReservationStationEntry) {
		this.executionEngine = executionEngine;
		this.toma_ReservationStationEntry = toma_ReservationStationEntry;
	}

	/**
	 * @return the toma_ReservationStationEntry
	 */
	public Toma_ReservationStationEntry getToma_ReservationStationEntry() {
		return toma_ReservationStationEntry;
	}

	/**
	 * @return the executionEngine
	 */
	public MultiIssueInorderExecutionEngine getExecutionEngine() {
		return executionEngine;
	}

}
