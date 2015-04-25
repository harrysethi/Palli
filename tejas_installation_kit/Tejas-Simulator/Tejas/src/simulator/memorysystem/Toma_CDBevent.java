package memorysystem;

import pipeline.multi_issue_inorder.MultiIssueInorderExecutionEngine;
import generic.Core;
import generic.Event;
import generic.EventQueue;
import generic.RequestType;
import generic.SimulationElement;

public class Toma_CDBevent extends Event implements Cloneable {

	Toma_CDBentry toma_CDBentry;
	MultiIssueInorderExecutionEngine executionEngine;
	Core core;

	public Toma_CDBevent(EventQueue eventQ, long eventTime, SimulationElement requestingElement,
			SimulationElement processingElement, RequestType requestType, Toma_CDBentry toma_CDBentry,
			MultiIssueInorderExecutionEngine executionEngine, Core core) {
		super(eventQ, eventTime, requestingElement, processingElement, requestType, -1);
		this.toma_CDBentry = toma_CDBentry;
		this.executionEngine = executionEngine;
		this.core = core;
	}

	/**
	 * @return the toma_CDBentry
	 */
	public Toma_CDBentry getToma_CDBentry() {
		return toma_CDBentry;
	}

	/**
	 * @return the executionEngine
	 */
	public MultiIssueInorderExecutionEngine getExecutionEngine() {
		return executionEngine;
	}

	/**
	 * @return the core
	 */
	public Core getCore() {
		return core;
	}

}
