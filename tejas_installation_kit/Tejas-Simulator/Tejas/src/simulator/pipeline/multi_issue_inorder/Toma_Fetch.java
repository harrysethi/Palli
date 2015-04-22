/**
 * 
 */
package pipeline.multi_issue_inorder;

import main.CustomObjectPool;
import config.SimulationConfig;
import generic.Core;
import generic.GenericCircularQueue;
import generic.Instruction;
import generic.OperationType;

/**
 * @author dell
 *
 */
public class Toma_Fetch {

	Toma_ICacheBuffer toma_ICacheBuffer;
	MultiIssueInorderExecutionEngine executionEngine;
	Core core;

	GenericCircularQueue<Instruction> toma_fetchBuffer;
	int toma_fetchWidth;

	public GenericCircularQueue<Instruction> inputToPipeline;

	// TODO: check this is a single queue rather a array as in OOO

	public Toma_Fetch(Core core, MultiIssueInorderExecutionEngine executionEngine) {
		this.core = core;
		this.executionEngine = executionEngine;
		toma_fetchBuffer = executionEngine.getToma_fetchBuffer();
		toma_fetchWidth = core.getDecodeWidth();
	}

	public void performFetch() {
		// TO-DO: logic in inOrder-- sir se upar ja ra hai... "we are using similar as OOO".

		Instruction ins;
		for (int i = 0; i < toma_fetchWidth; i++) {
			if (toma_fetchBuffer.isFull() == true) {
				break;
			}

			ins = toma_ICacheBuffer.getNextInstruction();

			if (ins != null) {
				toma_fetchBuffer.enqueue(ins);
			}

			else {
				this.core.getExecEngine().incrementInstructionMemStall(1); // TODO: check karna hai ke ni
				break;
			}
		}

		for (int i = 0; i < toma_ICacheBuffer.getSize(); i++) {
			if (inputToPipeline.size() <= 0) {
				break;
			}

			ins = inputToPipeline.peek(0);

			// TODO: "instructions to be dropped" & "synch" logic not written

			// drop memory operations if specified in configuration file
			if (ins.getOperationType() == OperationType.load || ins.getOperationType() == OperationType.store) {
				if (SimulationConfig.detachMemSysData == true) {
					CustomObjectPool.getInstructionPool().returnObject(ins);
					continue;
				}
			}

			// add to iCache buffer, and issue request to iCache
			if (!toma_ICacheBuffer.isFull() && executionEngine.getCoreMemorySystem().getiCache().isBusy() == false) {
				toma_ICacheBuffer.addToBuffer(inputToPipeline.pollFirst());

				if (SimulationConfig.detachMemSysInsn == false && ins.getOperationType() != OperationType.inValid) {
					if (ins.getCISCProgramCounter() != -1) {
						executionEngine.getCoreMemorySystem().issueRequestToInstrCache(ins.getCISCProgramCounter());
					}
				}
			}

			else {
				break;
			}

		}

	}

	/**
	 * @param inputToPipeline
	 *            the inputToPipeline to set
	 */
	public void setInputToPipeline(GenericCircularQueue<Instruction> inputToPipeline) {
		this.inputToPipeline = inputToPipeline;
	}

	/**
	 * @param toma_ICacheBuffer
	 *            the toma_ICacheBuffer to set
	 */
	public void setToma_ICacheBuffer(Toma_ICacheBuffer toma_ICacheBuffer) {
		this.toma_ICacheBuffer = toma_ICacheBuffer;
	}

}
