/**
 * 
 */
package memorysystem;

import pipeline.multi_issue_inorder.Toma_ROBentry;
import pipeline.multi_issue_inorder.Toma_ReservationStationEntry;

/**
 * @author dell
 *
 */
public class Toma_LSQentry {
	public enum Toma_LSQEntryType {
		LOAD, STORE
	};

	private Toma_LSQEntryType type;
	private long address;

	private boolean isOccupied;

	private boolean isStartedCalculatingAddress;
	private boolean isAddressCalculated;
	private long timeToCompleteAddressCalculation;

	private Toma_ROBentry toma_robEntry;
	private Toma_ReservationStationEntry toma_RSentry;

	private int indexInQ;

	public Toma_LSQentry(int indexInQ) {
		this.isOccupied = false;
		this.isAddressCalculated = false;
		this.toma_robEntry = null;
		this.address = -1;
		this.indexInQ = indexInQ;

		this.isAddressCalculated = false;
		this.isStartedCalculatingAddress = false;
	}

	/**
	 * @return the indexInQ
	 */
	public int getIndexInQ() {
		return indexInQ;
	}

	/**
	 * @return the isAddressCalculated
	 */
	public boolean isAddressCalculated() {
		return isAddressCalculated;
	}

	/**
	 * @param isAddressCalculated
	 *            the isAddressCalculated to set
	 */
	public void setAddressCalculated(boolean isAddressCalculated) {
		this.isAddressCalculated = isAddressCalculated;
	}

	/**
	 * @return the type
	 */
	public Toma_LSQEntryType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Toma_LSQEntryType type) {
		this.type = type;
	}

	/**
	 * @return the address
	 */
	public long getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(long address) {
		this.address = address;
	}

	/**
	 * @return the isOccupied
	 */
	public boolean isOccupied() {
		return isOccupied;
	}

	/**
	 * @param isOccupied
	 *            the isOccupied to set
	 */
	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
	}

	/**
	 * @return the toma_robEntry
	 */
	public Toma_ROBentry getToma_robEntry() {
		return toma_robEntry;
	}

	/**
	 * @param toma_robEntry
	 *            the toma_robEntry to set
	 */
	public void setToma_robEntry(Toma_ROBentry toma_robEntry) {
		this.toma_robEntry = toma_robEntry;
	}

	/**
	 * @return the toma_RSentry
	 */
	public Toma_ReservationStationEntry getToma_RSentry() {
		return toma_RSentry;
	}

	/**
	 * @param toma_RSentry
	 *            the toma_RSentry to set
	 */
	public void setToma_RSentry(Toma_ReservationStationEntry toma_RSentry) {
		this.toma_RSentry = toma_RSentry;
	}

	/**
	 * @return the isStartedCalculatingAddress
	 */
	public boolean isStartedCalculatingAddress() {
		return isStartedCalculatingAddress;
	}

	/**
	 * @param isStartedCalculatingAddress
	 *            the isStartedCalculatingAddress to set
	 */
	public void setStartedCalculatingAddress(boolean isStartedCalculatingAddress) {
		this.isStartedCalculatingAddress = isStartedCalculatingAddress;
	}

	/**
	 * @return the timeToCompleteAddressCalculation
	 */
	public long getTimeToCompleteAddressCalculation() {
		return timeToCompleteAddressCalculation;
	}

	/**
	 * @param timeToCompleteAddressCalculation
	 *            the timeToCompleteAddressCalculation to set
	 */
	public void setTimeToCompleteAddressCalculation(long timeToCompleteAddressCalculation) {
		this.timeToCompleteAddressCalculation = timeToCompleteAddressCalculation;
	}

}
