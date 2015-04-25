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

	private int sourceOperand1_availability;// Qj
	private int sourceOperand2_availability;// Qk

	// TO-DO:-------check if value actually required
	private Object sourceOperand1_value;// Vj
	private Object sourceOperand2_value;// Vk

	private long address; // A

	private boolean isStartedExecution;
	private boolean isCompletedExecution;
	private long timeToCompleteExecution;

	private boolean isIssuedRequestToCDB;

	public Toma_ReservationStationEntry() {
		isBusy = false;

		this.sourceOperand1_availability = -1;
		this.sourceOperand2_availability = -1;
		this.inst_entryNumber_ROB = -1;
		this.isStartedExecution = false;
		this.isCompletedExecution = false;
		this.isIssuedRequestToCDB = false;
	}

	/**
	 * @return the isIssuedRequestToCDB
	 */
	public boolean isIssuedRequestToCDB() {
		return isIssuedRequestToCDB;
	}

	/**
	 * @param isIssuedRequestToCDB
	 *            the isIssuedRequestToCDB to set
	 */
	public void setIssuedRequestToCDB(boolean isIssuedRequestToCDB) {
		this.isIssuedRequestToCDB = isIssuedRequestToCDB;
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
	 * @return the sourceOperand1_availability
	 */
	public int getSourceOperand1_availability() {
		return sourceOperand1_availability;
	}

	/**
	 * @param sourceOperand1_availability
	 *            the sourceOperand1_availability to set
	 */
	public void setSourceOperand1_availability(int sourceOperand1_availability) {
		this.sourceOperand1_availability = sourceOperand1_availability;
	}

	/**
	 * @return the sourceOperand2_availability
	 */
	public int getSourceOperand2_availability() {
		return sourceOperand2_availability;
	}

	/**
	 * @param sourceOperand2_availability
	 *            the sourceOperand2_availability to set
	 */
	public void setSourceOperand2_availability(int sourceOperand2_availability) {
		this.sourceOperand2_availability = sourceOperand2_availability;
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
		if (this.getSourceOperand1_availability() == 0 && this.getSourceOperand2_availability() == 0) {
			return true;
		}

		return false;
	}

	public void clearEntry() {
		this.setBusy(false);
		this.setInstruction(null);
		this.setInst_entryNumber_ROB(-1);
		this.setCompletedExecution(false);
		this.setStartedExecution(false);
		this.setIssuedRequestToCDB(false);
		this.setSourceOperand1_availability(-1);
		this.setSourceOperand2_availability(-1);
		this.setAddress(-1);
		this.setTimeToCompleteExecution(0);
	}

}
