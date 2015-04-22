package pipeline.multi_issue_inorder;

import pipeline.PipelineInterface;
import generic.Core;
import generic.EventQueue;
import generic.GenericCircularQueue;
import generic.GlobalClock;
import generic.Instruction;

public class MultiIssueInorderPipeline implements PipelineInterface {

	Core core;
	MultiIssueInorderExecutionEngine containingExecutionEngine;
	EventQueue eventQ;
	int coreStepSize;
	StageLatch_MII ifId, idEx, exMem, memWb, wbDone;

	public MultiIssueInorderPipeline(Core _core, EventQueue eventQ) {

		this.core = _core;
		containingExecutionEngine = (MultiIssueInorderExecutionEngine) core.getExecEngine();
		this.eventQ = eventQ;
		this.coreStepSize = core.getStepSize(); // Not Necessary. Global clock hasn't been initialized yet
												// So, step sizes of the cores hasn't been set.
												// It will be set when the step sizes of the cores will be set.
		this.ifId = containingExecutionEngine.getIfIdLatch();
		this.idEx = containingExecutionEngine.getIdExLatch();
		this.exMem = containingExecutionEngine.getExMemLatch();
		this.memWb = containingExecutionEngine.getMemWbLatch();
		this.wbDone = containingExecutionEngine.getWbDoneLatch();
	}

	public void oneCycleOperation() {
		// ------Toma Change Start-------------
		long currentTime = GlobalClock.getCurrentTime();

		if (currentTime % coreStepSize == 0 && containingExecutionEngine.isExecutionBegun() == true && containingExecutionEngine.isExecutionComplete() == false) {
			toma_commit();
			toma_writeback();
			// TODO: check toma_writeback yahin pe hi aayega na?
		}

		drainEventQueue(); // Process Memory Requests

		if (currentTime % getCoreStepSize() == 0 && containingExecutionEngine.isExecutionBegun() == true && !containingExecutionEngine.getExecutionComplete()) {
			toma_execute();
			toma_issue();
			toma_fetch();
		}

		// ------Toma Change End-------------

		// Toma:: below is commented as part of Toma Changes
		/*
		 * if (currentTime % getCoreStepSize() == 0 && containingExecutionEngine.isExecutionBegun() == true && !containingExecutionEngine.getExecutionComplete()) { writeback(); }
		 * 
		 * drainEventQueue(); // Process Memory Requests if (currentTime % getCoreStepSize() == 0 && containingExecutionEngine.isExecutionBegun() == true &&
		 * !containingExecutionEngine.getExecutionComplete()) { mem(); exec(); decode(); fetch(); }
		 */
	}

	private void drainEventQueue() {
		eventQ.processEvents();
	}

	// ------Toma Change Start-------------
	public void toma_fetch() {
		containingExecutionEngine.getToma_fetch().performFetch();// TODO: check may need to pass "this" :D..OOO mein naa kiya baai :O :O
	}

	public void toma_issue() {
		containingExecutionEngine.getToma_issue().performIssue();// TODO: check may need to pass "this" :D..OOO mein naa kiya baai :O :O
	}

	public void toma_execute() {
		containingExecutionEngine.getToma_execute().performExecute();// TODO: check may need to pass "this" :D..OOO mein naa kiya baai :O :O
	}

	public void toma_writeback() {
		containingExecutionEngine.getToma_writeResult().performWriteResult();
		;// TODO: check may need to pass "this" :D..OOO mein naa kiya baai :O :O
	}

	public void toma_commit() {
		containingExecutionEngine.getToma_ROB().performCommits();// TODO: check may need to pass "this" :D..OOO mein naa kiya baai :O :O
	}

	// ------Toma Change End-------------

	public void writeback() {
		containingExecutionEngine.getWriteBackUnitIn().performWriteBack(this);
	}

	public void mem() {
		containingExecutionEngine.getMemUnitIn().performMemEvent(this);
	}

	public void exec() {
		containingExecutionEngine.getExecUnitIn().execute(this);
	}

	public void decode() {
		containingExecutionEngine.getDecodeUnitIn().performDecode(this);
	}

	public void fetch() {
		containingExecutionEngine.getFetchUnitIn().performFetch(this);
	}

	@Override
	public boolean isExecutionComplete() {
		return (containingExecutionEngine.getExecutionComplete());
	}

	@Override
	public int getCoreStepSize() {
		return this.core.getStepSize();
	}

	@Override
	public void setcoreStepSize(int stepSize) {
		this.coreStepSize = stepSize;
	}

	@Override
	public void resumePipeline() {
		containingExecutionEngine.getFetchUnitIn().resumePipeline();
	}

	@Override
	public Core getCore() {
		return core;
	}

	@Override
	public boolean isSleeping() {
		return containingExecutionEngine.getFetchUnitIn().getSleep();
	}

	public StageLatch_MII getIfIdLatch() {
		return this.ifId;
	}

	public StageLatch_MII getIdExLatch() {
		return this.idEx;
	}

	public StageLatch_MII getExMemLatch() {
		return this.exMem;
	}

	public StageLatch_MII getMemWbLatch() {
		return this.memWb;
	}

	public StageLatch_MII getWbDoneLatch() {
		return this.wbDone;
	}

	@Override
	public void setExecutionComplete(boolean status) {
		containingExecutionEngine.setExecutionComplete(status);
	}

	@Override
	public void adjustRunningThreads(int adjval) {
		// TO-DO Auto-generated method stub
	}

	@Override
	public void setInputToPipeline(GenericCircularQueue<Instruction>[] inputToPipeline) {
		this.core.getExecEngine().setInputToPipeline(inputToPipeline);
	}

	@Override
	public void setTimingStatistics() {
		// Not needed here, set by inorderexecutionengine

	}

	@Override
	public void setPerCoreMemorySystemStatistics() {
		// Not needed here, set by inorderexecutionengine

	}
}
