package DataSchema;


import java.util.jar.Attributes;

import Exception.InvalidInputException;

public class BulkCondition {
	
	private RepositoryRelation relationToUpdate;
	private String condition = "";
	private String negated_condition = "";
	
	public BulkCondition (RepositoryRelation relationToUpdate, Attribute...attributes) throws InvalidInputException {
		this.relationToUpdate = relationToUpdate;
	}
	
	// check attribute is of the relationToUpdate relation and return corresponding name
	public String check_return_Attribute (Attribute att) throws InvalidInputException {
		if (!(att.getIn_relation() instanceof RepositoryRelation) || !((RepositoryRelation)att.getIn_relation() == this.relationToUpdate))
			throw new InvalidInputException ("Attributes should refer to correct Repository relation: " + relationToUpdate.getName());
		
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
		condition += " ( " + check_return_Attribute(att) + " > " + second + " )";
		negated_condition += " (not " + check_return_Attribute(att) + " > " + second + " )";
	}
	
	public void addSmallerCondition (Attribute att, Object second) throws InvalidInputException {
		condition += " ( " + check_return_Attribute(att) + " < " + second + " )";
		negated_condition += " (not " + check_return_Attribute(att) + " < " + second + " )";
	}
	
	public void addEqualCondition (Attribute att, Object second) throws InvalidInputException {
		condition += " ( " + check_return_Attribute(att) + " = " + second + " )";
		negated_condition += " (not " + check_return_Attribute(att) + " = " + second + " )";
	}
	
	public void addInRelationCondition (CatalogRelation cat, Attribute...attributes) throws InvalidInputException {
		// to dooooo
	}
	

}
