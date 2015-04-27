package memorysystem;

import generic.Event;
import generic.EventQueue;
import generic.RequestType;
import generic.SimulationElement;

public class AddressCarryingEvent extends Event implements Cloneable
{
	private long address;
	public long event_id;
	public int hopLength;
	public int dn_status=-1; //-1=initial, 1=broadcast, 2=hit, 3=miss
	public Event parentEvent=null;
	public int indexInQ = -10;
	
	public AddressCarryingEvent(EventQueue eventQ, long eventTime,
			SimulationElement requestingElement,
			SimulationElement processingElement,
			RequestType requestType, long address, int indexInQ) {
		super(eventQ, eventTime, requestingElement, processingElement,
				requestType, -1);
		this.address = address;
		this.indexInQ = indexInQ;
	}
	
	public AddressCarryingEvent(int indexInQ)
	{
		super(null, -1, null, null, RequestType.Cache_Read, -1);
		this.address = -1;
		this.indexInQ = indexInQ;
	}
	
	public AddressCarryingEvent(long eventId, EventQueue eventQ, long eventTime,
			SimulationElement requestingElement,
			SimulationElement processingElement,
			RequestType requestType, long address,int coreId, int indexInQ) {
		super(eventQ, eventTime, requestingElement, processingElement,
				requestType, coreId);
		this.event_id = eventId;
		this.address = address;
		this.indexInQ = indexInQ;
	}
	
	public AddressCarryingEvent updateEvent(EventQueue eventQ, long eventTime, 
			SimulationElement requestingElement,
			SimulationElement processingElement,
			RequestType requestType, long address,int coreId) {
		this.address = address;
		this.coreId = coreId;
		return (AddressCarryingEvent)this.update(eventQ, eventTime, requestingElement, processingElement, requestType);
	}
	
	public AddressCarryingEvent updateEvent(EventQueue eventQ, long eventTime, 
			SimulationElement requestingElement,
			SimulationElement processingElement,
			RequestType requestType) {
		return (AddressCarryingEvent) this.update(eventQ, eventTime, requestingElement, processingElement, requestType);
	}
	
	public long getAddress() {
		return address;
	}

	public void setAddress(long address) {
		this.address = address;
	}
	
	public void dump()
	{
		System.out.println(coreId + " : " + requestType + " : " + requestingElement + " : " + processingElement + " : " + eventTime + " : " + address);
	}
	
	public String toString(){
		String s = (coreId + " req : " + requestType + " reqE : " + requestingElement + " proE : " + processingElement + " evT : " + eventTime + " addr : " + address + " # " + serializationID); 
		return s;
	}

}
