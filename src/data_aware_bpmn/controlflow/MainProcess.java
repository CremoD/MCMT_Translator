package data_aware_bpmn.controlflow;

import data_aware_bpmn.exception.EevarOverflowException;
import data_aware_bpmn.exception.InvalidInputException;
import data_aware_bpmn.exception.UnmatchingSortException;

public class MainProcess {
	private ProcessBlock root;
	private String total_mcmt = "";
	
	public MainProcess (ProcessBlock process_block) {
		this.root = process_block;
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

}
