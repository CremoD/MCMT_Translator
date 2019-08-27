package data_aware_bpmn.dataschema;



import java.util.ArrayList;
import java.util.HashMap;

import data_aware_bpmn.exception.InvalidInputException;
import data_aware_bpmn.exception.UnmatchingSortException;

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

	
	public BulkCondition (RepositoryRelation relationToUpdate) throws InvalidInputException {
		this.relationToUpdate = relationToUpdate;
		this.set_table = new HashMap<String, String>();
		this.false_list = new ArrayList<String>();

	}
	
	// check attribute is of the relationToUpdate relation and return corresponding name
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
	
	// methods for building the condition. 4 types, greater, smaller, equal, in catalog relation
	public void addGreaterCondition (Attribute att, Object second) throws InvalidInputException {
		condition += " (> " + check_return_Attribute(att) + " " + second + ")";
		this.false_list.add(" (not (> " + check_return_Attribute(att) + " " + second + "))");
	}
	
	public void addSmallerCondition (Attribute att, Object second) throws InvalidInputException {
		condition += " (< " + check_return_Attribute(att) + " " + second + ")";
		this.false_list.add(" (not (< " + check_return_Attribute(att) + " " + second + "))");
	}
	
	public void addEqualCondition (Attribute att, Object second) throws InvalidInputException {
		condition += " (= " + check_return_Attribute(att) + " " + second + ")";
		this.false_list.add(" (not (= " + check_return_Attribute(att) + " " + second + "))");
	}
	
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
	
	
	public BulkCondition addTrueChild () throws InvalidInputException {
		BulkCondition new_condition = new BulkCondition(relationToUpdate);
		new_condition.setEevar_association(eevar_association);
		this.true_node = new_condition;
		return new_condition;
	}
	
	public BulkCondition addFalseChild () throws InvalidInputException {
		BulkCondition new_condition = new BulkCondition(relationToUpdate);
		new_condition.setEevar_association(eevar_association);
		this.false_node = new_condition;
		return new_condition;
	}
	
	public void set(Attribute att, String new_value) throws InvalidInputException, UnmatchingSortException {
		// control att is of the updated repository relation
		if (((RepositoryRelation)att.getIn_relation()) != relationToUpdate) {
			throw new InvalidInputException("Attribute to set is not an attributed of the considered relation " + relationToUpdate.getName());
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
	
	// method for getting the correct mcmt declaration of local update
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
	
	
	
	// prova, check for matching method
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

	public boolean isEevar(String value) throws InvalidInputException {
		if (!eevar_association.containsKey(value)) {
			if (!ConstantFactory.constant_list.containsKey(value)) {
				throw new InvalidInputException(value + " is not an answer of the precondition or a constant");
			}
			return false;
		}
		return true;
	}
	
	
	// getters and setters
	public String getCondition () {
		return this.condition;
	}

	public RepositoryRelation getRelationToUpdate() {
		return relationToUpdate;
	}

	public void setRelationToUpdate(RepositoryRelation relationToUpdate) {
		this.relationToUpdate = relationToUpdate;
	}

	public String getNegated_condition() {
		return negated_condition;
	}

	public void setNegated_condition(String negated_condition) {
		this.negated_condition = negated_condition;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public HashMap<String, String> getEevar_association() {
		return eevar_association;
	}

	public void setEevar_association(HashMap<String, String> eevar_association) {
		this.eevar_association = eevar_association;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public HashMap<String, String> getSet_table() {
		return set_table;
	}

	public void setSet_table(HashMap<String, String> set_table) {
		this.set_table = set_table;
	}

	public ArrayList<String> getFalse_list() {
		return false_list;
	}

	public void setFalse_list(ArrayList<String> false_list) {
		this.false_list = false_list;
	}
	
	
	
	
	
	
	

}
