package pipeline;

import java.lang.reflect.Array;

import main.ArchitecturalComponent;
import main.Main;
import config.XMLParser;
import generic.GenericCircularQueue;
import generic.GlobalClock;
import generic.Instruction;
import generic.Operand;
import generic.OperationType;
import generic.Statistics;

public class PipelineTests {

	static String configFileName;
	static GenericCircularQueue<Instruction> inputToPipeline;
	static final int INSTRUCTION_THRESHOLD = 2000;

	public static void setUpBeforeClass(String configFile) {

		// Parse the command line arguments
		XMLParser.parse(configFile);

		// initialize object pools
		Main.initializeObjectPools();

		// initialize cores, memory, tokenBus
		ArchitecturalComponent.createChip();
		inputToPipeline = new GenericCircularQueue<Instruction>(Instruction.class, INSTRUCTION_THRESHOLD);
		GenericCircularQueue<Instruction>[] toBeSet = (GenericCircularQueue<Instruction>[]) Array.newInstance(
				GenericCircularQueue.class, 1);
		toBeSet[0] = inputToPipeline;
		ArchitecturalComponent.getCore(0).getPipelineInterface().setInputToPipeline(toBeSet);
		ArchitecturalComponent.getCore(0).currentThreads = 1;
		ArchitecturalComponent.getCore(0).getExecEngine().setExecutionBegun(true);

		// Initialize the statistics
		Statistics.initStatistics();
	}

	/*
	 * simulates a sequence of intALU instructions that have no data dependencies
	 */
	public static void minimumDataDependencies() {

		// generate instruction sequence
		Instruction newInst;
		int temp = 1;
		for (int i = 0; i < 100; i++) {
			temp++;
			if (temp % 16 == 0) {
				temp++;
			}

			newInst = Instruction.getIntALUInstruction(Operand.getIntegerRegister(0), Operand.getIntegerRegister(0),
					Operand.getIntegerRegister(temp % 16));

			inputToPipeline.enqueue(newInst);
		}
		inputToPipeline.enqueue(Instruction.getInvalidInstruction());

		// simulate pipeline
		while (ArchitecturalComponent.getCores()[0].getPipelineInterface().isExecutionComplete() == false) {
			ArchitecturalComponent.getCores()[0].getPipelineInterface().oneCycleOperation();
			GlobalClock.incrementClock();
		}
	}

	/*
	 * simulates a sequence of intALU instructions, with (i+1)th instruction dependent on ith
	 */
	public static void maximumDataDependencies() {

		// generate instruction sequence
		Instruction newInst;
		for (int i = 0; i < 100; i++) {
			newInst = Instruction.getIntALUInstruction(Operand.getIntegerRegister(i % 16),
					Operand.getIntegerRegister(i % 16), Operand.getIntegerRegister((i + 1) % 16));

			inputToPipeline.enqueue(newInst);
		}
		inputToPipeline.enqueue(Instruction.getInvalidInstruction());

		// simulate pipeline
		while (ArchitecturalComponent.getCores()[0].getPipelineInterface().isExecutionComplete() == false) {
			ArchitecturalComponent.getCores()[0].getPipelineInterface().oneCycleOperation();
			GlobalClock.incrementClock();
		}
	}

	/*
	 * simulates a sequence of floatDiv instructions, with no data dependencies
	 */
	public static void structuralHazards() {

		// generate instruction sequence
		Instruction newInst;
		int temp = 1;
		for (int i = 0; i < 100; i++) {
			temp++;
			if (temp % 16 == 0) {
				temp++;
			}

			newInst = Instruction.getFloatingPointDivision(Operand.getIntegerRegister(0),
					Operand.getIntegerRegister(0), Operand.getIntegerRegister(temp % 16));

			inputToPipeline.enqueue(newInst);
		}
		inputToPipeline.enqueue(Instruction.getInvalidInstruction());

		// simulate pipeline
		while (ArchitecturalComponent.getCores()[0].getPipelineInterface().isExecutionComplete() == false) {
			ArchitecturalComponent.getCores()[0].getPipelineInterface().oneCycleOperation();
			GlobalClock.incrementClock();
		}
	}

	/*
	 * simulates a sequence of floatDiv instructions, all operating on R0, and writing to R0
	 */
	public static void renameTest() {

		// generate instruction sequence
		Instruction newInst;
		for (int i = 0; i < 100; i++) {
			newInst = Instruction.getFloatingPointDivision(Operand.getFloatRegister(0), Operand.getFloatRegister(0),
					Operand.getFloatRegister(0));

			inputToPipeline.enqueue(newInst);
		}
		inputToPipeline.enqueue(Instruction.getInvalidInstruction());

		// simulate pipeline
		while (ArchitecturalComponent.getCores()[0].getPipelineInterface().isExecutionComplete() == false) {
			ArchitecturalComponent.getCores()[0].getPipelineInterface().oneCycleOperation();
			GlobalClock.incrementClock();
		}
	}

	public static void main(String[] arguments) {
		String configFile = arguments[0];
		int testType = Integer.parseInt(arguments[1]);

		setUpBeforeClass(configFile);

		switch (testType) {
		case 0:
			minimumDataDependencies();
			break;

		case 1:
			maximumDataDependencies();
			break;

		case 2:
			structuralHazards();
			break;

		case 3:
			renameTest();
			break;

		case 11:
			toma_test_simple_noInst();
			break;

		case 12:
			toma_test_simple_intALU();
			break;

		case 13:
			toma_test_simple_nop();
			break;

		case 14:
			toma_test_simple_mov();
			break;

		case 15:
			toma_test_simple_floatDIV();
			break;

		case 16:
			toma_test_simple_xchg();
			break;

		case 17:
			toma_test_simple_jump();
			break;

		case 18:
			toma_test_simple_branch();
			break;

		default:
			misc.Error.showErrorAndExit("unknown test type");
		}
	}

	// ------Toma Change Start-------------

	private static void toma_simple_helper(OperationType opType) {
		Instruction newInst = null;
		for (int i = 0; i < 1; i++) {
			switch (opType) {
			case inValid:
				break;

			case integerALU:
				newInst = Instruction.getIntALUInstruction(Operand.getIntegerRegister(0),
						Operand.getIntegerRegister(0), Operand.getIntegerRegister(2));
				break;

			case nop:
				newInst = Instruction.getNOPInstruction();
				break;

			case mov:
				newInst = Instruction.getMoveInstruction(Operand.getIntegerRegister(2), Operand.getIntegerRegister(1));
				break;

			case floatDiv:
				newInst = Instruction.getFloatingPointDivision(Operand.getIntegerRegister(0),
						Operand.getIntegerRegister(0), Operand.getIntegerRegister(2));
				break;

			case xchg:
				newInst = Instruction.getExchangeInstruction(Operand.getIntegerRegister(0),
						Operand.getIntegerRegister(1));
				break;

			case jump:
				// newInst = Instruction.getUnconditionalJumpInstruction(newInstructionAddress);//TODO:vekho ainu
				break;

			case branch:
				// newInst = Instruction.getBranchInstruction(newInstructionAddress);//TODO: vekho ainu
				break;

			default:
				break;
			}

			if (opType != OperationType.inValid) {
				inputToPipeline.enqueue(newInst);
			}
		}
		inputToPipeline.enqueue(Instruction.getInvalidInstruction());

		// simulate pipeline
		while (ArchitecturalComponent.getCores()[0].getPipelineInterface().isExecutionComplete() == false) {
			ArchitecturalComponent.getCores()[0].getPipelineInterface().oneCycleOperation();
			GlobalClock.incrementClock();
		}
	}

	private static void toma_printIPC() {
		System.out.println("Total cycles taken: " + GlobalClock.getCurrentTime());
		System.out.println("IPC: " + (float) 1 / GlobalClock.getCurrentTime());
	}

	public static void toma_test_simple_noInst() {
		toma_simple_helper(OperationType.inValid);
		toma_printIPC();
	}

	public static void toma_test_simple_intALU() {
		toma_simple_helper(OperationType.integerALU);
		toma_printIPC();
	}

	public static void toma_test_simple_nop() {
		toma_simple_helper(OperationType.nop);
		toma_printIPC();
	}

	public static void toma_test_simple_mov() {
		toma_simple_helper(OperationType.mov);
		toma_printIPC();
	}

	public static void toma_test_simple_floatDIV() {
		toma_simple_helper(OperationType.floatDiv);
		toma_printIPC();
	}

	public static void toma_test_simple_xchg() {
		toma_simple_helper(OperationType.xchg);
		toma_printIPC();
	}

	public static void toma_test_simple_jump() {
		toma_simple_helper(OperationType.jump);
		toma_printIPC();
	}

	public static void toma_test_simple_branch() {
		toma_simple_helper(OperationType.branch);
		toma_printIPC();
	}

	// ------Toma Change End-------------
}
