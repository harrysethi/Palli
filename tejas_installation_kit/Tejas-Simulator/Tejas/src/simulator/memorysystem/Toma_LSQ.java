/**
 * 
 */
package memorysystem;

import generic.Event;
import generic.EventQueue;
import generic.PortType;
import generic.SimulationElement;
import memorysystem.Toma_LSQEntry.Toma_LSQEntryType;

/**
 * @author dell
 *
 */
public class Toma_LSQ extends SimulationElement {
	CoreMemorySystem containingMemSys;

	private Toma_LSQEntry[] toma_lsqueue;
	protected int tail;
	protected int head;
	public int lsqSize;

	public Toma_LSQ(PortType portType, int noOfPorts, long occupancy, long latency, CoreMemorySystem containingMemSys, int lsqSize) {
		super(portType, noOfPorts, occupancy, latency, containingMemSys.getCore().getFrequency());
		this.containingMemSys = containingMemSys;
		this.lsqSize = lsqSize;
		head = -1;
		tail = -1;

		toma_lsqueue = new Toma_LSQEntry[lsqSize];
		for (int i = 0; i < lsqSize; i++) {
			Toma_LSQEntry entry = new Toma_LSQEntry(Toma_LSQEntryType.LOAD, null);
			entry.setAddr(-1);
			entry.setIndexInQ(i);
			toma_lsqueue[i] = entry;
		}
	}

	@Override
	public void handleEvent(EventQueue eventQ, Event event) {
		// TODO Auto-generated method stub

	}

}
