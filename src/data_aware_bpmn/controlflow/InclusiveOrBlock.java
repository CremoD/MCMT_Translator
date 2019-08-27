package data_aware_bpmn.controlflow;

import data_aware_bpmn.dataschema.CaseVariableFactory;
import data_aware_bpmn.dataschema.ConjunctiveSelectQuery;
import data_aware_bpmn.dataschema.InsertTransition;
import data_aware_bpmn.dataschema.SortFactory;
import data_aware_bpmn.exception.EevarOverflowException;
import data_aware_bpmn.exception.InvalidInputException;
import data_aware_bpmn.exception.UnmatchingSortException;

public class InclusiveOrBlock extends Block{
	private ConjunctiveSelectQuery cond1;
	private ConjunctiveSelectQuery cond2;
	
	public InclusiveOrBlock (String name) {
		this.name = name;
		this.sub_blocks = new Block[2];
		this.life_cycle = CaseVariableFactory.getInstance().getCaseVariable("lifecycle_" + name, SortFactory.getInstance().getSort("String_sort"), true);
	}
	
	public void addB1 (Block b1) {
		this.sub_blocks[0] = b1;
	}
	
	public void addB2 (Block b2) {
		this.sub_blocks[1] = b2;
	}
	
	public void addCond1 (ConjunctiveSelectQuery cond1) {
		this.cond1 = cond1;
	}
	
	public void addCond2 (ConjunctiveSelectQuery cond2) {
		this.cond2 = cond2;
	}
	
	@Override
	public String mcmt_translation() throws InvalidInputException, UnmatchingSortException, EevarOverflowException {
		// TODO Auto-generated method stub
		String result = "";

		// first part: itself ENABLED and cond1 and NOT cond2 --> B1 ENABLED and itself ACTIVE1
		ConjunctiveSelectQuery firstG = new ConjunctiveSelectQuery();
		firstG.addBinaryCondition(true, this.life_cycle, "Enabled");
		InsertTransition firstU = new InsertTransition(this.name + " first translation", firstG);
		firstU.addTaskGuard(cond1.getMCMT());
		firstU.addTaskGuard(cond2.getNegated_mcmt());
		firstU.set(this.life_cycle, "Active1");
		firstU.set(this.sub_blocks[0].life_cycle, "Enabled");

		// second part: itself ENABLED and NOT cond1 and cond2 --> B2 ENABLED and itself ACTIVE1
		ConjunctiveSelectQuery secondG = new ConjunctiveSelectQuery();
		secondG.addBinaryCondition(true, this.life_cycle, "Enabled");
		InsertTransition secondU = new InsertTransition(this.name + " second translation", secondG);
		secondU.addTaskGuard(cond2.getMCMT());
		secondU.addTaskGuard(cond1.getNegated_mcmt());
		secondU.set(this.life_cycle, "Active1");
		secondU.set(this.sub_blocks[1].life_cycle, "Enabled");
		

		// third part: itself ENABLED and BOTH cond1 and cond2 --> B1 ENABLED, B2 ENABLED and itself ACTIVE2
		ConjunctiveSelectQuery thirdG = new ConjunctiveSelectQuery();
		thirdG.addBinaryCondition(true, life_cycle, "Enabled");
		InsertTransition thirdU = new InsertTransition(this.name + " third translation", thirdG);
		thirdU.addTaskGuard(cond1.getMCMT());
		thirdU.addTaskGuard(cond2.getMCMT());
		thirdU.set(this.life_cycle, "Active2");
		thirdU.set(this.sub_blocks[0].life_cycle, "Enabled");
		thirdU.set(this.sub_blocks[1].life_cycle, "Enabled");
		
		// fourth part: B1 completed and itself ACTIVE1 --> B1 idle and itself completed
		ConjunctiveSelectQuery fourthG = new ConjunctiveSelectQuery();
		fourthG.addBinaryCondition(true, this.sub_blocks[0].life_cycle, "Completed");
		fourthG.addBinaryCondition(true, this.life_cycle, "Active1");
		InsertTransition fourthU = new InsertTransition(this.name + " fourth translation", fourthG);
		fourthU.set(this.sub_blocks[0].life_cycle, "Idle");
		fourthU.set(this.life_cycle, "Completed");
		
		// fifth part: B2 completed and itself ACTIVE1 --> B2 idle and itself completed
		ConjunctiveSelectQuery fifthG = new ConjunctiveSelectQuery();
		fifthG.addBinaryCondition(true, this.sub_blocks[1].life_cycle, "Completed");
		fifthG.addBinaryCondition(true, this.life_cycle, "Active1");
		InsertTransition fifthU = new InsertTransition(this.name + " fifth translation", fifthG);
		fifthU.set(this.sub_blocks[1].life_cycle, "Idle");
		fifthU.set(this.life_cycle, "Completed");
		
		// sixth part: B1 and B2 completed and itself ACTIVE2 --> B1 and B2 idle and itself completed
		ConjunctiveSelectQuery sixthG = new ConjunctiveSelectQuery();
		sixthG.addBinaryCondition(true, this.sub_blocks[0].life_cycle, "Completed");
		sixthG.addBinaryCondition(true, this.sub_blocks[1].life_cycle, "Completed");
		sixthG.addBinaryCondition(true, this.life_cycle, "Active2");
		InsertTransition sixthU = new InsertTransition(this.name + " sixth translation", sixthG);
		sixthU.set(this.sub_blocks[0].life_cycle, "Idle");
		sixthU.set(this.sub_blocks[1].life_cycle, "Idle");
		sixthU.set(this.life_cycle, "Completed");

		// generate MCMT translation
		result += firstU.generateMCMT() + "\n" + secondU.generateMCMT() + "\n" + thirdU.generateMCMT() + "\n"+ fourthU.generateMCMT() + "\n"+ fifthU.generateMCMT() + "\n"+ sixthU.generateMCMT() + "\n";

		return result;
		
	}
}
