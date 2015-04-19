/**
 * 
 */
package memorysystem;

import generic.Event;
import generic.EventQueue;
import generic.PortType;
import generic.SimulationElement;

/**
 * @author dell
 *
 */
public class Toma_CDB extends SimulationElement {

	CoreMemorySystem containingMemSys;

	int data_to_be_xfrd;
	int rob_tag; // TODO:---: is this actually int

	public Toma_CDB(PortType portType, int noOfPorts, long occupancy, long latency, CoreMemorySystem containingMemSys) {
		super(portType, noOfPorts, occupancy, latency, containingMemSys.getCore().getFrequency());
		this.containingMemSys = containingMemSys;
	}

	@Override
	public void handleEvent(EventQueue eventQ, Event event) {
		// TODO Auto-generated method stub

	}

}
