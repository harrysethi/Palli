/**
 * 
 */
package pipeline.multi_issue_inorder;

/**
 * @author dell
 *
 */
public class Toma_ReservationStation {
	
	int maxReservationStationSize;
	Toma_ReservationStationEntry[] reservationStationEntries;

	public Toma_ReservationStation() {

		reservationStationEntries = new Toma_ReservationStationEntry[maxReservationStationSize];
		for (int i = 0; i < maxReservationStationSize; i++) {
			reservationStationEntries[i] = new Toma_ReservationStationEntry();
		}
	}

}
