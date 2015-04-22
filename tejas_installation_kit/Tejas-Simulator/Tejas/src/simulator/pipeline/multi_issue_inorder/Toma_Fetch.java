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
	// TODO:--- check whether to extend simulation element
	// TODO: --- check do we need latch

	Toma_ICacheBuffer toma_ICacheBuffer;
	MultiIssueInorderExecutionEngine executionEngine;
	Core core;// TODO: core ko hatao agar ni chiye to

	GenericCircularQueue<Instruction> toma_fetchBuffer;
	int toma_fetchWidth;

	public GenericCircularQueue<Instruction> inputToPipeline;

	// TODO: check this is a single queue rather a array as in OOO

	public Toma_Fetch(Core core, MultiIssueInorderExecutionEngine executionEngine) {
		// TODO: check do we need "super(PortType.Unlimited, -1, -1, -1, -1);"... i think hona chahiye
		this.core = core;
		this.executionEngine = executionEngine;
		toma_fetchBuffer = executionEngine.getToma_fetchBuffer();
		toma_fetchWidth = core.getDecodeWidth();
	}

	public void performFetch() {
		// TODO: logic in inOrder-- sir se upar ja ra hai... "we are using similar as OOO".

		// TODO: ye jo neeche hain ..ye OOO mein shayad kabi ni chalega...to fer kahaan karna hai
		/*
		 * if (ins.getOperationType() == OperationType.inValid) { executionEngine.setExecutionComplete(true);
		 * CustomObjectPool.getInstructionPool().returnObject(ins); return; }
		 */
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

			// TODO: fetch_logic se chaep rahe hain...& synch ni chaepa...ensure it's correct

			// TODO: instructions to be dropped...bi ni chaepa

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
