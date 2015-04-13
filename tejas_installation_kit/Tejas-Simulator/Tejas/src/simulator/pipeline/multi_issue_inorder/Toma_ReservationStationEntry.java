/**
 * 
 */
package pipeline.multi_issue_inorder;

import generic.OperationType;

/**
 * @author dell
 *
 */
public class Toma_ReservationStationEntry {

	private OperationType operationType; // Op
	private boolean isBusy;
	private int inst_entryNumber_ROB; // Qi

	private int sourceOperand1_avaliability;// Qj
	private int sourceOperand2_avaliability;// Qk

	// TODO:-------check if value actually required ;)
	private Object sourceOperand1_value;// Vj
	private Object sourceOperand2_value;// Vk

	public Toma_ReservationStationEntry() {
		isBusy = false;
	}

	/**
	 * @return the operationType
	 */
	public OperationType getOperationType() {
		return operationType;
	}

	/**
	 * @param operationType
	 *            the operationType to set
	 */
	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
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

}
