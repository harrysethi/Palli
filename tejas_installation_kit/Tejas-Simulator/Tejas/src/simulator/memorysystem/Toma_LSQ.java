/**
 * 
 */
package memorysystem;

import generic.GlobalClock;
import config.SimulationConfig;
import memorysystem.Toma_LSQentry.Toma_LSQEntryType;
import pipeline.multi_issue_inorder.Toma_ROBentry;
import pipeline.multi_issue_inorder.Toma_ReservationStationEntry;

/**
 * @author dell
 *
 */
public class Toma_LSQ {

	Toma_LSQentry toma_lsqueue[];

	private int head;
	private int tail;

	private int lsq_Size;
	private int current_Size;

	public Toma_LSQ(int lsq_Size) {
		this.lsq_Size = lsq_Size;
		toma_lsqueue = new Toma_LSQentry[lsq_Size];

		head = -1;
		tail = -1;
		for (int i = 0; i < lsq_Size; i++) {
			toma_lsqueue[i] = new Toma_LSQentry(i);
		}

		current_Size = 0;

	}

	public boolean isFull() {
		if (current_Size >= lsq_Size)
			return true;
		else
			return false;
	}

	public Toma_LSQentry addLsqEntry(boolean isLoad, long address, Toma_ROBentry toma_robEntry,
			Toma_ReservationStationEntry toma_RSentry) {

		if (head == -1) {
			head = tail = 0;
		} else {
			tail = (tail + 1) % lsq_Size;
		}

		Toma_LSQentry toma_LSQentry = toma_lsqueue[tail];

		toma_LSQentry.setAddress(address);
		toma_LSQentry.setToma_robEntry(toma_robEntry);
		toma_LSQentry.setToma_RSentry(toma_RSentry);

		Toma_LSQEntryType type = Toma_LSQEntryType.LOAD;

		if (!isLoad) {
			type = Toma_LSQEntryType.STORE;
		}

		if (toma_LSQentry.isOccupied()) {
			misc.Error.showErrorAndExit("entry currently in use being re-allocated");
		}

		toma_LSQentry.setType(type);
		toma_LSQentry.setOccupied(true);

		current_Size++;
		return toma_LSQentry;
	}

	public boolean isStoreAlreadyAvailableWithSameAddress(Toma_LSQentry toma_LSQentry_load) {
		for (int index = head; index < toma_LSQentry_load.getIndexInQ(); index = (index + 1) % lsq_Size) {

			Toma_LSQentry entry = toma_lsqueue[index];

			if (entry.getType() == Toma_LSQEntryType.LOAD) {
				continue;
			}

			if (entry.isAddressCalculated() == false) {
				// the address may be same when calculated, thus returning true
				return true;
			}

			if (entry.getAddress() == toma_LSQentry_load.getAddress()) {
				return true;
			}

		}
		return false;
	}

	public void handleMemoryResponse(long address, int indexInQ) {

		Toma_LSQentry toma_LSQentry = this.toma_lsqueue[indexInQ];
		Toma_ReservationStationEntry toma_RSentry = toma_LSQentry.getToma_RSentry();
		toma_RSentry.setCompletedExecution(true);
		if (SimulationConfig.debugMode) {
			System.out.println("\n" + GlobalClock.getCurrentTime() + ": "
					+ "Execute (LSQ) | Completed Executing (index- " + indexInQ + "): " + "\n "
					+ toma_RSentry.getInstruction());
		}

		/*
		 * int index = this.head;
		 * 
		 * for (int i = 0; i < this.current_Size; i++) {
		 * 
		 * toma_LSQentry = this.toma_lsqueue[index];
		 * 
		 * Toma_ReservationStationEntry toma_RSentry = toma_LSQentry.getToma_RSentry();
		 * 
		 * if (toma_LSQentry.getType() == Toma_LSQEntryType.LOAD && toma_RSentry.isStartedExecution() &&
		 * !toma_RSentry.isCompletedExecution() && toma_LSQentry.isAddressCalculated() && toma_LSQentry.getAddress() ==
		 * address && toma_LSQentry.isOccupied()) { toma_RSentry.setCompletedExecution(true); if
		 * (SimulationConfig.debugMode) { System.out.println("\n" + GlobalClock.getCurrentTime() + ": " +
		 * "Execute (LSQ) | Completed Executing : " + " " + toma_RSentry.getInstruction()); } return; }
		 * 
		 * index = (index + 1) % lsq_Size; }
		 */
	}

	public void removeEntry(Toma_LSQentry toma_LSQentry) {

		if (toma_LSQentry.isOccupied() == false) {
			return;
		}

		toma_LSQentry.setOccupied(false);
		toma_LSQentry.setAddress(-1);
		toma_LSQentry.setStartedCalculatingAddress(false);
		toma_LSQentry.setAddressCalculated(false);
		toma_LSQentry.setTimeToCompleteAddressCalculation(0);
		toma_LSQentry.setToma_robEntry(null);
		toma_LSQentry.setToma_RSentry(null);

		if (head == tail) {
			head = -1;
			tail = -1;
		}

		else {
			head = (head + 1) % lsq_Size;
		}

		current_Size--;

	}
}
