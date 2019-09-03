package data_aware_bpmn.controlflow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import data_aware_bpmn.dataschema.CaseVariableFactory;
import data_aware_bpmn.dataschema.ConjunctiveSelectQuery;
import data_aware_bpmn.dataschema.ConstantFactory;
import data_aware_bpmn.dataschema.EevarManager;
import data_aware_bpmn.dataschema.RelationFactory;
import data_aware_bpmn.dataschema.SortFactory;
import data_aware_bpmn.exception.EevarOverflowException;
import data_aware_bpmn.exception.InvalidInputException;
import data_aware_bpmn.exception.UnmatchingSortException;

public class MainProcess {
	private ProcessBlock root;
	private String total_mcmt = "";
	private ConjunctiveSelectQuery safety_formula;
	private PrintWriter pw;
	
	public MainProcess (ProcessBlock process_block) {
		this.root = process_block;
		this.root.life_cycle.setLifeCycle(2);
		try {
			this.pw = new PrintWriter(new File("mcmt_translation.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ProcessBlock getRoot() {
		return root;
	}

	public void setRoot(ProcessBlock root) {
		this.root = root;
	}

	public String getTotal_mcmt() {
		return total_mcmt;
	}

	public void setTotal_mcmt(String total_mcmt) {
		this.total_mcmt = total_mcmt;
	}
	
	public ConjunctiveSelectQuery getSafety_formula() {
		return safety_formula;
	}

	public void setSafety_formula(ConjunctiveSelectQuery safety_formula) {
		this.safety_formula = safety_formula;
	}

	// method for generating the total mcmt translation of the process
	public String process_mcmt_generation() throws InvalidInputException, UnmatchingSortException, EevarOverflowException {
		process_mcmt_generation(this.root);
		return this.total_mcmt;
	}
	
	public void process_mcmt_generation(Block block) throws InvalidInputException, UnmatchingSortException, EevarOverflowException {
		// base case, the block is a task
		if (block instanceof Task) {
			this.total_mcmt += block.mcmt_translation();
			return;
		}
		
		// translation of the block, then recursively go through the depth of the tree
		this.total_mcmt += block.mcmt_translation();
		for (Block sub_block : block.sub_blocks) {
			process_mcmt_generation(sub_block);
		}
	}
	
	public void printDeclaration() {
		pw.println(SortFactory.getInstance().printSort());
		pw.println(RelationFactory.getInstance().printCatalog());
		pw.println(ConstantFactory.getInstance().printConstants());
		pw.println(SortFactory.getInstance().printSortList());
		pw.println(RelationFactory.getInstance().printFunctions());
		pw.println(ConstantFactory.getInstance().printConstantList());
		pw.println(RelationFactory.getInstance().printRepository());
		pw.println(CaseVariableFactory.getInstance().printCaseVariables());

	}
	public void printInitialization() {
		pw.print(":initial\n:var x\n:cnj ");
		pw.println(RelationFactory.getInstance().initialize() + " " + CaseVariableFactory.getInstance().initialize() + "\n");
	}
	
	public void generateMCMT() throws InvalidInputException, UnmatchingSortException, EevarOverflowException {
		printDeclaration();
		printInitialization();
		pw.println(":u_cnj " + this.safety_formula.getMCMT() + "\n");
		pw.println(EevarManager.printEevar());
		pw.println(this.process_mcmt_generation());
		pw.close();
	}

}
