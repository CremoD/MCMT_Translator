package data_aware_bpmn.controlflow;

import data_aware_bpmn.dataschema.CaseVariableFactory;
import data_aware_bpmn.dataschema.ConjunctiveSelectQuery;
import data_aware_bpmn.dataschema.ConstantFactory;
import data_aware_bpmn.dataschema.InsertTransition;
import data_aware_bpmn.dataschema.SortFactory;
import data_aware_bpmn.exception.EevarOverflowException;
import data_aware_bpmn.exception.InvalidInputException;
import data_aware_bpmn.exception.UnmatchingSortException;

public class ErrorBlock extends Block{

	private ConjunctiveSelectQuery cond;
	private Block handler;
	
	public ErrorBlock (String name, Block handler) {
		this.name = name;
		this.handler = handler;
		this.sub_blocks = new Block[1];
		this.life_cycle = CaseVariableFactory.getInstance().getCaseVariable("lifecycle_" + name, SortFactory.getInstance().getSort("String_sort"), true);
		this.life_cycle.setLifeCycle(1);
	}
	
	public ErrorBlock (String name, Block handler, ConjunctiveSelectQuery cond) {
		this.name = name;
		this.handler = handler;
		this.cond = cond;
		this.sub_blocks = new Block[1];
		this.life_cycle = CaseVariableFactory.getInstance().getCaseVariable("lifecycle_" + name, SortFactory.getInstance().getSort("String_sort"), true);
	}
	
	public void addB1 (Block b1) {
		this.sub_blocks[0] = b1;
	}
	
	public void addCond (ConjunctiveSelectQuery cond1) {
		this.cond = cond1;
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

		// second part: B1 completed and cond TRUE --> B1 IDLE and itself COMPLETED 
		ConjunctiveSelectQuery secondG = new ConjunctiveSelectQuery();
		secondG.addBinaryCondition(true, this.sub_blocks[0].life_cycle, "Completed");
		InsertTransition secondU = new InsertTransition(this.name + " second translation", secondG);
		secondU.addTaskGuard(cond.getMCMT());
		secondU.set(this.life_cycle, "Completed");
		secondU.set(this.sub_blocks[0].life_cycle, "Idle");
		

		// third part: B1 completed and cond FALSE --> B1 idle, Handler error, sub_blocks of handler idle
		ConjunctiveSelectQuery thirdG = new ConjunctiveSelectQuery();
		thirdG.addBinaryCondition(true, this.sub_blocks[0].life_cycle, "Completed");
		InsertTransition thirdU = new InsertTransition(this.name + " third translation", thirdG);
		thirdU.addTaskGuard(cond.getNegated_mcmt());
		start_propagation(handler, thirdU);		
		
		

		// generate MCMT translation
		result += firstU.generateMCMT() + "\n" + secondU.generateMCMT() + "\n" + thirdU.generateMCMT() + "\n";

		return result;
		
	}
	
	public void start_propagation(Block handler, InsertTransition transition) throws InvalidInputException, UnmatchingSortException {
		transition.set(handler.life_cycle, ConstantFactory.getInstance().getConstant("Error" + handler.sub_blocks[0].name, SortFactory.getInstance().getSort("String_sort")).getName());
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
