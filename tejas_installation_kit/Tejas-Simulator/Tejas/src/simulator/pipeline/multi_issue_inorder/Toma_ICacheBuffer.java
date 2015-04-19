package pipeline.multi_issue_inorder;

import config.SimulationConfig;
import generic.Instruction;
import generic.OperationType;

public class Toma_ICacheBuffer {

	private Instruction[] buffer;
	private boolean[] fetchComplete;
	private int size;
	private int head;
	private int tail;

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	public Toma_ICacheBuffer(int size) {
		this.size = size;
		buffer = new Instruction[size];
		fetchComplete = new boolean[size];
		head = -1;
		tail = -1;
	}

	public void addToBuffer(Instruction newInstruction) {
		// TODO: ensure the below
		/*
		 * check if buffer is full before calling this function
		 */

		if (head == -1) {
			head = tail = 0;
		} else {
			tail = (tail + 1) % size;
		}

		buffer[tail] = newInstruction;
		if (SimulationConfig.detachMemSysInsn == true || newInstruction.getOperationType() == OperationType.inValid || newInstruction.getCISCProgramCounter() == -1) {
			// The first micro-operation of an instruction has a valid CISC IP. All the subsequent
			// micro-ops will have IP = -1(meaning invalid). We must not forward this requests to iCache.
			fetchComplete[tail] = true;
		} else {
			fetchComplete[tail] = false;
		}
	}

	public Instruction getNextInstruction() {
		Instruction toBeReturned = null;

		if (head == -1)
			return null;

		if (fetchComplete[head] == true) {
			toBeReturned = buffer[head];
			if (toBeReturned == null) {
				System.out.println("to be returned is null");
			}
			buffer[head] = null;
			if (head == tail) {
				head = tail = -1;
			} else {
				head = (head + 1) % size;
			}
		}

		return toBeReturned;
	}

	// TODO: check do we need below function...else remove
	public void updateFetchComplete(long programCounter) {
		if (head == -1)
			return;

		for (int i = head;; i = (i + 1) % size) {
			if (buffer[i] != null && buffer[i].getCISCProgramCounter() == programCounter) {
				fetchComplete[i] = true;
			}

			if (i == tail)
				break;
		}
	}

	public boolean isFull() {
		if ((tail + 1) % size == head && head != -1 && buffer[head] != null)
			return true;
		return false;
	}

	// TODO: check do we need below function...else remove
	public void dump() {
		if (head == -1)
			return;

		for (int i = head;; i = (i + 1) % size) {
			if (buffer[i] != null)
				System.out.println(buffer[i].getCISCProgramCounter() + " : " + fetchComplete[i]);

			if (i == tail)
				break;
		}
	}

}
