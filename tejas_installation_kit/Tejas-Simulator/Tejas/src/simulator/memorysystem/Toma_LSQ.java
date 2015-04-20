/**
 * 
 */
package memorysystem;

import generic.Event;
import generic.EventQueue;
import generic.PortType;
import generic.SimulationElement;
import memorysystem.Toma_LSQEntry.Toma_LSQEntryType;
import pipeline.multi_issue_inorder.Toma_ROBentry;

/**
 * @author dell
 *
 */
public class Toma_LSQ extends SimulationElement {
	CoreMemorySystem containingMemSys;

	private Toma_LSQEntry[] toma_lsqueue;
	private int tail;
	private int head;

	private int lsqSize;
	private int curSize;

	public Toma_LSQ(PortType portType, int noOfPorts, long occupancy, long latency, CoreMemorySystem containingMemSys, int lsqSize) {
		super(portType, noOfPorts, occupancy, latency, containingMemSys.getCore().getFrequency());
		this.containingMemSys = containingMemSys;
		this.lsqSize = lsqSize;
		head = -1;
		tail = -1;

		curSize = 0;

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

	public boolean isFull() {
		if (curSize >= lsqSize)
			return true;
		else
			return false;
	}

	public Toma_LSQEntry addEntry(boolean isLoad, long address, Toma_ROBentry toma_robEntry) {
		// noOfMemRequests++;//TODO: counters..chk
		Toma_LSQEntry.Toma_LSQEntryType type = (isLoad) ? Toma_LSQEntry.Toma_LSQEntryType.LOAD : Toma_LSQEntry.Toma_LSQEntryType.STORE;

		/*
		 * if (isLoad)//TODO: counters..chk NoOfLd++; else NoOfSt++;
		 */

		if (head == -1) {
			head = tail = 0;
		} else {
			tail = (tail + 1) % lsqSize;
		}

		Toma_LSQEntry toma_lsqEntry = toma_lsqueue[tail];
		if (!toma_lsqEntry.isRemoved())
			misc.Error.showErrorAndExit("entry currently in use being re-allocated");

		toma_lsqEntry.recycle();
		toma_lsqEntry.setType(type);
		toma_lsqEntry.setToma_robEntry(toma_robEntry);
		toma_lsqEntry.setAddr(address);
		this.curSize++;

		// incrementNumAccesses(1);//TODO: counters..chk

		return toma_lsqEntry;
	}

}
