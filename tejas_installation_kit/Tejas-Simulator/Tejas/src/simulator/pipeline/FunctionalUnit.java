package pipeline;

import pipeline.multi_issue_inorder.Toma_ReservationStation;

public class FunctionalUnit {

	FunctionalUnitType FUType;
	int latency;
	int reciprocalOfThroughput;
	long timeWhenFUAvailable;
	int portNumber;

	Toma_ReservationStation reservationStation;

	public FunctionalUnit(FunctionalUnitType FUType, int latency, int reciprocalOfThroughput, int portNumber) {
		this.FUType = FUType;
		this.latency = latency;
		this.reciprocalOfThroughput = reciprocalOfThroughput;
		this.timeWhenFUAvailable = 0;
		this.portNumber = portNumber;
	}

	public FunctionalUnitType getFUType() {
		return FUType;
	}

	public int getLatency() {
		return latency;
	}

	public int getReciprocalOfThroughput() {
		return reciprocalOfThroughput;
	}

	public long getTimeWhenFUAvailable() {
		return timeWhenFUAvailable;
	}

	public void setTimeWhenFUAvailable(long timeWhenFUAvailable) {
		this.timeWhenFUAvailable = timeWhenFUAvailable;
	}

	public int getPortNumber() {
		return portNumber;
	}

}
