/**
 * 
 */
package memorysystem;

import pipeline.multi_issue_inorder.Toma_ROBentry;

/**
 * @author dell
 *
 */
public class Toma_LSQEntry_bk {
	public enum Toma_LSQEntryType {
		LOAD, STORE
	};

	private int indexInQ;
	private Toma_LSQEntryType type;

	private long addr;

	private boolean valid;
	private boolean issued;
	private boolean forwarded;// Whether the load has got its value or not //TODO: check if required...

	private boolean removed; // If the entry has been committed and removed from the LSQ //TODO: check if required...

	private Toma_ROBentry toma_robEntry;

	public Toma_LSQEntry_bk(Toma_LSQEntryType type, Toma_ROBentry robEntry) {
		this.type = type;
		this.toma_robEntry = robEntry;
		valid = false;
		issued = false;
		forwarded = false;
		removed = true;
	}

	public void recycle() {
		toma_robEntry = null;
		valid = false;
		issued = false;
		forwarded = false;
		removed = false;
	}

	/**
	 * @return the indexInQ
	 */
	public int getIndexInQ() {
		return indexInQ;
	}

	/**
	 * @param indexInQ
	 *            the indexInQ to set
	 */
	public void setIndexInQ(int indexInQ) {
		this.indexInQ = indexInQ;
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
	 * @return the addr
	 */
	public long getAddr() {
		return addr;
	}

	/**
	 * @param addr
	 *            the addr to set
	 */
	public void setAddr(long addr) {
		this.addr = addr;
	}

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * @param valid
	 *            the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	/**
	 * @return the issued
	 */
	public boolean isIssued() {
		return issued;
	}

	/**
	 * @param issued
	 *            the issued to set
	 */
	public void setIssued(boolean issued) {
		this.issued = issued;
	}

	/**
	 * @return the forwarded
	 */
	public boolean isForwarded() {
		return forwarded;
	}

	/**
	 * @param forwarded
	 *            the forwarded to set
	 */
	public void setForwarded(boolean forwarded) {
		this.forwarded = forwarded;
	}

	/**
	 * @return the removed
	 */
	public boolean isRemoved() {
		return removed;
	}

	/**
	 * @param removed
	 *            the removed to set
	 */
	public void setRemoved(boolean removed) {
		this.removed = removed;
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

}
