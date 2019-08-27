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

public class ForwardExceptionBlock extends Block{
	private Constant error;
	
	public ForwardExceptionBlock (String name) {
		this.name = name;
		this.sub_blocks = new Block[3];
		this.life_cycle = CaseVariableFactory.getInstance().getCaseVariable("lifecycle_" + name, SortFactory.getInstance().getSort("String_sort"), true);
	}
	
		
	public void addB1 (Block b1) {
		this.sub_blocks[0] = b1;
		// add constant error
		this.error = ConstantFactory.getInstance().getConstant("Error" + b1.name, SortFactory.getInstance().getSort("String_sort"));
	}
	
	public void addB2 (Block b2) {
		this.sub_blocks[1] = b2;
	}
	
	public void addB3 (Block b3) {
		this.sub_blocks[2] = b3;
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

		// second part: B1 completed --> B1 IDLE and B2 ENABLED 
		ConjunctiveSelectQuery secondG = new ConjunctiveSelectQuery();
		secondG.addBinaryCondition(true, this.sub_blocks[0].life_cycle, "Completed");
		InsertTransition secondU = new InsertTransition(this.name + " second translation", secondG);
		secondU.set(this.sub_blocks[0].life_cycle, "Idle");
		secondU.set(this.sub_blocks[1].life_cycle, "Enabled");
		
		// third part: B2 COMPLETED --> B2 IDLE and itself COMPLETED
		ConjunctiveSelectQuery thirdG = new ConjunctiveSelectQuery();
		thirdG.addBinaryCondition(true, this.sub_blocks[1].life_cycle, "Completed");
		InsertTransition thirdU = new InsertTransition(this.name + " third translation", thirdG);
		thirdU.set(this.life_cycle, "Completed");
		thirdU.set(this.sub_blocks[1].life_cycle, "Idle");
		
		// fourth part: B1 error --> B1 IDLE and B3 ENABLED
		ConjunctiveSelectQuery fourthG = new ConjunctiveSelectQuery();
		fourthG.addBinaryCondition(true, this.sub_blocks[0].life_cycle, this.error.getName());
		InsertTransition fourthU = new InsertTransition(this.name + " fourth translation", fourthG);
		fourthU.set(this.sub_blocks[0].life_cycle, "Idle");
		fourthU.set(this.sub_blocks[2].life_cycle, "Enabled");
		
		// fifth part:  B3 completed --> B3 IDLE and itself COMPLETED
		ConjunctiveSelectQuery fifthG = new ConjunctiveSelectQuery();
		fifthG.addBinaryCondition(true, this.sub_blocks[2].life_cycle, "Completed");
		InsertTransition fifthU = new InsertTransition(this.name + " fifth translation", fifthG);
		fifthU.set(this.sub_blocks[2].life_cycle, "Idle");
		fifthU.set(this.life_cycle, "Completed");
		
		// sixth part, non deterministic error
		ConjunctiveSelectQuery sixthG = new ConjunctiveSelectQuery();
		sixthG.addBinaryCondition(true, this.sub_blocks[0].life_cycle, "Enabled");
		InsertTransition nondeterministicU = new InsertTransition(this.name + " sixth translation", sixthG);
		nondeterministicU.set(this.sub_blocks[0].life_cycle, this.error.getName());
		start_propagation(this.sub_blocks[0], nondeterministicU);
		
		

		// generate MCMT translation
		result += firstU.generateMCMT() + "\n" + secondU.generateMCMT() + "\n" + thirdU.generateMCMT() + "\n"+ fourthU.generateMCMT() + "\n" + fifthU.generateMCMT() + "\n" + nondeterministicU.generateMCMT() + "\n";
		nondeterministicU.setGuard("(= " + this.sub_blocks[0].life_cycle.getName() + " Active)");
		nondeterministicU.setName(this.name + " seventh translation");
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
