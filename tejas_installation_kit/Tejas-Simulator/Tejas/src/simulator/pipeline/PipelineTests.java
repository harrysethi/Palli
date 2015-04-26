package pipeline;

import generic.GenericCircularQueue;
import generic.GlobalClock;
import generic.Instruction;
import generic.Operand;
import generic.OperationType;
import generic.Statistics;

import java.lang.reflect.Array;

import main.ArchitecturalComponent;
import main.Main;
import config.XMLParser;

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
	private static void minimumDataDependencies() {
		System.out.println("---------minimumDataDependencies-------------");
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

		simulatePipeline();
	}

	/*
	 * simulates a sequence of intALU instructions, with (i+1)th instruction dependent on ith
	 */
	private static void maximumDataDependencies() {
		System.out.println("---------maximumDataDependencies-------------");
		// generate instruction sequence
		Instruction newInst;
		for (int i = 0; i < 100; i++) {
			newInst = Instruction.getIntALUInstruction(Operand.getIntegerRegister(i % 16),
					Operand.getIntegerRegister(i % 16), Operand.getIntegerRegister((i + 1) % 16));

			inputToPipeline.enqueue(newInst);
		}
		inputToPipeline.enqueue(Instruction.getInvalidInstruction());

		simulatePipeline();
	}

	/*
	 * simulates a sequence of floatDiv instructions, with no data dependencies
	 */
	private static void structuralHazards() {
		System.out.println("---------structuralHazards-------------");
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

		simulatePipeline();
	}

	/*
	 * simulates a sequence of floatDiv instructions, all operating on R0, and writing to R0
	 */
	private static void renameTest() {
		System.out.println("---------renameTest-------------");
		// generate instruction sequence
		Instruction newInst;
		for (int i = 0; i < 100; i++) {
			newInst = Instruction.getFloatingPointDivision(Operand.getFloatRegister(0), Operand.getFloatRegister(0),
					Operand.getFloatRegister(0));

			inputToPipeline.enqueue(newInst);
		}
		inputToPipeline.enqueue(Instruction.getInvalidInstruction());

		simulatePipeline();
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

		// ------Toma Change Start-------------

		case 11:
			toma_test_simple_noInst();
			break;

		case 12:
			toma_test_simple_nop();
			break;

		case 13:
			toma_test_simple_intALU();
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
			toma_test_simple_load();
			break;

		case 18:
			toma_test_simple_store();
			break;

		case 19:
			toma_test_simple_jump();
			break;

		case 20:
			toma_test_simple_branch();
			break;

		case 21:
			toma_test_dependency();
			break;

		case 22:
			toma_test_no_dependency();
			break;

		case 23:
			toma_test_out_of_order();
			break;

		case 24:
			toma_test_dependency_xchg();
			break;

		case 25:
			toma_test_dependency_xchg_2();
			break;

		case 26:
			toma_test_dependency_int_float();
			break;

		case 27:
			toma_test_mov_imm();
			break;

		case 28:
			toma_test_dependency_load();
			break;

		case 29:
			toma_test_dependency_store();
			break;

		case 30:
			toma_test_dependency_load_store();
			break;

		case 31:
			toma_test_mispredicted_branch();
			break;

		case 32:
			toma_test_predicted_branch();
			break;

		default:
			misc.Error.showErrorAndExit("unknown test type");
		}
	}

	private static void toma_simple_helper(OperationType opType) {
		Instruction newInst = null;
		for (int i = 0; i < 1; i++) {
			switch (opType) {
			case inValid:
				break;

			case nop:
				newInst = Instruction.getNOPInstruction();
				break;

			case integerALU:
				newInst = Instruction.getIntALUInstruction(Operand.getIntegerRegister(0),
						Operand.getIntegerRegister(0), Operand.getIntegerRegister(2));
				break;

			case mov:
				newInst = Instruction.getMoveInstruction(Operand.getIntegerRegister(2), Operand.getIntegerRegister(1));
				break;

			case floatDiv:
				newInst = Instruction.getFloatingPointDivision(Operand.getFloatRegister(0),
						Operand.getFloatRegister(0), Operand.getFloatRegister(2));
				break;

			case xchg:
				newInst = Instruction.getExchangeInstruction(Operand.getIntegerRegister(0),
						Operand.getIntegerRegister(1));
				break;

			case load:
				newInst = getLoadInstruction(0, 1, 1234);
				break;

			case store:
				newInst = getStoreInstruction(0, 1, 1234);
				break;

			case jump:
				newInst = Instruction.getUnconditionalJumpInstruction(Operand.getIntegerRegister(0));
				break;

			case branch:
				newInst = Instruction.getBranchInstruction(Operand.getIntegerRegister(0));
				newInst.setBranchTaken(true);
				break;

			default:
				break;
			}

			if (opType != OperationType.inValid) {
				inputToPipeline.enqueue(newInst);
			}
		}

		simulatePipeline();
	}

	private static Instruction getStoreInstruction(int memReg, int srcReg, int addr) {
		Instruction newInst;
		Operand memory_first_operand_store_source1 = Operand.getIntegerRegister(memReg);
		Operand operand_store_source1 = Operand.getMemoryOperand(memory_first_operand_store_source1, null);
		newInst = Instruction.getStoreInstruction(operand_store_source1, Operand.getIntegerRegister(srcReg));
		newInst.setSourceOperand1MemValue(addr);
		return newInst;
	}

	private static Instruction getLoadInstruction(int memReg, int destReg, int addr) {
		Instruction newInst;
		Operand memory_first_operand_load_source1 = Operand.getIntegerRegister(memReg);
		Operand operand_load_source1 = Operand.getMemoryOperand(memory_first_operand_load_source1, null);
		newInst = Instruction.getLoadInstruction(operand_load_source1, Operand.getIntegerRegister(destReg));
		newInst.setSourceOperand1MemValue(addr);
		return newInst;
	}

	private static void simulatePipeline() {
		inputToPipeline.enqueue(Instruction.getInvalidInstruction());

		while (ArchitecturalComponent.getCores()[0].getPipelineInterface().isExecutionComplete() == false) {
			ArchitecturalComponent.getCores()[0].getPipelineInterface().oneCycleOperation();
			GlobalClock.incrementClock();
		}

		System.out.println("\n---------------Simulation Completed------------");
	}

	private static void toma_printIPC(int numOfInstr) {
		System.out.println();
		System.out.println();
		System.out.println("Total cycles taken: " + GlobalClock.getCurrentTime());
		System.out.println("IPC: " + (float) numOfInstr / GlobalClock.getCurrentTime());
	}

	public static void toma_test_simple_noInst() {
		System.out.println("---------toma_test_simple_noInst-------------");
		toma_simple_helper(OperationType.inValid);
		toma_printIPC(1);
	}

	public static void toma_test_simple_nop() {
		System.out.println("---------toma_test_simple_nop-------------");
		toma_simple_helper(OperationType.nop);
		toma_printIPC(1);
	}

	public static void toma_test_simple_intALU() {
		System.out.println("---------toma_test_simple_intALU-------------");
		toma_simple_helper(OperationType.integerALU);
		toma_printIPC(1);
	}

	public static void toma_test_simple_mov() {
		System.out.println("---------toma_test_simple_mov-------------");
		toma_simple_helper(OperationType.mov);
		toma_printIPC(1);
	}

	public static void toma_test_simple_floatDIV() {
		System.out.println("---------toma_test_simple_floatDIV-------------");
		toma_simple_helper(OperationType.floatDiv);
		toma_printIPC(1);
	}

	public static void toma_test_simple_xchg() {
		System.out.println("---------toma_test_simple_xchg-------------");
		toma_simple_helper(OperationType.xchg);
		toma_printIPC(1);
	}

	public static void toma_test_simple_load() {
		System.out.println("---------toma_test_simple_load-------------");
		toma_simple_helper(OperationType.load);
		toma_printIPC(1);
	}

	public static void toma_test_simple_store() {
		System.out.println("---------toma_test_simple_store-------------");
		toma_simple_helper(OperationType.store);
		toma_printIPC(1);
	}

	public static void toma_test_simple_jump() {
		System.out.println("---------toma_test_simple_jump-------------");
		toma_simple_helper(OperationType.jump);
		toma_printIPC(1);
	}

	public static void toma_test_simple_branch() {
		System.out.println("---------toma_test_simple_branch-------------");
		toma_simple_helper(OperationType.branch);
		toma_printIPC(1);
	}

	public static void toma_test_dependency() {
		System.out.println("---------toma_test_dependency-------------");
		Instruction ins = null;
		ins = Instruction.getIntALUInstruction(Operand.getIntegerRegister(0), Operand.getIntegerRegister(0),
				Operand.getIntegerRegister(2));
		inputToPipeline.enqueue(ins);
		ins = Instruction.getIntALUInstruction(Operand.getIntegerRegister(2), Operand.getIntegerRegister(0),
				Operand.getIntegerRegister(3));
		inputToPipeline.enqueue(ins);

		simulatePipeline();
		toma_printIPC(2);
	}

	public static void toma_test_no_dependency() {
		System.out.println("---------toma_test_no_dependency-------------");
		Instruction ins = null;
		ins = Instruction.getIntALUInstruction(Operand.getIntegerRegister(0), Operand.getIntegerRegister(0),
				Operand.getIntegerRegister(2));
		inputToPipeline.enqueue(ins);
		ins = Instruction.getIntALUInstruction(Operand.getIntegerRegister(5), Operand.getIntegerRegister(0),
				Operand.getIntegerRegister(3));
		inputToPipeline.enqueue(ins);

		simulatePipeline();
		toma_printIPC(2);
	}

	public static void toma_test_out_of_order() {
		System.out.println("---------toma_test_out_of_order-------------");
		Instruction ins = null;
		ins = Instruction.getIntALUInstruction(Operand.getIntegerRegister(0), Operand.getIntegerRegister(0),
				Operand.getIntegerRegister(2));
		inputToPipeline.enqueue(ins);
		ins = Instruction.getIntALUInstruction(Operand.getIntegerRegister(2), Operand.getIntegerRegister(0),
				Operand.getIntegerRegister(3));
		inputToPipeline.enqueue(ins);
		ins = Instruction.getIntALUInstruction(Operand.getIntegerRegister(5), Operand.getIntegerRegister(0),
				Operand.getIntegerRegister(3));
		inputToPipeline.enqueue(ins);

		simulatePipeline();
		toma_printIPC(3);
	}

	public static void toma_test_dependency_xchg() {
		System.out.println("---------toma_test_dependency_xchg-------------");
		Instruction ins = null;
		ins = Instruction.getIntALUInstruction(Operand.getIntegerRegister(0), Operand.getIntegerRegister(0),
				Operand.getIntegerRegister(2));
		inputToPipeline.enqueue(ins);
		ins = Instruction.getExchangeInstruction(Operand.getIntegerRegister(0), Operand.getIntegerRegister(2));
		inputToPipeline.enqueue(ins);

		simulatePipeline();
		toma_printIPC(2);
	}

	public static void toma_test_dependency_xchg_2() {
		System.out.println("---------toma_test_dependency_xchg_2-------------");
		Instruction ins = null;
		ins = Instruction.getIntALUInstruction(Operand.getIntegerRegister(0), Operand.getIntegerRegister(0),
				Operand.getIntegerRegister(2));
		inputToPipeline.enqueue(ins);
		ins = Instruction.getIntALUInstruction(Operand.getIntegerRegister(2), Operand.getIntegerRegister(0),
				Operand.getIntegerRegister(5));
		inputToPipeline.enqueue(ins);
		ins = Instruction.getExchangeInstruction(Operand.getIntegerRegister(0), Operand.getIntegerRegister(2));
		inputToPipeline.enqueue(ins);
		ins = Instruction.getIntALUInstruction(Operand.getIntegerRegister(2), Operand.getIntegerRegister(1),
				Operand.getIntegerRegister(5));
		inputToPipeline.enqueue(ins);

		simulatePipeline();
		toma_printIPC(4);
	}

	public static void toma_test_dependency_int_float() {
		System.out.println("---------toma_test_dependency_int_float-------------");
		Instruction ins = null;

		ins = Instruction.getIntALUInstruction(Operand.getIntegerRegister(0), Operand.getIntegerRegister(0),
				Operand.getIntegerRegister(2));
		inputToPipeline.enqueue(ins);
		ins = Instruction.getFloatingPointALU(Operand.getFloatRegister(2), Operand.getFloatRegister(0),
				Operand.getFloatRegister(3));
		inputToPipeline.enqueue(ins);

		simulatePipeline();
		toma_printIPC(2);
	}

	public static void toma_test_mov_imm() {
		System.out.println("---------toma_test_mov_imm-------------");
		Instruction ins = null;

		ins = Instruction.getMoveInstruction(Operand.getIntegerRegister(2), Operand.getImmediateOperand());
		inputToPipeline.enqueue(ins);

		simulatePipeline();
		toma_printIPC(1);
	}

	public static void toma_test_dependency_load() {
		System.out.println("---------toma_test_dependency_load-------------");
		Instruction ins = null;

		ins = getLoadInstruction(0, 1, 1234);
		inputToPipeline.enqueue(ins);
		ins = Instruction.getIntALUInstruction(Operand.getIntegerRegister(1), Operand.getIntegerRegister(2),
				Operand.getIntegerRegister(5));
		inputToPipeline.enqueue(ins);

		simulatePipeline();
		toma_printIPC(2);
	}

	public static void toma_test_dependency_store() {
		System.out.println("---------toma_test_dependency_store-------------");
		Instruction ins = null;

		ins = Instruction.getIntALUInstruction(Operand.getIntegerRegister(1), Operand.getIntegerRegister(2),
				Operand.getIntegerRegister(1));
		inputToPipeline.enqueue(ins);
		ins = getStoreInstruction(1, 0, 1234);
		inputToPipeline.enqueue(ins);

		simulatePipeline();
		toma_printIPC(2);
	}

	public static void toma_test_dependency_load_store() {
		System.out.println("---------toma_test_dependency_load_store-------------");
		Instruction ins = null;

		ins = getStoreInstruction(0, 1, 1234);
		inputToPipeline.enqueue(ins);
		ins = getLoadInstruction(2, 3, 1234);
		inputToPipeline.enqueue(ins);
		ins = getStoreInstruction(4, 5, 1234);
		inputToPipeline.enqueue(ins);
		ins = getStoreInstruction(6, 7, 1235);
		inputToPipeline.enqueue(ins);

		simulatePipeline();
		toma_printIPC(4);
	}

	public static void toma_test_mispredicted_branch() {
		System.out.println("---------toma_test_mispredicted_branch-------------");
		Instruction ins = null;

		ins = Instruction.getBranchInstruction(Operand.getIntegerRegister(0));
		ins.setBranchTaken(false);
		inputToPipeline.enqueue(ins);
		ins = Instruction.getIntALUInstruction(Operand.getIntegerRegister(1), Operand.getIntegerRegister(2),
				Operand.getIntegerRegister(1));
		inputToPipeline.enqueue(ins);

		simulatePipeline();
		toma_printIPC(4);
	}

	public static void toma_test_predicted_branch() {
		System.out.println("---------toma_test_predicted_branch-------------");
		Instruction ins = null;

		ins = Instruction.getBranchInstruction(Operand.getIntegerRegister(0));
		ins.setBranchTaken(true);
		inputToPipeline.enqueue(ins);
		ins = Instruction.getIntALUInstruction(Operand.getIntegerRegister(1), Operand.getIntegerRegister(2),
				Operand.getIntegerRegister(1));
		inputToPipeline.enqueue(ins);

		simulatePipeline();
		toma_printIPC(4);
	}

	// ------Toma Change End-------------
}
