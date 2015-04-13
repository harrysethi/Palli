/**
 * 
 */
package pipeline.multi_issue_inorder;

import pipeline.FunctionalUnitType;
import pipeline.OpTypeToFUTypeMapping;
import generic.Core;
import generic.GenericCircularQueue;
import generic.Instruction;

/**
 * @author dell
 *
 */
public class Toma_Issue {
	// TODO:--- check whether to extend simulation element
	// TODO: --- check do we need latch

	Core core;

	public GenericCircularQueue<Instruction> inputToPipeline;

	// TODO: check this is a single queue rather a array as in OOO

	public Toma_Issue(Core core) {
		// TODO: check do we need "super(PortType.Unlimited, -1, -1, -1, -1);"... i think hona chahiye
		this.core = core;
	}

	public void performIssue() {
		// TODO: check do we need icacheBuffer
		// TODO: logic in inOrder & OOO -- sir se upar ja ra hai... check later

		if (inputToPipeline.isEmpty())
			return;

		Instruction ins = inputToPipeline.pollFirst();

		if (ins == null)
			return;

		// TODO: shall be some logic if instr is invalid

		long fURequest = 0;
		if (OpTypeToFUTypeMapping.getFUType(ins.getOperationType()) != FunctionalUnitType.inValid) {
			fURequest = containingExecutionEngine.getExecutionCore().requestFU(OpTypeToFUTypeMapping.getFUType(ins.getOperationType()));

			if (fURequest > 0) {
				break;
			}
		}
	}
}
