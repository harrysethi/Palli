/**
 * 
 */
package memorysystem;

import memorysystem.Toma_LSQentry.Toma_LSQEntryType;
import pipeline.multi_issue_inorder.Toma_ROBentry;

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

		head = -1;
		tail = -1;
		for (int i = 0; i < lsq_Size; i++) {
			toma_lsqueue[i] = new Toma_LSQentry(i);
		}

		current_Size = 0;

	}

	public boolean isFull() {
		if (current_Size >= current_Size)
			return true;
		else
			return false;
	}

	public Toma_LSQentry addLsqEntry(boolean isLoad, long address, Toma_ROBentry toma_robEntry) {

		if (head == -1) {
			head = tail = 0;
		} else {
			tail = (tail + 1) % lsq_Size;
		}

		Toma_LSQentry toma_LSQentry = toma_lsqueue[tail];

		toma_LSQentry.setAddress(address);
		toma_LSQentry.setToma_robEntry(toma_robEntry);

		Toma_LSQEntryType type = Toma_LSQEntryType.LOAD;

		if (!isLoad) {
			type = Toma_LSQEntryType.STORE;
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
}
