package data_aware_bpmn.controlflow;

import data_aware_bpmn.dataschema.CaseVariableFactory;
import data_aware_bpmn.dataschema.ConjunctiveSelectQuery;
import data_aware_bpmn.dataschema.Constant;
import data_aware_bpmn.dataschema.ConstantFactory;
import data_aware_bpmn.dataschema.InsertTransition;
import data_aware_bpmn.dataschema.SortFactory;
import data_aware_bpmn.exception.EevarOverflowException;
import data_aware_bpmn.exception.InvalidInputException;
import data_aware_bpmn.exception.UnmatchingSortException;

public class BackwardExceptionBlock extends Block{

	private Constant error;
	
	public BackwardExceptionBlock (String name) {
		this.name = name;
		this.sub_blocks = new Block[2];
		this.life_cycle = CaseVariableFactory.getInstance().getCaseVariable("lifecycle_" + name, SortFactory.getInstance().getSort("String_sort"), true);
		this.life_cycle.setLifeCycle(1);
	}
	
		
	public void addB1 (Block b1) {
		this.sub_blocks[0] = b1;
		// add constant error
		this.error = ConstantFactory.getInstance().getConstant("Error" + b1.name, SortFactory.getInstance().getSort("String_sort"));
	}
	
	public void addB2 (Block b2) {
		this.sub_blocks[1] = b2;
	}
	
	
	@Override
	public String mcmt_translation() throws InvalidInputException, UnmatchingSortException, EevarOverflowException {
		// TODO Auto-generated method stub
		String result = "";

		// first part: itself ENABLED --> B1 ENABLED and itself ACTIVE
		ConjunctiveSelectQuery firstG = new ConjunctiveSelectQuery();
		firstG.addBinaryCondition(true, this.life_cycle, "Enabled");
		InsertTransition firstU = new InsertTransition(this.name + " first translation", firstG);
		firstU.set(this.life_cycle, "Active");
		firstU.set(this.sub_blocks[0].life_cycle, "Enabled");

		// second part: B1 completed --> B1 IDLE and itself COMPLETED 
		ConjunctiveSelectQuery secondG = new ConjunctiveSelectQuery();
		secondG.addBinaryCondition(true, this.sub_blocks[0].life_cycle, "Completed");
		InsertTransition secondU = new InsertTransition(this.name + " second translation", secondG);
		secondU.set(this.life_cycle, "Completed");
		secondU.set(this.sub_blocks[0].life_cycle, "Idle");
		

		// third part: B1 error --> B1 IDLE and B2 ENABLED
		ConjunctiveSelectQuery thirdG = new ConjunctiveSelectQuery();
		thirdG.addBinaryCondition(true, this.sub_blocks[0].life_cycle, error.getName());
		InsertTransition thirdU = new InsertTransition(this.name + " third translation", thirdG);
		thirdU.set(this.sub_blocks[0].life_cycle, "Idle");
		thirdU.set(this.sub_blocks[1].life_cycle, "Enabled");
		
		// fourth part:  B2 completed --> B1 enabled B2 idle
		ConjunctiveSelectQuery fourthG = new ConjunctiveSelectQuery();
		fourthG.addBinaryCondition(true, this.sub_blocks[1].life_cycle, "Completed");
		InsertTransition fourthU = new InsertTransition(this.name + " fourth translation", fourthG);
		fourthU.set(this.sub_blocks[1].life_cycle, "Idle");
		fourthU.set(this.sub_blocks[0].life_cycle, "Enabled");
		
		// fifth part, non deterministic error
		ConjunctiveSelectQuery fifthG = new ConjunctiveSelectQuery();
		fifthG.addBinaryCondition(true, this.sub_blocks[0].life_cycle, "Enabled");
		InsertTransition nondeterministicU = new InsertTransition(this.name + " fifth translation", fifthG);
		nondeterministicU.set(this.sub_blocks[0].life_cycle, this.error.getName());
		start_propagation(this.sub_blocks[0], nondeterministicU);
		
		

		// generate MCMT translation
		result += firstU.generateMCMT() + "\n" + secondU.generateMCMT() + "\n" + thirdU.generateMCMT() + "\n"+ fourthU.generateMCMT() + "\n" + nondeterministicU.generateMCMT() + "\n";
		nondeterministicU.setGuard("(= " + this.sub_blocks[0].life_cycle.getName() + " Active)");
		nondeterministicU.setName(this.name + " sixth translation");
		result += nondeterministicU.generateMCMT() + "\n";


		return result;
		
	}
	
	public void start_propagation(Block handler, InsertTransition transition) throws InvalidInputException, UnmatchingSortException {
		for (Block sub : handler.sub_blocks) 
			propagateError(sub, transition);
	}
	
	public void propagateError(Block current, InsertTransition transition) throws InvalidInputException, UnmatchingSortException {
		if (current instanceof Task) {
			transition.set(current.life_cycle, "Idle");
			return;
		}
		transition.set(current.life_cycle, "Idle");
		for (Block sub : current.sub_blocks) 
			propagateError(sub, transition);
		
	}
}
