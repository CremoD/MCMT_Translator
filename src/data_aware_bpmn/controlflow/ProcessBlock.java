package data_aware_bpmn.controlflow;

import data_aware_bpmn.dataschema.CaseVariableFactory;
import data_aware_bpmn.dataschema.ConjunctiveSelectQuery;
import data_aware_bpmn.dataschema.InsertTransition;
import data_aware_bpmn.dataschema.SortFactory;
import data_aware_bpmn.exception.EevarOverflowException;
import data_aware_bpmn.exception.InvalidInputException;
import data_aware_bpmn.exception.UnmatchingSortException;

public class ProcessBlock extends Block{
	// possibility of having a start event with set update
	private InsertTransition set_trans = null;
	
	public ProcessBlock (String name) {
		this.name = name;
		this.sub_blocks = new Block[1];
		this.life_cycle = CaseVariableFactory.getInstance().getCaseVariable("lifecycle_" + name, SortFactory.getInstance().getSort("String_sort"), true);
		this.life_cycle.setLifeCycle(1);
	}
	
	public ProcessBlock (String name, InsertTransition ins) {
		this.name = name;
		this.set_trans = ins;
		this.sub_blocks = new Block[1];
		this.life_cycle = CaseVariableFactory.getInstance().getCaseVariable("lifecycle_" + name, SortFactory.getInstance().getSort("String_sort"), true);
	}
	
	public void addB1 (Block b1) {
		this.sub_blocks[0] = b1;
	}
	
	public void setEventTransition(InsertTransition ins) {
		this.set_trans = ins;
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

		// second part: B1 COMPLETED --> B1 IDLE and itself completed
		ConjunctiveSelectQuery secondG = new ConjunctiveSelectQuery();
		secondG.addBinaryCondition(true, this.sub_blocks[0].life_cycle, "Completed");
		InsertTransition secondU = new InsertTransition(this.name + " second translation", secondG);
		secondU.set(this.sub_blocks[0].life_cycle, "Idle");
		secondU.set(this.life_cycle, "Completed");

		
		// generate MCMT translation
		// check if there is a set update in the start event
		if (set_trans != null) {
			result += set_trans.generateMCMT() + "\n" + firstU.generateMCMT() + "\n" + secondU.generateMCMT() + "\n";
		}
		else 
			result += firstU.generateMCMT() + "\n" + secondU.generateMCMT() + "\n";

		return result;
		
	}
}
