/**
 * 
 */
package pipeline.multi_issue_inorder;

import generic.Core;

/**
 * @author dell
 *
 */
public class Toma_ReservationStation {

	// TODO:--- check whether to extend simulation element

	private int maxReservationStationSize;
	private Toma_ReservationStationEntry[] reservationStationEntries;

	public Toma_ReservationStation(Core core) {
		this.maxReservationStationSize = core.getToma_rsBufferSize();
		reservationStationEntries = new Toma_ReservationStationEntry[maxReservationStationSize];
		for (int i = 0; i < maxReservationStationSize; i++) {
			reservationStationEntries[i] = new Toma_ReservationStationEntry();
		}
	}

	/**
	 * @return the reservationStationEntries
	 */
	public Toma_ReservationStationEntry[] getReservationStationEntries() {
		return reservationStationEntries;
	}

	// returns null if no freeEntry found
	public Toma_ReservationStationEntry getFreeEntryIn_RS() {
		for (Toma_ReservationStationEntry toma_ReservationStationEntry : reservationStationEntries) {
			if (!toma_ReservationStationEntry.isBusy()) {
				return toma_ReservationStationEntry;
			}
		}
		return null;
	}

	// returns the entry whose both operands are available
	public Toma_ReservationStationEntry getAvailableEntryIn_RS() {
		for (Toma_ReservationStationEntry toma_RSentry : reservationStationEntries) {
			if (toma_RSentry.getSourceOperand1_avaliability() == 0 && toma_RSentry.getSourceOperand2_avaliability() == 0) {
				return toma_RSentry;
			}
		}
		return null;
	}

}
