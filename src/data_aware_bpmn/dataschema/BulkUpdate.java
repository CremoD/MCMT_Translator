package data_aware_bpmn.dataschema;

import java.util.HashMap;

import data_aware_bpmn.exception.InvalidInputException;
import data_aware_bpmn.exception.UnmatchingSortException;

/**
 * Class representing the conditional update. It is designed conceptually as a tree structure, made by different 
 * inner nodes (conditions) and leaves (set operation on considered repository relation to update)
 * @author DavideCremonini
 *
 */
public class BulkUpdate implements Transition{
	
	private ConjunctiveSelectQuery precondition;
	private HashMap<String, String> eevar_association;
	private String name;
	private CaseVariable toSet; // only case in which there is a case variable to be changed, the lifecycle of current task
	private String guard;
	private String final_mcmt = "";
	private int numcases = 0;
	private String case_update = "";
	private RepositoryRelation toUpdate;
	BulkCondition root;
	
	/**
	 * Constructor of the class. The precondition guard defines the used eevars and MCMT guard translation. The root of the tree is defined.
	 * @param name of the transition
	 * @param precondition guard of the transition
	 * @param toUpdate repository relation to be updated
	 */
	public BulkUpdate(String name, ConjunctiveSelectQuery precondition, RepositoryRelation toUpdate) throws InvalidInputException {
		this.precondition = precondition;
		this.eevar_association = precondition.getRef_manager();
		this.guard = precondition.getMCMT();
		this.name = name;
		this.toUpdate = toUpdate;
		this.root = new BulkCondition(toUpdate);
		this.root.setEevar_association(this.eevar_association);
	}
	
	/**
	 *  Set method for setting the control case variables indicated the lifecycle state of the current task
	 *  @param cv case variable to be set
	 *  @param new_value value to assign to the case variable
	 */
	public void set(CaseVariable cv, String new_value) throws InvalidInputException, UnmatchingSortException {
		this.toSet = cv;
	}
	
	
	/**
	 * Method for getting once the string representing the list of immutated case variables. Since the update 
	 * involves only the repository relation, all case variables remain the same except from the control case variable
	 * @return the String representing the values of the case variables after update
	 */
	public String globalStatic() {
		String result = "";
		for (CaseVariable cv : CaseVariableFactory.casevariable_list.values()) {
			if (cv == this.toSet)
				result += ":val Completed\n";
			else 
				result += ":val " + cv.getName() + "\n";
		}
		
		return result;	
	}
	
	/**
	 * Method to print the complete transition translation
	 * @return String representing the transition translation
	 */
	public String generateMCMT() throws InvalidInputException {
		final_mcmt += ":comment " + this.name + "\n:transition\n:var j\n";
		// control of the indexes
		if (this.precondition.isIndex_present()) 
			final_mcmt += ":var x\n";	
		final_mcmt += ":guard " + this.guard + "\n";
		
		//here to iterate through the three and print corresponding declaration
		showTree();
		final_mcmt += ":numcases " + numcases + "\n";
		final_mcmt += case_update;

		return final_mcmt;
	}
	
	/**
	 * Method that is invoked to start the recursive algorithm for traversing the tree
	 */
	public void showTree() throws InvalidInputException {
		showTree(this.root, "");
	}
	
	/**
	 * Recursive method that performs DFS algorithm to process the tree.
	 * @param node current node considered
	 * @param condition accumulator variable, keep trace of conditions along the path visited
	 */
	public void showTree(BulkCondition node, String condition) throws InvalidInputException {
		//base case
		if (node.isLeaf()) {
			case_update += ":case" + condition + "\n";
			case_update += node.getLocalUpdate() + "\n";
			case_update += this.globalStatic() + "\n";
			numcases++;
			return;
		}
		
		showTree(node.true_node, condition + node.getCondition());
		for (String s : node.getFalse_list()) {
			showTree(node.false_node, condition + s);
		}
	}

	
	/**
	 * Method for getting the precondition of conditional update
	 * @return precondition of conditional update
	 */
	public ConjunctiveSelectQuery getPrecondition() {
		return precondition;
	}

	/**
	 * Method for setting the precondition of conditional update
	 * @param precondition of conditional update to set
	 */
	public void setPrecondition(ConjunctiveSelectQuery precondition) {
		this.precondition = precondition;
	}

	/**
	 * Method for getting the HashMap referring to eevar association of this update
	 * @return HashMap referring to eevar association of this update
	 */
	public HashMap<String, String> getEevar_association() {
		return eevar_association;
	}

	/**
	 * Method for setting the HashMap referring to eevar association of this update
	 * @param eevar_association of this update to set
	 */
	public void setEevar_association(HashMap<String, String> eevar_association) {
		this.eevar_association = eevar_association;
	}

	/**
	 * Method for getting the name of the conditional update transition
	 * @return name of the conditional update transition
	 */
	public String getName() {
		return name;
	}

	/**
	 * Method for setting the name of the conditional update transition
	 * @param name of the conditional update transition to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Method for getting MCMT translation of precondition, representing the guard
	 * @return MCMT translation of precondition, representing the guard
	 */
	public String getGuard() {
		return guard;
	}

	
	/**
	 * Method for setting the MCMT translation of precondition, representing the guard
	 * @param guard MCMT translation of precondition to be set
	 */
	public void setGuard(String guard) {
		this.guard = guard;
	}

	/**
	 * Method for getting MCMT translation of the conditional update
	 * @return MCMT translation of the conditional update
	 */
	public String getFinal_mcmt() {
		return final_mcmt;
	}


	/**
	 * Method for setting the MCMT translation of the conditional update
	 * @param final_mcmt MCMT translation of the conditional update to be set 
	 */
	public void setFinal_mcmt(String final_mcmt) {
		this.final_mcmt = final_mcmt;
	}

	/**
	 * Method for getting the repository relation to update
	 * @return repository relation to update
	 */
	public RepositoryRelation getToUpdate() {
		return toUpdate;
	}


	/**
	 * Method for setting the repository relation to update
	 * @param toUpdate repository relation to be set
	 */
	public void setToUpdate(RepositoryRelation toUpdate) {
		this.toUpdate = toUpdate;
	}


	@Override
	/**
	 * Method for modifying the task guard adding some conditions. Useful in the process schema translation, for adding 
	 * information about control case variables.
	 * @param toAdd String representing the condition to add 
	 */
	public void addTaskGuard(String toAdd) {
		// TODO Auto-generated method stub
		this.guard += toAdd;
	}
	
	
}
