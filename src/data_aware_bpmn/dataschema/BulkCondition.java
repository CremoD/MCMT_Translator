package data_aware_bpmn.dataschema;



import java.util.ArrayList;
import java.util.HashMap;

import data_aware_bpmn.exception.InvalidInputException;
import data_aware_bpmn.exception.UnmatchingSortException;

/**
 * Class representing the node of the bulk update tree, implementing the tree structure of the conditional update of DABs.
 * Each Node is either an inner node, if it is characterized by a condition, or a leaf node, if it is characterized by a set rule.
 * Each condition added will have impact on the cases of the MCMT transitions.
 * @author DavideCremonini
 *
 */
public class BulkCondition {
	
	private RepositoryRelation relationToUpdate;
	private String condition = "";
	private String negated_condition = "";
	private boolean isLeaf = false;
	BulkCondition true_node;
	BulkCondition false_node;
	private ArrayList<String> false_list;
	private HashMap<String, String> eevar_association;
	private HashMap<String, String> set_table;

	/**
	 * Constructor of the Bulk condition
	 * @param relationToUpdate indicates the relation subject of the update effect 
	 */
	public BulkCondition (RepositoryRelation relationToUpdate) throws InvalidInputException {
		this.relationToUpdate = relationToUpdate;
		this.set_table = new HashMap<String, String>();
		this.false_list = new ArrayList<String>();

	}
	
	/**
	 * Method that check if the attribute passed as parameter is of the relation to update and return corresponding name.
	 * @param att attribute to be checked
	 * @return changed name of the attribute to refer to MCMT local array variable
	 * @throws InvalidInputException if the attribute does not refer to correct repository relation to update
	 */
	public String check_return_Attribute (Attribute att) throws InvalidInputException {
		if (!(att.getIn_relation() instanceof RepositoryRelation) || !((RepositoryRelation)att.getIn_relation() == this.relationToUpdate))
			throw new InvalidInputException ("Attribute " + att.getName() + " should refer to correct Repository relation: " + relationToUpdate.getName());
		
		String result ="";
		for (int i = 0; i < relationToUpdate.getAttributes().size(); i++) {
			if (relationToUpdate.getAttribute(i) == att) {
				result = relationToUpdate.getName() + (i+1) + "[j]";
				break;
			}
		}
		return result;
	}
	
	/**
	 * Method for building the greater condition between attribute and another object
	 * @param att attribute of the relation to update to insert in the condition
	 * @param second object to insert in the condition
	 */
	public void addGreaterCondition (Attribute att, Object second) throws InvalidInputException {
		condition += " (> " + check_return_Attribute(att) + " " + second + ")";
		this.false_list.add(" (not (> " + check_return_Attribute(att) + " " + second + "))");
	}
	
	/**
	 * Method for building the smaller condition between attribute and another object
	 * @param att attribute of the relation to update to insert in the condition
	 * @param second object to insert in the condition
	 */
	public void addSmallerCondition (Attribute att, Object second) throws InvalidInputException {
		condition += " (< " + check_return_Attribute(att) + " " + second + ")";
		this.false_list.add(" (not (< " + check_return_Attribute(att) + " " + second + "))");
	}
	
	/**
	 * Method for building the equality condition between attribute and another object
	 * @param att attribute of the relation to update to insert in the condition
	 * @param second object to insert in the condition
	 */
	public void addEqualCondition (Attribute att, Object second) throws InvalidInputException {
		condition += " (= " + check_return_Attribute(att) + " " + second + ")";
		this.false_list.add(" (not (= " + check_return_Attribute(att) + " " + second + "))");
	}
	
	/**
	 * Method for building the condition for which a tuple of attributes is in a particular catalog relation
	 * @param cat catalog relation considered
	 * @param attributes array of attributes to check in the condition
	 * @throws InvalidInputException if there is no marching between arity of relation and number of values
	 */
	public void addInRelationCondition (CatalogRelation cat, Attribute...attributes) throws InvalidInputException {
		// first control arity
		if (cat.arity() != attributes.length) {
			throw new InvalidInputException(
					"No matching between arity of the relation and number of values. The number of values should be "
							+ cat.arity() + " instead of " + attributes.length);
		}
			
		for (int i = 0; i< attributes.length; i++) {
			if (i == 0 && attributes[i] != null) {
				condition += " (not (= " + check_return_Attribute(attributes[0]) + " null))";
			}
			else {
				if (attributes[i] != null) {
					condition += " (= (" + cat.getFunctionName((i+1), check_return_Attribute(attributes[0])) + ") " + check_return_Attribute(attributes[i]) + ")";
				}
			}
		}
	}
	
	/**
	 * Method that adds the true Child in the tree of Bulk Update
	 * @return new added true child
	 */
	public BulkCondition addTrueChild () throws InvalidInputException {
		BulkCondition new_condition = new BulkCondition(relationToUpdate);
		new_condition.setEevar_association(eevar_association);
		this.true_node = new_condition;
		return new_condition;
	}
	
	/**
	 * Method that adds the false Child in the tree of Bulk Update
	 * @return new added false child
	 */
	public BulkCondition addFalseChild () throws InvalidInputException {
		BulkCondition new_condition = new BulkCondition(relationToUpdate);
		new_condition.setEevar_association(eevar_association);
		this.false_node = new_condition;
		return new_condition;
	}
	
	/**
	 * Method that characterizes a leaf of the tree. Set attribute of the repository relation to update to new values
	 * @param att attribute to be set
	 * @param new_value to set to attribute
	 * @throws InvalidInputException if attribute to be set is not an attribute of the considered relation to update or is already set
	 */
	public void set(Attribute att, String new_value) throws InvalidInputException, UnmatchingSortException {
		// control att is of the updated repository relation
		if (((RepositoryRelation)att.getIn_relation()) != relationToUpdate) {
			throw new InvalidInputException("Attribute to set is not an attribute of the considered relation " + relationToUpdate.getName());
		}
		// control of eevar or constant new_value
		boolean eevar = isEevar(new_value);
		// control of sorts
		checkSorts(eevar, att.getSort(), new_value);

		// check attribute not already set
		if (this.set_table.containsKey(check_return_Attribute(att)))
			throw new InvalidInputException("Attribute " + att.getName() + " already set");

		this.set_table.put(check_return_Attribute(att), new_value);
		this.isLeaf = true;
	}
	
	/**
	 * Method for getting the correct mcmt translation of local update
	 * @return String representing the mcmt translation
	 */
	public String getLocalUpdate() throws InvalidInputException {
		String result="";
		// iterate through the repository relation factory and found the considered relation
		for (RepositoryRelation rep : RelationFactory.repository.values()) {
			// case in which is the updated one
			if (rep == relationToUpdate) {
				for (Attribute att : rep.getAttributes()) {
					String corresponding_name = check_return_Attribute(att);
					if (this.set_table.containsKey(corresponding_name)) {
						result += ":val " + this.set_table.get(corresponding_name) + "\n";
					}
					else {
						result += ":val " + corresponding_name + "\n";
					}
				}
			}
			
			// case in which this is not the update one
			else {
				for (int i = 0; i < rep.getAttributes().size(); i++) {
					result += ":val " + rep.getName() + (i+1) + "[j]\n";
				}
			}
		}
		
		return result;
	}
	
	
	
	/**
	 * Method that checks for matching sorts
	 * @param eevar boolean value that indicates whether the situation is applied to eevar variable
	 * @param first Sort to be checked
	 * @param second Name of the second object to check 
	 * @throws UnmatchingSortException if there is no matching between sorts
	 */
	public void checkSorts(boolean eevar, Sort first, String second) throws UnmatchingSortException {
		if (eevar && !first.getName().equals(EevarManager.getSortByVariable(eevar_association.get(second)).getName())) {
			throw new UnmatchingSortException("No matching between sorts: " + first.getName() + " VS "
					+ EevarManager.getSortByVariable(eevar_association.get(second)).getName());
		}
		if (!eevar && !first.getName().equals(ConstantFactory.constant_list.get(second).getSort().getName())) {
			throw new UnmatchingSortException("No matching between sorts: " + first.getName() + " VS "
					+ ConstantFactory.constant_list.get(second).getSort().getName());
		}
	}

	/**
	 * Method that checks whether the value of the String passed as parameter is an answer variable, i.e., eevar, or a constant.
	 * @param value to be checked
	 * @return boolean result, indicating true if the value is an eevar or false if it is a constant
	 * @throws InvalidInputException if the String value passed as parameter is neither an eevar nor a constant
	 */
	public boolean isEevar(String value) throws InvalidInputException {
		if (!eevar_association.containsKey(value)) {
			if (!ConstantFactory.constant_list.containsKey(value)) {
				throw new InvalidInputException(value + " is not an answer of the precondition or a constant");
			}
			return false;
		}
		return true;
	}
	
	
	/**
	 * Method for getting the condition 
	 * @return string representing condition
	 */
	public String getCondition () {
		return this.condition;
	}
	
	/**
	 * Method for setting the condition
	 * @param condition value of condition to be set
	 */
	public void setCondition(String condition) {
		this.condition = condition;
	}

	/**
	 * Method for getting the relation to update
	 * @return condition to update
	 */
	public RepositoryRelation getRelationToUpdate() {
		return relationToUpdate;
	}

	/**
	 * Method for setting the relation to update
	 * @param relationToUpdate value of relation to be set
	 */
	public void setRelationToUpdate(RepositoryRelation relationToUpdate) {
		this.relationToUpdate = relationToUpdate;
	}

	/**
	 * Method for getting the negated condition 
	 * @return string representing negated condition
	 */
	public String getNegated_condition() {
		return negated_condition;
	}
	
	/**
	 * Method for setting the negated condition
	 * @param negated_condition value of negated condition to be set
	 */
	public void setNegated_condition(String negated_condition) {
		this.negated_condition = negated_condition;
	}

	/**
	 * Method for checking if the object is a leaf or not
	 * @return
	 */
	public boolean isLeaf() {
		return isLeaf;
	}

	/**
	 * Method for setting the boolean variable indicated whether BulkCondition is a leaf or inner node
	 * @param isLeaf boolean value to be set
	 */
	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	/**
	 * Method for getting HashMap representing the eevar association 
	 * @return HashMap representing eevar association
	 */
	public HashMap<String, String> getEevar_association() {
		return eevar_association;
	}

	/**
	 * Method for setting the eevar associaton HashMap
	 * @param eevar_association HashMap to be set
	 */
	public void setEevar_association(HashMap<String, String> eevar_association) {
		this.eevar_association = eevar_association;
	}

	/**
	 * Method for getting HashMap representing the set table
	 * @return HashMap representing set table
	 */
	public HashMap<String, String> getSet_table() {
		return set_table;
	}

	/**
	 * Method for setting the set table
	 * @param set_table to be set
	 */
	public void setSet_table(HashMap<String, String> set_table) {
		this.set_table = set_table;
	}

	/**
	 * Method for getting ArrayList representing the list of false conditions
	 * @return ArrayList representing list of false conditions
	 */
	public ArrayList<String> getFalse_list() {
		return false_list;
	}

	/**
	 * Method for setting the list of false conditions
	 * @param false_list to be set
	 */
	public void setFalse_list(ArrayList<String> false_list) {
		this.false_list = false_list;
	}
	
	
	
	
	
	
	

}
