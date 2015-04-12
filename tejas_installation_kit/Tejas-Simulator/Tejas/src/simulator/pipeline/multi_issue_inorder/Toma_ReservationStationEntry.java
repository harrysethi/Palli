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

	OperationType operationType; // Op
	boolean isBusy;
	int inst_entryNumber_ROB; // Qi

	int sourceOperand1_avaliability;// Qj
	int sourceOperand2_avaliability;// Qk

	// TODO:-------check if value actually required ;)
	int sourceOperand1_value;// Vj
	int sourceOperand2_value;// Vk

}
