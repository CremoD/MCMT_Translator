package data_aware_bpmn.controlflow;

import data_aware_bpmn.dataschema.CaseVariableFactory;
import data_aware_bpmn.dataschema.ConjunctiveSelectQuery;
import data_aware_bpmn.dataschema.ConstantFactory;
import data_aware_bpmn.dataschema.InsertTransition;
import data_aware_bpmn.dataschema.SortFactory;
import data_aware_bpmn.exception.EevarOverflowException;
import data_aware_bpmn.exception.InvalidInputException;
import data_aware_bpmn.exception.UnmatchingSortException;

public class ErrorEventBlock extends Block{

	private Block handler;
	
	public ErrorEventBlock (String name, Block handler) {
		this.name = name;
		this.handler = handler;
		this.sub_blocks = new Block[2];
		this.life_cycle = CaseVariableFactory.getInstance().getCaseVariable("lifecycle_" + name, SortFactory.getInstance().getSort("String_sort"), true);
	}
	
	
	public void addB1 (Event b1) {
		this.sub_blocks[0] = b1;
	}
	
	public void addB2 (Event b2) {
		this.sub_blocks[1] = b2;
	}
	
	
		
	@Override
	public String mcmt_translation() throws InvalidInputException, UnmatchingSortException, EevarOverflowException {
		// TODO Auto-generated method stub
		String result = "";

		// first part: event B1 enabled --> B1 COMPLETED and UPDATE
		InsertTransition firstU = null;
		if (((Event)this.sub_blocks[0]).hasEffect()) {
			firstU = ((Event)this.sub_blocks[0]).getTransition();
			firstU.addTaskGuard("(= " + this.sub_blocks[0].life_cycle.getName() + " Enabled)");
			firstU.set(this.sub_blocks[0].life_cycle, "Completed");
		}
		else {
			ConjunctiveSelectQuery firstG = new ConjunctiveSelectQuery();
			firstG.addBinaryCondition(true, this.sub_blocks[0].life_cycle, "Enabled");
			firstU = new InsertTransition(this.name + " first translation", firstG);
			firstU.set(this.sub_blocks[0].life_cycle, "Completed");
		}

		// second part: event B1 enabled --> Error propagation and possible update 
		InsertTransition secondU = null;
		if (((Event)this.sub_blocks[1]).hasEffect()) {
			secondU = ((Event)this.sub_blocks[1]).getTransition();
			start_propagation(this.handler, secondU);
		}
		else {
			ConjunctiveSelectQuery secondG = new ConjunctiveSelectQuery();
			secondG.addBinaryCondition(true, this.sub_blocks[1].life_cycle, "Enabled");
			secondU = new InsertTransition (this.name + " second translation", secondG);
			start_propagation(this.handler, secondU);
		}
		
	
		// generate MCMT translation
		result += firstU.generateMCMT() + "\n" + secondU.generateMCMT() + "\n";

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
