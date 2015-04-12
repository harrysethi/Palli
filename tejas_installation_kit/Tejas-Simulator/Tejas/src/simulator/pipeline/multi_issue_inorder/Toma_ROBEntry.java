/**
 * 
 */
package pipeline.multi_issue_inorder;

import generic.Instruction;

/**
 * @author dell
 *
 */
public class Toma_ROBEntry {

	private Instruction instruction; // Inst
	// TODO:---- humein poore instruction ki zaroorat hai kya.. ya fer operationType shall work
	private boolean isBusy;
	private boolean isReady;
	private int resultValue; // Val
	private int destinationRegNumber; // Dst

	// TODO:----..check here using regNum may be incorrect

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

	public int getResultValue() {
		return resultValue;
	}

	public void setResultValue(int resultValue) {
		this.resultValue = resultValue;
	}

	public int getDestinationRegNumber() {
		return destinationRegNumber;
	}

	public void setDestinationRegNumber(int destinationRegNumber) {
		this.destinationRegNumber = destinationRegNumber;
	}

}
