/**
 * 
 */
package pipeline.multi_issue_inorder;

/**
 * @author dell
 *
 */
public class Toma_RegisterFile {

	// TODO:--- check whether to extend simulation element

	// private Toma_ROBentry[] toma_ROBentry;// Qi[] //TODO: we are using index rather than this (though this is what is there in OOO)
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
			toma_ROBentry[i] = -1;// TODO: check initializing here with -1 is fine
		}
	}

	public Object getValue(int index) {
		return values[index];
	}

	public void setValue(Object value, int index) {
		this.values[index] = value;
	}

	public boolean isBusy(int index) {
		// TODO : checko "incrementNumAccesses(1);"
		return isBusy[index];
	}

	public void setBusy(boolean isBusy, int index) {
		this.isBusy[index] = isBusy;
		// TODO : checko if required
		/*
		 * if(isBusy == true) { incrementNumAccesses(1); }
		 */
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
