/**
 * 
 */
package memorysystem;

import generic.Core;
import generic.Event;
import generic.EventQueue;
import generic.GlobalClock;
import generic.Instruction;
import generic.PortType;
import generic.SimulationElement;
import pipeline.multi_issue_inorder.Toma_ROB;
import pipeline.multi_issue_inorder.Toma_ReservationStation;
import pipeline.multi_issue_inorder.Toma_ReservationStationEntry;
import config.SimulationConfig;

/**
 * @author dell
 *
 */
public class Toma_CDB extends SimulationElement {

	public Toma_CDB(PortType portType, int noOfPorts, long occupancy, long latency, CoreMemorySystem containingMemSys) {
		super(portType, noOfPorts, occupancy, latency, containingMemSys.getCore().getFrequency());
	}

	@Override
	public void handleEvent(EventQueue eventQ, Event event) {
		Toma_CDBentry toma_CDBentry = ((Toma_CDBevent) event).getToma_CDBentry();
		// MultiIssueInorderExecutionEngine executionEngine = ((Toma_CDBevent) event).getExecutionEngine();
		// Core core = ((Toma_CDBevent) event).getCore();

		Toma_ReservationStationEntry toma_RSentry = toma_CDBentry.getToma_ReservationStationEntry();
		Toma_ReservationStation rs = toma_CDBentry.getExecutionEngine().getToma_ReservationStation();
		Toma_ROB rob = toma_CDBentry.getExecutionEngine().getToma_ROB();
		Instruction ins = toma_RSentry.getInstruction();

		int b = toma_RSentry.getInst_entryNumber_ROB();
		toma_RSentry.clearEntry();

		if (SimulationConfig.debugMode) {
			System.out.println("WriteResult (CDB) | RS free & ROB ready : " + " \n " + ins);
		}

		Toma_ReservationStationEntry[] reservationStationEntries = rs.getReservationStationEntries();

		Object result = null;
		// TO-DO: left | this shall come from somewhere..vaise shall not be required..values se kuch lena dena ni hai

		for (Toma_ReservationStationEntry toma_RSentryentry : reservationStationEntries) {

			if (!toma_RSentryentry.isBusy()) {
				continue;
			}

			Instruction _ins = toma_RSentryentry.getInstruction();

			if (toma_RSentryentry.getSourceOperand1_availability() == b) {
				// Toma_RegisterFile toma_RF_source1 = executionEngine.getToma_RegisterFile(ins.getSourceOperand1());
				// int register_source1 = (int) ins.getSourceOperand1().getValue();

				// TODO: remove after testing
				/*
				 * if (toma_RF_source1.isBusy(register_source1)) { continue; }
				 */

				toma_RSentryentry.setSourceOperand1_value(result);
				toma_RSentryentry.setSourceOperand1_availability(0);

				if (SimulationConfig.debugMode) {
					System.out.println("WriteResult | source1 now available : " + " \n " + _ins);
				}

				/*
				 * if (ins.getOperationType() == OperationType.xchg) {
				 * toma_RF_source1.setToma_ROBEntry(toma_RSentryentry.getInst_entryNumber_ROB(), register_source1); //
				 * TODO: remove after testing // toma_RF_source1.setBusy(true, register_source1); //break; }
				 */
			}

			if (toma_RSentryentry.getSourceOperand2_availability() == b) {
				// Toma_RegisterFile toma_RF_source2 = executionEngine.getToma_RegisterFile(ins.getSourceOperand2());
				// int register_source2 = (int) ins.getSourceOperand2().getValue();

				// TODO: remove after testing
				/*
				 * if (toma_RF_source2.isBusy(register_source2)) { continue; }
				 */

				toma_RSentryentry.setSourceOperand2_value(result);
				toma_RSentryentry.setSourceOperand2_availability(0);

				if (SimulationConfig.debugMode) {
					System.out.println("WriteResult | source2 now available : " + " \n " + _ins);
				}

				/*
				 * if (ins.getOperationType() == OperationType.xchg) {
				 * toma_RF_source2.setToma_ROBEntry(toma_RSentryentry.getInst_entryNumber_ROB(), register_source2); //
				 * TODO: remove after testing // toma_RF_source2.setBusy(true, register_source2); break; }
				 */
			}
		}

		rob.getRobEntries()[b].setReady(true);
		rob.getRobEntries()[b].setResultValue(result);
	}

}
