/**
 * 
 */
package pipeline.multi_issue_inorder;

import pipeline.outoforder.ReorderBufferEntry;

/**
 * @author dell
 *
 */
public class Toma_RegisterFile {

	// TODO:--- check whether to extend simulation element

	private Toma_ROBentry[] toma_ROBentry;// Qi[]
	private boolean isBusy[];// Busy[]
	private Object values[];// Val[]

	private int registerFileSize;

	public Toma_RegisterFile(int registerFileSize) {
		this.registerFileSize = registerFileSize;
		values = new Object[registerFileSize];
		isBusy = new boolean[registerFileSize];
		toma_ROBentry = new Toma_ROBentry[registerFileSize];

		/*
		 * for (int i = 0; i < registerFileSize; i++) { isBusy[i] = false; toma_ROBentry[i] = null; }
		 */
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

	public Toma_ROBentry getToma_ROBEntry(int index) {
		return toma_ROBentry[index];
	}

	public void setToma_ROBEntry(Toma_ROBentry toma_ROBentry, int index) {
		this.toma_ROBentry[index] = toma_ROBentry;
	}
}
