package data_aware_bpmn.dataschema;


import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;

/**
 * Class responsible for creation of typed attributes, characterized by particular name and sort
 * @author DavideCremonini
 * 
 */
public class Attribute {
	// list of attributes
	private String name;
	private Sort sort; 
	private DbColumn db_column;
	private Relation in_relation; 
	private String function_name = "";
	
	
	/**
	 * Constructor of an attribute instance 
	 * @param name specific name of the attribute
	 * @param sort type of the attribute
	 */
	public Attribute (String name, Sort sort) {
		this.name = name;
		this.sort = sort;
		
	}
	
	
	/**
	 * Get method for returning the name
	 * @return name of the attribute
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set method for setting the name
	 * @param name value of the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get method for returning the sort
	 * @return sort of the attribute
	 */
	public Sort getSort() {
		return sort;
	}

	/**
	 * Set method for setting the sort
	 * @param sort value of the sort to set
	 */
	public void setSort(Sort sort) {
		this.sort = sort;
	}

	/**
	 * Get method for returning the relation of the attribute
	 * @return relation of the attribute
	 */
	public Relation getIn_relation() {
		return in_relation;
	}

	/**
	 * Set method for setting the relation
	 * @param in_relation value of the relation to set
	 */
	public void setIn_relation(Relation in_relation) {
		this.in_relation = in_relation;
	}
	
	/**
	 * Get method for returning the DB column represented by the attribute
	 * @return column of the attribute
	 */
	public DbColumn getDbColumn() {
		return this.db_column;
	}
	
	/**
	 * Set method for setting the DB column
	 * @param db_column value of the DB column to set
	 */
	public void setDbColumn(DbColumn db_column) {
		this.db_column = db_column;
	}
	
	/**
	 * Get method for returning the name of the MCMT function 
	 * @return name of MCMT function of the attribute
	 */
	public String getFunction_name() {
		return function_name;
	}

	/**
	 * Set method for setting the function name
	 * @param function_name value of the function name to set
	 */
	public void setFunction_name(String function_name) {
		this.function_name = function_name;
	}

	
	@Override
	/**
	 * Method for the String representation of an attribute, with respect to MCMT translation
	 * @return String representing the attribute in MCMT
	 */
	public String toString() {
		String result = "";
		
		if (this.getFunction_name().equals("") || (this.getIn_relation() instanceof RepositoryRelation))
			result += this.name;
		else {
			result += "(" + this.function_name + " "+ ((CatalogRelation)this.getIn_relation()).getPrimary_key().getName() + ")";
		}
		
		return result;
	}
	
	/**
	 * Method for the String representation of an attribute, with respect to MCMT translation, in case the name is affected by eevar
	 * @param eevar that indicates the name of the eevar value of the attribute (used in transitions)
	 * @return String representing the attribute in MCMT
	 */
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
