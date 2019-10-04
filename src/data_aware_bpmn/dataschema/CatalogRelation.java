package data_aware_bpmn.dataschema;

import java.util.ArrayList;

import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

/**
 * Class responsible for representing the Catalog relation component of data schema of a DAB
 * @author DavideCremonini
 *
 */
public class CatalogRelation implements Relation{
	
	//list of attributes
	private String name;
	private Attribute primary_key;
	private DbTable relationTable;
	private ArrayList<Attribute> list_attributes;
	private int function_number = 1;
	
	/**
	 * Constructor of the class. To initialize a catalog relation, it is necesssary to indicate the name of the relation
	 * @param name of the relation
	 */
	public CatalogRelation (String name) {
		this.name = name;
		this.list_attributes = new ArrayList<Attribute>();
	}
	
	/**
	 * Method for getting the name of the relation
	 * @return name of the relation
	 */
	public String getName() {
		return name;
	}

	/**
	 * Method for setting the name of the relation
	 * @param name of the relation to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Method for getting the attribute representing the primary key of the relation
	 * @return attribute representing the primary key of the relation
	 */
	public Attribute getPrimary_key() {
		return primary_key;
	}

	/**
	 * method for setting the attribute representing the primary key of the relation
	 * @param primary_key attribute representing the primary key of the relation to be set
	 */
	public void setPrimary_key(Attribute primary_key) {
		this.primary_key = primary_key;
	}

	/**
	 * Method for getting the list of attributes of the relation
	 * @return list of attributes of the relation
	 */
	public ArrayList<Attribute> getAttributes() {
		return list_attributes;
	}

	/**
	 * Method for setting the list of attributes of the relation
	 * @param attributes list of attributes of the relation to be set
	 */
	public void setAttributes(ArrayList<Attribute> attributes) {
		this.list_attributes = attributes;
	}
	
	/**
	 * Method for getting the DBTable of the relation
	 * @return DBTable of the relation
	 */
	public DbTable getDbTable() {
		return this.relationTable;
	}
	
	/**
	 *  Method for setting the DBTable of the relation
	 * @param relationTable DBTable of the relation to be set
	 */
	public void setDbTable(DbTable relationTable) {
		this.relationTable = relationTable;
	}
	
	/**
	 * Method for printing the MCMT declaration of the function indicating the attributes other than primary key
	 * @return MCMT declaration of the function indicating the attributes other than primary key
	 */
	public String getFunctionNames() {
		String result = "";
		for (Attribute attr : list_attributes) {
			if (!attr.getFunction_name().equals(""))
				result += attr.getFunction_name() + " ";
		}
		return result;
	}
	
	/**
	 * Method that get a function name given a number
	 * @param num to build the function name
	 * @param applied String to apply the function name
	 * @return function name
	 */
	public String getFunctionName(int num, String applied) {		
		return this.name + "_f" + num + " " + applied;
	}
	
	/**
	 * Method that returns the arity of the relation
	 * @return arity of the relation
	 */
	public int arity() {
		return this.list_attributes.size();
	}
	
	/**
	 * Method for adding an attribute
	 * @param name of the attribute
	 * @param sort of the attribute
	 * @return attribute added
	 */
	public Attribute addAttribute(String name, Sort sort) {
		Attribute attribute = new Attribute(name, sort);
		attribute.setIn_relation(this);
		attribute.setDbColumn(this.relationTable.addColumn(name, sort.getName(), null));
		if (this.list_attributes.size() == 0)
			this.primary_key = attribute;
		else {
			attribute.setFunction_name(this.name + "_f" + function_number);
			function_number++;
		}
		this.list_attributes.add(attribute);
		return attribute;
	}

	/**
	 * Method for returning a particular column of an attribute given the index
	 * @param index of the attribute
	 * @return particular column of an attribute given the index
	 */
	public DbColumn getAttributeColumn(int index) {
		return list_attributes.get(index).getDbColumn();
	}
	
	/**
	 * Method for returning a particular attribute given the index
	 * @param index of the attribute
	 * @return attribute
	 */
	public Attribute getAttribute(int index) {
		return list_attributes.get(index);
	}
	
	/**
	 * Method for getting an alias of the current catalog relation
	 * @return catalog relation being the alias of the current one
	 */
	public CatalogRelation getAlias() {
		DbTable alias = RelationFactory.schema.addTable(this.name);
		// copy the columns
		for (DbColumn col : this.relationTable.getColumns()) {
			alias.addColumn(col.getName(), col.getTypeNameSQL(), col.getTypeLength());
		}
		CatalogRelation cat_relation = new CatalogRelation(this.name);
		cat_relation.setDbTable(alias);
		for (Attribute att : this.list_attributes) {
			cat_relation.addAttribute(alias.getAlias() + "_" + att.getName(), att.getSort());
		}
		return cat_relation;
	}
	

	/**
	 * toString method in order to print the corresponding MCMT declaration
	 * @return MCMT declaration of the current catalog relation
	 */
	@Override
	public String toString() {
		String result = "";
		int function_number = 0;
		for (Attribute attr : list_attributes) {
			if (function_number != 0)
				result += ":smt(define "+ this.name +"_f" + function_number + "::(->" + this.primary_key.getSort().getName() + " " + attr.getSort().getName() + "))\n";
			function_number++;
		}
		return result;
	}

}
