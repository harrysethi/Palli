/**
 * 
 */
package pipeline.multi_issue_inorder;

/**
 * @author dell
 *
 */
public class Toma_RegisterFile {

	private int[] toma_ROBentry;// Qi[]
	private boolean isBusy[];// Busy[]
	private Object values[];// Val[]

	private int registerFileSize;

	public Toma_RegisterFile(int _registerFileSize) {
		registerFileSize = _registerFileSize;
		values = new Object[registerFileSize];
		isBusy = new boolean[registerFileSize];
		toma_ROBentry = new int[registerFileSize];

		for (int i = 0; i < _registerFileSize; i++) {
			isBusy[i] = false;
			toma_ROBentry[i] = -1;
		}
	}

	public Object getValue(int index) {
		return values[index];
	}

	public void setValue(Object value, int index) {
		this.values[index] = value;
	}

	public boolean isBusy(int index) {
		return isBusy[index];
	}

	public void setBusy(boolean isBusy, int index) {
		this.isBusy[index] = isBusy;
	}

	public int getToma_ROBEntry(int index) {
		return toma_ROBentry[index];
	}

	public void setToma_ROBEntry(int toma_ROBentry, int index) {
		this.toma_ROBentry[index] = toma_ROBentry;
	}

	public void clearROBentries() {
		for (int i = 0; i < registerFileSize; i++) {
			toma_ROBentry[i] = -1;
		}
	}
}
