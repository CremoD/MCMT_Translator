package ControlFlow;

import DataSchema.CaseVariableFactory;
import DataSchema.ConjunctiveSelectQuery;
import DataSchema.InsertTransition;
import DataSchema.SortFactory;
import Exception.EevarOverflowException;
import Exception.InvalidInputException;
import Exception.UnmatchingSortException;

public class DeferredChoiceBlock extends Block{

	
	public DeferredChoiceBlock (String name) {
		this.name = name;
		this.sub_blocks = new Block[2];
		this.life_cycle = CaseVariableFactory.getInstance().getCaseVariable("lifecycle" + name, SortFactory.getInstance().getSort("String_sort"), true);
	}
	
	public void addB1 (Block b1) {
		this.sub_blocks[0] = b1;
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

		// second part: itself ENABLED --> B2 ENABLED and itself ACTIVE
		ConjunctiveSelectQuery secondG = new ConjunctiveSelectQuery();
		secondG.addBinaryCondition(true, this.life_cycle, "Enabled");
		InsertTransition secondU = new InsertTransition(this.name + " second translation", secondG);
		secondU.set(this.life_cycle, "Active");
		secondU.set(this.sub_blocks[1].life_cycle, "Enabled");
		

		// third part: B1 completed --> B1 IDLE and itself COMPLETED
		ConjunctiveSelectQuery thirdG = new ConjunctiveSelectQuery();
		thirdG.addBinaryCondition(true, this.sub_blocks[0].life_cycle, "Completed");
		InsertTransition thirdU = new InsertTransition(this.name + " third translation", thirdG);
		thirdU.set(this.life_cycle, "Completed");
		thirdU.set(this.sub_blocks[0].life_cycle, "Idle");
		
		// fourth part:  B2 completed --> B2 IDLE and itself COMPLETED
		ConjunctiveSelectQuery fourthG = new ConjunctiveSelectQuery();
		fourthG.addBinaryCondition(true, this.sub_blocks[1].life_cycle, "Completed");
		InsertTransition fourthU = new InsertTransition(this.name + " fourth translation", fourthG);
		fourthU.set(this.sub_blocks[1].life_cycle, "Idle");
		fourthU.set(this.life_cycle, "Completed");
		
		

		// generate MCMT translation
		result += firstU.generateMCMT() + "\n" + secondU.generateMCMT() + "\n" + thirdU.generateMCMT() + "\n"+ fourthU.generateMCMT() + "\n";

		return result;
		
	}
}
