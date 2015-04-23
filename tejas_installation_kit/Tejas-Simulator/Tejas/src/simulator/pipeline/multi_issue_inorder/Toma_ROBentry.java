/**
 * 
 */
package pipeline.multi_issue_inorder;

import generic.Instruction;
import memorysystem.Toma_LSQentry;

/**
 * @author dell
 *
 */
public class Toma_ROBentry {

	private Instruction instruction; // Inst
	private boolean isBusy;
	private boolean isReady;
	private Object resultValue; // Val
	private int destinationRegNumber; // Dst

	private Toma_LSQentry toma_lsqEntry = null;

	public Toma_ROBentry() {
		this.destinationRegNumber = -1;
	}

	public Instruction getInstruction() {
		return instruction;
	}

	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}

	public boolean isBusy() {
		return isBusy;
	}

	public void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}

	/**
	 * @return the destinationRegNumber
	 */
	public int getDestinationRegNumber() {
		return destinationRegNumber;
	}

	/**
	 * @param destinationRegNumber
	 *            the destinationRegNumber to set
	 */
	public void setDestinationRegNumber(int destinationRegNumber) {
		this.destinationRegNumber = destinationRegNumber;
	}

	/**
	 * @return the resultValue
	 */
	public Object getResultValue() {
		return resultValue;
	}

	/**
	 * @param resultValue
	 *            the resultValue to set
	 */
	public void setResultValue(Object resultValue) {
		this.resultValue = resultValue;
	}

	/**
	 * @return the toma_lsqEntry
	 */
	public Toma_LSQentry getToma_lsqEntry() {
		return toma_lsqEntry;
	}

	/**
	 * @param toma_lsqEntry
	 *            the toma_lsqEntry to set
	 */
	public void setToma_lsqEntry(Toma_LSQentry toma_lsqEntry) {
		this.toma_lsqEntry = toma_lsqEntry;
	}

}
