package DataSchema;

import java.util.HashMap;

import Exception.InvalidInputException;

public class BulkUpdate {
	
	private ConjunctiveSelectQuery precondition;
	private HashMap<String, String> eevar_association;
	private String name;
	private String guard;
	private String final_mcmt = "";
	private RepositoryRelation toUpdate;
	BulkCondition root;
	
	// constructor
	public BulkUpdate(String name, ConjunctiveSelectQuery precondition, RepositoryRelation toUpdate) throws InvalidInputException {
		this.precondition = precondition;
		this.eevar_association = precondition.getRef_manager();
		this.guard = precondition.getMCMT();
		this.name = name;
		this.toUpdate = toUpdate;
		this.root = new BulkCondition(toUpdate);
		this.root.setEevar_association(this.eevar_association);
	}
	
	
	// method for getting once the string representing the list of immutated case variables
	public String globalStatic() {
		String result = "";
		for (CaseVariable cv : CaseVariableFactory.casevariable_list.values()) 
				result += ":val " + cv.getName() + "\n";
		
		return result;

		
	}
	
	
	
	// method to print the complete insert transition
	public String generateMCMT() throws InvalidInputException {
		final_mcmt += ":comment " + this.name + "\n:transition\n:var j\n";
		// control of the indexes
		if (this.precondition.isIndex_present()) 
			final_mcmt += ":var x\n";	
		final_mcmt += ":guard " + this.guard + "\n";
		
		//here to iterate through the three and print corresponding declaration
		showTree();

		

		return final_mcmt;
	}
	
	public void showTree() throws InvalidInputException {
		showTree(this.root);
	}
	
	public void showTree(BulkCondition node) throws InvalidInputException {
		//base case
		if (node.isLeaf()) {
			final_mcmt += node.getCondition();
			final_mcmt += node.getMcmt_local_update() + "\n";
			final_mcmt += this.globalStatic() + "\n";
			return;
		}
		
		showTree(node.true_node);
		showTree(node.false_node);
	}

	
	
	// getter and setter
	public ConjunctiveSelectQuery getPrecondition() {
		return precondition;
	}


	public void setPrecondition(ConjunctiveSelectQuery precondition) {
		this.precondition = precondition;
	}


	public HashMap<String, String> getEevar_association() {
		return eevar_association;
	}


	public void setEevar_association(HashMap<String, String> eevar_association) {
		this.eevar_association = eevar_association;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getGuard() {
		return guard;
	}


	public void setGuard(String guard) {
		this.guard = guard;
	}


	public String getFinal_mcmt() {
		return final_mcmt;
	}


	public void setFinal_mcmt(String final_mcmt) {
		this.final_mcmt = final_mcmt;
	}


	public RepositoryRelation getToUpdate() {
		return toUpdate;
	}


	public void setToUpdate(RepositoryRelation toUpdate) {
		this.toUpdate = toUpdate;
	}
	
	
}
