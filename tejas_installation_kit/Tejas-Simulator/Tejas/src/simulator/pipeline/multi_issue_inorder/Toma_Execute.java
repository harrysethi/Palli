/**
 * 
 */
package pipeline.multi_issue_inorder;

import pipeline.FunctionalUnitType;
import pipeline.OpTypeToFUTypeMapping;
import generic.Core;
import generic.GlobalClock;
import generic.OperationType;

/**
 * @author dell
 *
 */
public class Toma_Execute {
	// TODO:--- check whether to extend simulation element

	MultiIssueInorderExecutionEngine executionEngine;
	Core core;

	public Toma_Execute(MultiIssueInorderExecutionEngine executionEngine, Core core) {
		// TODO: check do we need "super(PortType.Unlimited, -1, -1, -1, -1);"... i think hona chahiye
		this.executionEngine = executionEngine;
		this.core = core;
	}

	public void performExecute() {
		Toma_ReservationStation rs = executionEngine.getToma_ReservationStation();

		for (Toma_ReservationStationEntry toma_RSentry : rs.getReservationStationEntries()) {

			if (!toma_RSentry.isBusy()) {
				// there is no point of executing a non-busy RS
				continue;
			}

			if (!rs.isEntryAvailableIn_RS(toma_RSentry)) {
				continue;
			}

			if (toma_RSentry.isCompletedExecution()) {
				continue;
			}

			if (toma_RSentry.isStartedExecution()) {
				if (GlobalClock.getCurrentTime() >= toma_RSentry.getTimeToCompleteExecution()) {
					// execution completed
					toma_RSentry.setCompletedExecution(true);

					if (toma_RSentry.getInstruction().getOperationType() != OperationType.inValid)
						executionEngine.getCoreMemorySystem().issueRequestToToma_CDB(toma_RSentry);
				}
				continue;
			}

			else {// not yet started execution

				// checking if FU is available
				FunctionalUnitType fuType = OpTypeToFUTypeMapping.getFUType(toma_RSentry.getInstruction()
						.getOperationType());

				long FURequest = 0;
				if (fuType != FunctionalUnitType.memory && fuType != FunctionalUnitType.inValid) {
					FURequest = executionEngine.getExecutionCore().requestFU(fuType);
				}

				if (FURequest > 0) { // FU is not available
					continue;
				}

				toma_RSentry.setStartedExecution(true);

				long lat = 1;

				if (fuType != FunctionalUnitType.memory && fuType != FunctionalUnitType.inValid) {
					lat = executionEngine.getExecutionCore().getFULatency(fuType);
				}

				else {
					// TODO: check if something required here
				}

				toma_RSentry.setTimeToCompleteExecution(GlobalClock.getCurrentTime() + lat * core.getStepSize());
			}
		}

		/*
		 * Toma_ReservationStationEntry rs_availableEntry = rs.getAvailableEntryIn_RS();
		 * 
		 * if (rs_availableEntry == null) { return; }
		 */
		/*
		 * if (rs_availableEntry.isCompletedExecution()) { return; }
		 * 
		 * if (rs_availableEntry.isStartedExecution()) { if (GlobalClock.getCurrentTime() >=
		 * rs_availableEntry.getTimeToCompleteExecution()) { rs_availableEntry.setCompletedExecution(true); //
		 * TODO:duplicate issue request to CDB } return; }
		 * 
		 * // not yet started execution rs_availableEntry.setStartedExecution(true);
		 * 
		 * FunctionalUnitType fuType = OpTypeToFUTypeMapping.getFUType(rs_availableEntry.getOperationType()); long lat =
		 * 1;
		 * 
		 * if (fuType != FunctionalUnitType.memory && fuType != FunctionalUnitType.inValid) { lat =
		 * executionEngine.getExecutionCore().getFULatency(fuType);
		 * 
		 * } else { // TODO:duplicate check if something required here }
		 * 
		 * rs_availableEntry.setTimeToCompleteExecution(GlobalClock.getCurrentTime() + lat * core.getStepSize());
		 * 
		 * // TODO: logic in inOrder & OOO -- sir se upar ja ra hai... check later
		 * 
		 * // TODO: left: compute result...check something shall be required
		 */}
}
