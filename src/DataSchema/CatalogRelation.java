package DataSchema;

import java.util.ArrayList;

import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

public class CatalogRelation implements Relation{
	
	//list of attributes
	private String name;
	private Attribute primary_key;
	private DbTable relationTable;
	private ArrayList<Attribute> list_attributes;
	private int function_number = 1;
	
	// constructor
	public CatalogRelation (String name) {
		this.name = name;
		this.list_attributes = new ArrayList<Attribute>();
	}
	
	// getters and setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Attribute getPrimary_key() {
		return primary_key;
	}

	public void setPrimary_key(Attribute primary_key) {
		this.primary_key = primary_key;
	}

	public ArrayList<Attribute> getAttributes() {
		return list_attributes;
	}

	public void setAttributes(ArrayList<Attribute> attributes) {
		this.list_attributes = attributes;
	}
	
	public DbTable getDbTable() {
		return this.relationTable;
	}
	
	public void setDbTable(DbTable relationTable) {
		this.relationTable = relationTable;
	}
	
	// print list of functions
	public String getFunctionNames() {
		String result = "";
		for (Attribute attr : list_attributes) {
			if (!attr.getFunction_name().equals(""))
				result += attr.getFunction_name() + " ";
		}
		return result;
	}
	
	// method that gets name of function given number
	public String getFunctionName(int num, String applied) {		
		return this.name + "_f" + num + " " + applied;
	}
	
	// method that returns the arity of the relation
	public int arity() {
		return this.list_attributes.size();
	}
	
	// add attributes
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

	// method for returning a particular column of an attribute given the index
	public DbColumn getAttributeColumn(int index) {
		return list_attributes.get(index).getDbColumn();
	}
	
	// method for returning a particular attribute given the index
	public Attribute getAttribute(int index) {
		return list_attributes.get(index);
	}
	
	// method for getting an alias of the current catalog relation
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
	

	// toString method in order to print the corresponding MCMT declaration
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
