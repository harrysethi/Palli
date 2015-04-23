/**
 * 
 */
package pipeline.multi_issue_inorder;

import memorysystem.Toma_LSQ;
import memorysystem.Toma_LSQentry;
import pipeline.FunctionalUnitType;
import pipeline.OpTypeToFUTypeMapping;
import generic.Core;
import generic.GlobalClock;
import generic.Instruction;
import generic.OperationType;
import generic.RequestType;

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

			if (toma_RSentry.isCompletedExecution()) {
				continue;
			}

			Toma_LSQ toma_LSQ = executionEngine.getCoreMemorySystem().getToma_LSQ();
			Toma_ROB toma_ROB = executionEngine.getToma_ROB();
			Toma_LSQentry toma_LSQentry = toma_ROB.getRobEntries()[toma_RSentry.getInst_entryNumber_ROB()]
					.getToma_lsqEntry();

			if (toma_RSentry.getInstruction().getOperationType() == OperationType.load) {
				if (toma_RSentry.isStartedExecution()) {
					continue;
				}

				if (toma_RSentry.getSourceOperand1_avaliability() != 0) {
					continue;
				}

				calculateAddressForLoadStore(toma_RSentry, toma_LSQentry);

				if (toma_LSQentry.isAddressCalculated() == false
						|| toma_LSQ.isStoreAlreadyAvailableWithSameAddress(toma_LSQentry)) {
					continue;
				}

				boolean memReqIssued = executionEngine.multiIssueInorderCoreMemorySystem.issueRequestToL1Cache(
						RequestType.Cache_Read, toma_LSQentry.getAddress());

				if (memReqIssued == false) {
					continue;
				}

				toma_RSentry.setStartedExecution(true);

				// TODO
			}

			else if (toma_RSentry.getInstruction().getOperationType() == OperationType.store) {
				if (toma_RSentry.getSourceOperand1_avaliability() != 0) {
					continue;
				}

				toma_RSentry.setStartedExecution(true);

				calculateAddressForLoadStore(toma_RSentry, toma_LSQentry);

				// setting completed execution since no event is called for store
				toma_RSentry.setCompletedExecution(true);
			}

			else {// not a load/store instruction

				if (!toma_RSentry.isEntryAvailableIn_RS()) {
					continue;
				}

				if (toma_RSentry.isStartedExecution()) {
					if (GlobalClock.getCurrentTime() >= toma_RSentry.getTimeToCompleteExecution()) {
						// execution completed
						toma_RSentry.setCompletedExecution(true);
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

	private void calculateAddressForLoadStore(Toma_ReservationStationEntry toma_RSentry, Toma_LSQentry toma_LSQentry) {
		// TODO: check why condn in algo.. before address calculate ..both in case of load & store
		// calculating address
		long address = toma_RSentry.getAddress();
		address += (Long) toma_RSentry.getSourceOperand1_value();

		toma_RSentry.setAddress(address);

		toma_LSQentry.setAddress(address);
		toma_LSQentry.setAddressCalculated(true);
	}
}
