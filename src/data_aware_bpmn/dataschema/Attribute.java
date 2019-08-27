package data_aware_bpmn.dataschema;


import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;

public class Attribute {
	// list of attributes
	private String name;
	private Sort sort; 
	private DbColumn db_column;
	private Relation in_relation; 
	private String function_name = "";
	
	// Attribute constructor
	public Attribute (String name, Sort sort) {
		this.name = name;
		this.sort = sort;
		
	}
	
	
	// getter and setter
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Sort getSort() {
		return sort;
	}

	public void setSort(Sort sort) {
		this.sort = sort;
	}

	public Relation getIn_relation() {
		return in_relation;
	}

	public void setIn_relation(Relation in_relation) {
		this.in_relation = in_relation;
	}
	
	public DbColumn getDbColumn() {
		return this.db_column;
	}
	
	public void setDbColumn(DbColumn db_column) {
		this.db_column = db_column;
	}
	
	public String getFunction_name() {
		return function_name;
	}

	public void setFunction_name(String function_name) {
		this.function_name = function_name;
	}

	@Override
	public String toString() {
		String result = "";
		
		if (this.getFunction_name().equals("") || (this.getIn_relation() instanceof RepositoryRelation))
			result += this.name;
		else {
			result += "(" + this.function_name + " "+ ((CatalogRelation)this.getIn_relation()).getPrimary_key().getName() + ")";
		}
		
		return result;
	}
	
	public String toString(String eevar) {
		String result = "";
		
		if (this.getFunction_name().equals("") || (this.getIn_relation() instanceof RepositoryRelation))
			result += this.name;
		else {
			result += "(" + this.function_name + " "+ eevar + ")";
		}
		
		return result;
	}
}
