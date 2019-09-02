package data_aware_bpmn.dataschema;

import java.util.ArrayList;

import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

public class RepositoryRelation implements Relation{
	
	// list of attributes
	private String name;
	private DbTable relationTable;
	private ArrayList<Attribute> attributes;
	
	// constructor
	public RepositoryRelation(String name) {
		this.name = name;
		this.attributes = new ArrayList<Attribute>();
	}
	
	// add attributes
	public Attribute addAttribute(String name, Sort sort) {
		Attribute attribute = new Attribute(name, sort);
		attribute.setIn_relation(this);
		attribute.setDbColumn(this.relationTable.addColumn(name, sort.getName(), null));
		this.attributes.add(attribute);
		return attribute;
	}
	
	// method for returning a particular column of an attribute given the index
	public DbColumn getAttributeColumn(int index) {
		return attributes.get(index).getDbColumn();
	}
	
	// method for returning a particular attribute given the index
	public Attribute getAttribute(int index) {
		return attributes.get(index);
	}

	
	// getters and setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(ArrayList<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	public DbTable getDbTable() {
		return this.relationTable;
	}
	
	public void setDbTable(DbTable relationTable) {
		this.relationTable = relationTable;
	}
	
	// method that returns the arity of the relation
	public int arity() {
		return this.attributes.size();
	}
	
	// toString method in order to print the corresponding MCMT declaration
	@Override
	public String toString() {
		String result = "";
		int attr_num = 1;
		
		for (Attribute attr : attributes) {
			result += ":local " + this.name + attr_num + " " + attr.getSort().getName() + "\n";
			attr_num++;
		}
		
		return result;
	}
	
	public String initialize() {
		String result = "";
		int attr_num = 1;
		
		for (Attribute attr : attributes) {
			result += "(= " + this.name + attr_num + "[x] NULL_" + attr.getSort().getName() + ") ";
			attr_num++;
		}
		
		return result;
	}

}
