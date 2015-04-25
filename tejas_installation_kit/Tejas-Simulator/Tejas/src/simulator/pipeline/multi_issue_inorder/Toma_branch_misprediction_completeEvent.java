package pipeline.multi_issue_inorder;

import generic.Event;
import generic.RequestType;
import generic.SimulationElement;

public class Toma_branch_misprediction_completeEvent extends Event {

	public Toma_branch_misprediction_completeEvent(long eventTime, SimulationElement requestingElement,
			SimulationElement processingElement, RequestType requestType) {
		super(null, eventTime, requestingElement, processingElement, requestType, -1);
	}

}
