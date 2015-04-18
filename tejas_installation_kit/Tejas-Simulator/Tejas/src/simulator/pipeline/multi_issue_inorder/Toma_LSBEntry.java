/**
 * 
 */
package pipeline.multi_issue_inorder;

/**
 * @author dell
 *
 */
public class Toma_LSBEntry {
	public enum LSQEntryType {
		LOAD, STORE
	};

	private int indexInQ;
	private LSQEntryType type;

	private long addr;

	private boolean valid;
	private boolean issued;
	private boolean forwarded;// Whether the load has got its value or not //TODO: check if required...

	private boolean removed; // If the entry has been committed and removed from the LSQ //TODO: check if required...

	private Toma_ROBentry toma_robEntry;

	public Toma_LSBEntry(LSQEntryType type, Toma_ROBentry robEntry) {
		this.type = type;
		this.toma_robEntry = robEntry;
		valid = false;
		issued = false;
		forwarded = false;
		removed = true;
	}
}
