/**
 * 
 */
package memorysystem;

import pipeline.multi_issue_inorder.Toma_ROB;
import pipeline.multi_issue_inorder.Toma_ReservationStation;
import pipeline.multi_issue_inorder.Toma_ReservationStationEntry;
import generic.Event;
import generic.EventQueue;
import generic.PortType;
import generic.SimulationElement;

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
		Toma_ReservationStationEntry toma_RSentry = toma_CDBentry.getToma_ReservationStationEntry();
		Toma_ReservationStation rs = toma_CDBentry.getExecutionEngine().getToma_ReservationStation();
		Toma_ROB rob = toma_CDBentry.getExecutionEngine().getToma_ROB();

		int b = toma_RSentry.getInst_entryNumber_ROB();
		toma_RSentry.setBusy(false);
		toma_RSentry.setCompletedExecution(false);
		toma_RSentry.setStartedExecution(false);
		toma_RSentry.setSourceOperand1_avaliability(-1);
		toma_RSentry.setSourceOperand2_avaliability(-1);

		Toma_ReservationStationEntry[] reservationStationEntries = rs.getReservationStationEntries();

		Object result = null;
		// TODO: left | this shall come from somewhere..vaise shall not be required..values se kuch lena dena ni hai

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
		rob.getRobEntries()[b].setResultValue(result);
	}

}
