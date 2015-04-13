/**
 * 
 */
package pipeline.multi_issue_inorder;

/**
 * @author dell
 *
 */
public class Toma_ReservationStation {

	private int maxReservationStationSize;
	private Toma_ReservationStationEntry[] reservationStationEntries;

	public Toma_ReservationStation() {

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

}
