package DataSchema;

import java.util.HashMap;

public class BulkUpdate {
	
	private ConjunctiveSelectQuery precondition;
	private HashMap<String, String> eevar_association;
	private HashMap<CaseVariable, String> set_table; 
	private String name;
	private String guard;
	private String local_update = "";
	private String local_static = "";
	private RepositoryRelation toUpdate;
	Node root;
	
	// INTERNAL NODE CLASS
	static class Node {
		Node true_node, false_node;
		String actualCondition;
		// TODO condition
		
	}
	
	// constructor
	public BulkUpdate(String name, ConjunctiveSelectQuery precondition, RepositoryRelation toUpdate) {
		this.precondition = precondition;
		this.eevar_association = precondition.getRef_manager();
		this.guard = precondition.getMCMT();
		this.set_table = new HashMap<CaseVariable, String>();
		this.name = name;
	}
	
	// method for adding condition
	public void addCondition() {
		
	}
	
	// method for adding "true" child to a Node
	public void conditionTrue (Node node) {
		
	}
	
	// method for adding "false" child to a Node
	public void conditionFalse(Node node) {

	}

}
