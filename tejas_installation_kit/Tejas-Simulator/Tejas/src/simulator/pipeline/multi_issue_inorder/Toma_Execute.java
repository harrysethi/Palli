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

	MultiIssueInorderExecutionEngine executionEngine;
	Core core;

	public Toma_Execute(MultiIssueInorderExecutionEngine executionEngine, Core core) {
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

					OperationType opType = toma_RSentry.getInstruction().getOperationType();

					if (opType != OperationType.inValid && opType != OperationType.nop) {
						executionEngine.getCoreMemorySystem().issueRequestToToma_CDB(toma_RSentry);
					}
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

	}
}
