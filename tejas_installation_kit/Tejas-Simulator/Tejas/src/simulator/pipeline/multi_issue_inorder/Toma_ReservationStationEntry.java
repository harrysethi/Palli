/**
 * 
 */
package pipeline.multi_issue_inorder;

import generic.Instruction;

/**
 * @author dell
 *
 */
public class Toma_ReservationStationEntry {

	private Instruction instruction;// Op
	private boolean isBusy;
	private int inst_entryNumber_ROB; // Qi

	private int sourceOperand1_avaliability;// Qj
	private int sourceOperand2_avaliability;// Qk

	// TO-DO:-------check if value actually required
	private Object sourceOperand1_value;// Vj
	private Object sourceOperand2_value;// Vk

	private long address; // A

	private boolean isStartedExecution;
	private boolean isCompletedExecution;
	private long timeToCompleteExecution;
	
	public Toma_ReservationStationEntry() {
		isBusy = false;

		this.sourceOperand1_avaliability = -1;
		this.sourceOperand2_avaliability = -1;
		this.inst_entryNumber_ROB = -1;
		this.isStartedExecution = false;
		this.isCompletedExecution = false;
	}

	/**
	 * @return the timeToCompleteExecution
	 */
	public long getTimeToCompleteExecution() {
		return timeToCompleteExecution;
	}

	/**
	 * @param timeToCompleteExecution
	 *            the timeToCompleteExecution to set
	 */
	public void setTimeToCompleteExecution(long timeToCompleteExecution) {
		this.timeToCompleteExecution = timeToCompleteExecution;
	}

	/**
	 * @return the isStartedExecution
	 */
	public boolean isStartedExecution() {
		return isStartedExecution;
	}

	/**
	 * @param isStartedExecution
	 *            the isStartedExecution to set
	 */
	public void setStartedExecution(boolean isStartedExecution) {
		this.isStartedExecution = isStartedExecution;
	}

	/**
	 * @return the isCompletedExecution
	 */
	public boolean isCompletedExecution() {
		return isCompletedExecution;
	}

	/**
	 * @param isCompletedExecution
	 *            the isCompletedExecution to set
	 */
	public void setCompletedExecution(boolean isCompletedExecution) {
		this.isCompletedExecution = isCompletedExecution;
	}

	/**
	 * @return the instruction
	 */
	public Instruction getInstruction() {
		return instruction;
	}

	/**
	 * @param instruction
	 *            the instruction to set
	 */
	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}

	/**
	 * @return the isBusy
	 */
	public boolean isBusy() {
		return isBusy;
	}

	/**
	 * @param isBusy
	 *            the isBusy to set
	 */
	public void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}

	/**
	 * @return the inst_entryNumber_ROB
	 */
	public int getInst_entryNumber_ROB() {
		return inst_entryNumber_ROB;
	}

	/**
	 * @param inst_entryNumber_ROB
	 *            the inst_entryNumber_ROB to set
	 */
	public void setInst_entryNumber_ROB(int inst_entryNumber_ROB) {
		this.inst_entryNumber_ROB = inst_entryNumber_ROB;
	}

	/**
	 * @return the sourceOperand1_avaliability
	 */
	public int getSourceOperand1_avaliability() {
		return sourceOperand1_avaliability;
	}

	/**
	 * @param sourceOperand1_avaliability
	 *            the sourceOperand1_avaliability to set
	 */
	public void setSourceOperand1_avaliability(int sourceOperand1_avaliability) {
		this.sourceOperand1_avaliability = sourceOperand1_avaliability;
	}

	/**
	 * @return the sourceOperand2_avaliability
	 */
	public int getSourceOperand2_avaliability() {
		return sourceOperand2_avaliability;
	}

	/**
	 * @param sourceOperand2_avaliability
	 *            the sourceOperand2_avaliability to set
	 */
	public void setSourceOperand2_avaliability(int sourceOperand2_avaliability) {
		this.sourceOperand2_avaliability = sourceOperand2_avaliability;
	}

	/**
	 * @return the sourceOperand1_value
	 */
	public Object getSourceOperand1_value() {
		return sourceOperand1_value;
	}

	/**
	 * @param sourceOperand1_value
	 *            the sourceOperand1_value to set
	 */
	public void setSourceOperand1_value(Object sourceOperand1_value) {
		this.sourceOperand1_value = sourceOperand1_value;
	}

	/**
	 * @return the sourceOperand2_value
	 */
	public Object getSourceOperand2_value() {
		return sourceOperand2_value;
	}

	/**
	 * @param sourceOperand2_value
	 *            the sourceOperand2_value to set
	 */
	public void setSourceOperand2_value(Object sourceOperand2_value) {
		this.sourceOperand2_value = sourceOperand2_value;
	}

	/**
	 * @return the address
	 */
	public long getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(long address) {
		this.address = address;
	}

	public boolean isEntryAvailableIn_RS() {
		if (this.getSourceOperand1_avaliability() == 0 && this.getSourceOperand2_avaliability() == 0) {
			return true;
		}

		return false;
	}

}
