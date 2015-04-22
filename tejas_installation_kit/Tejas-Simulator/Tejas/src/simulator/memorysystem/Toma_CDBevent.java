package memorysystem;

import generic.Event;
import generic.EventQueue;
import generic.RequestType;
import generic.SimulationElement;

public class Toma_CDBevent extends Event implements Cloneable {

	Toma_CDBentry toma_CDBentry;

	public Toma_CDBevent(EventQueue eventQ, long eventTime, SimulationElement requestingElement,
			SimulationElement processingElement, RequestType requestType, Toma_CDBentry toma_CDBentry) {
		super(eventQ, eventTime, requestingElement, processingElement, requestType, -1);
		this.toma_CDBentry = toma_CDBentry;
	}

	/**
	 * @return the toma_CDBentry
	 */
	public Toma_CDBentry getToma_CDBentry() {
		return toma_CDBentry;
	}

}
