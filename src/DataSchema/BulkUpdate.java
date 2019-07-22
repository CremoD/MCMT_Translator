package DataSchema;

import java.util.HashMap;

import Exception.InvalidInputException;

public class BulkUpdate {
	
	private ConjunctiveSelectQuery precondition;
	private HashMap<String, String> eevar_association;
	private String name;
	private String guard;
	private String local_update = "";
	private String local_static = "";
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
	
	
	
	
	// method for adding "false" child to a Node
	public void conditionFalse(BulkCondition node) {

	}
	
	
	// method to print the complete insert transition
	public String generateMCMT() {
		String final_mcmt = ":comment " + this.name + "\n:transition\n:var j\n";
		// control of the indexes
		if (this.precondition.isIndex_present()) 
			final_mcmt += ":var x\n";	
		final_mcmt += ":guard " + this.guard + "\n";

		

		return final_mcmt;
	}

}
