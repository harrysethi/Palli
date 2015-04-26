/**
 * 
 */
package pipeline.multi_issue_inorder;

import config.SimulationConfig;
import generic.Core;
import generic.GlobalClock;
import generic.Instruction;
import generic.OperationType;
import generic.RequestType;
import memorysystem.Toma_LSQ;
import memorysystem.Toma_LSQentry;
import pipeline.FunctionalUnitType;
import pipeline.OpTypeToFUTypeMapping;

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
		if (executionEngine.isToma_stall_branchMisprediction()) {
			return;
		}

		Toma_ReservationStation rs = executionEngine.getToma_ReservationStation();

		for (Toma_ReservationStationEntry toma_RSentry : rs.getReservationStationEntries()) {

			if (toma_RSentry.isBusy() == false) {
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

			Instruction ins = toma_RSentry.getInstruction();

			if (toma_RSentry.getInstruction().getOperationType() == OperationType.load) {

				if (toma_RSentry.getSourceOperand1_availability() != 0) {
					continue;
				}

				if (toma_RSentry.isStartedExecution()) {
					continue;
				}

				if (toma_LSQentry.isAddressCalculated()) {

					if (toma_LSQ.isStoreAlreadyAvailableWithSameAddress(toma_LSQentry)) {
						continue;
					}

					boolean memReqIssued = executionEngine.multiIssueInorderCoreMemorySystem.issueRequestToL1Cache(
							RequestType.Cache_Read, toma_LSQentry.getAddress());

					if (memReqIssued == false) {
						continue;
					}

					toma_RSentry.setStartedExecution(true);
					if (SimulationConfig.debugMode) {
						System.out.println("\n" + GlobalClock.getCurrentTime() + ": "
								+ "Execute | Started Executing : " + " \n " + ins);
					}
					continue;
				}

				// address not yet calculated
				if (toma_LSQentry.isStartedCalculatingAddress()) {
					if (GlobalClock.getCurrentTime() >= toma_LSQentry.getTimeToCompleteAddressCalculation()) {
						// address calculated
						toma_LSQentry.setAddressCalculated(true);
						if (SimulationConfig.debugMode) {
							System.out.println("\n" + GlobalClock.getCurrentTime() + ": "
									+ "Execute | Address Calculated : " + " \n " + ins);
						}
					}
					continue;
				}

				calculateAddressForLoadStore(toma_RSentry, toma_LSQentry);
			}

			else if (toma_RSentry.getInstruction().getOperationType() == OperationType.store) {
				if (toma_RSentry.getSourceOperand1_availability() != 0) {
					continue;
				}

				if (toma_LSQentry.isAddressCalculated()) {
					toma_RSentry.setStartedExecution(true);
					if (SimulationConfig.debugMode) {
						System.out.println("\n" + GlobalClock.getCurrentTime() + ": "
								+ "Execute | Started Executing : " + " \n " + ins);
					}

					// setting completed execution since no event is called for store
					toma_RSentry.setCompletedExecution(true);
					if (SimulationConfig.debugMode) {
						System.out.println("\n" + GlobalClock.getCurrentTime() + ": "
								+ "Execute | Completed Executing : " + " \n " + ins);
					}
					continue;
				}

				// address not yet calculated
				if (toma_LSQentry.isStartedCalculatingAddress()) {
					if (GlobalClock.getCurrentTime() >= toma_LSQentry.getTimeToCompleteAddressCalculation()) {
						// address calculated
						toma_LSQentry.setAddressCalculated(true);
						if (SimulationConfig.debugMode) {
							System.out.println("\n" + GlobalClock.getCurrentTime() + ": "
									+ "Execute | Address Calculated : " + " \n " + ins);
						}
					}
					continue;
				}

				calculateAddressForLoadStore(toma_RSentry, toma_LSQentry);
			}

			else {// not a load/store instruction

				if (toma_RSentry.isEntryAvailableIn_RS() == false) {
					continue;
				}

				if (toma_RSentry.isStartedExecution()) {
					if (GlobalClock.getCurrentTime() >= toma_RSentry.getTimeToCompleteExecution()) {
						// execution completed
						toma_RSentry.setCompletedExecution(true);
						if (SimulationConfig.debugMode) {
							System.out.println("\n" + GlobalClock.getCurrentTime() + ": "
									+ "Execute | Completed Executing : " + " \n " + ins);
						}
					}
					continue;
				}

				// execution not yet started...checking if FU is available
				FunctionalUnitType fuType = getFUtype(toma_RSentry);

				if (isFUavailable(fuType) == false) {
					continue;
				}

				toma_RSentry.setStartedExecution(true);
				if (SimulationConfig.debugMode) {
					System.out.println("\n" + GlobalClock.getCurrentTime() + ": " + "Execute | Started Executing : "
							+ " \n " + ins);
				}

				long lat = getFUlatency(fuType);

				toma_RSentry.setTimeToCompleteExecution(GlobalClock.getCurrentTime() + lat * core.getStepSize());
			}
		}

	}

	private FunctionalUnitType getFUtype(Toma_ReservationStationEntry toma_RSentry) {
		FunctionalUnitType fuType = OpTypeToFUTypeMapping.getFUType(toma_RSentry.getInstruction().getOperationType());
		return fuType;
	}

	private boolean isFUavailable(FunctionalUnitType fuType) {
		long FURequest = 0;

		// TODO: check ye memory kyun laga hua tha pehle jabki humko load/store ke liye to memory chiye
		// & baaki mein memory hota hi ni
		// if (fuType != FunctionalUnitType.memory && fuType != FunctionalUnitType.inValid) {

		if (fuType != FunctionalUnitType.inValid) {
			FURequest = executionEngine.getExecutionCore().requestFU(fuType);
		}

		if (FURequest > 0) { // FU is not available
			return false;
		}

		return true;
	}

	private long getFUlatency(FunctionalUnitType fuType) {
		long latency = 1;

		// TODO: check ye memory kyun laga hua tha pehle jabki humko load/store ke liye to memory chiye
		// & baaki mein memory hota hi ni
		// if (fuType != FunctionalUnitType.memory && fuType != FunctionalUnitType.inValid) {

		if (fuType != FunctionalUnitType.inValid) {
			latency = executionEngine.getExecutionCore().getFULatency(fuType);
		}

		return latency;
	}

	private void calculateAddressForLoadStore(Toma_ReservationStationEntry toma_RSentry, Toma_LSQentry toma_LSQentry) {
		// TO-DO: check why condn in algo.. before address calculate ..both in case of load & store..chhado

		// calculating address
		long address = toma_RSentry.getAddress();

		FunctionalUnitType fuType = getFUtype(toma_RSentry);

		if (!isFUavailable(fuType)) {
			return;
		}

		toma_LSQentry.setStartedCalculatingAddress(true);
		Instruction ins = toma_RSentry.getInstruction();
		if (SimulationConfig.debugMode) {
			System.out.println("\n" + GlobalClock.getCurrentTime() + ": " + "Execute | Started Calculating Address : "
					+ " \n " + ins);
		}

		long lat = getFUlatency(fuType);

		toma_LSQentry.setTimeToCompleteAddressCalculation(GlobalClock.getCurrentTime() + lat * core.getStepSize());

		// TO-DO: check do we need to actually calculate the address...NO
		// address += (Long) toma_RSentry.getSourceOperand1_value();

		toma_RSentry.setAddress(address);

		toma_LSQentry.setAddress(address);
	}
}
