/**
 * 
 */
package pipeline.multi_issue_inorder;

import generic.OperationType;

/**
 * @author dell
 *
 */
public class Toma_ROBEntry {
	OperationType operationType; // Inst
	boolean isBusy;
	boolean isReady;
	int resultValue; // Val
	int destinationRegNumber; // Dst
	// TODO:----..check here using regNum may be incorrect
}
