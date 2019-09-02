package data_aware_bpmn.dataschema;

import java.util.HashMap;

import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;

public class RelationFactory {
	// create an object of Relation Factory (singleton pattern)
	private static RelationFactory relation_factory = new RelationFactory ();
	public static HashMap<String, CatalogRelation> catalog = new HashMap<String, CatalogRelation>();
	public static HashMap<String, RepositoryRelation> repository = new HashMap<String, RepositoryRelation>();
	private static DbSpec spec;
	public static DbSchema schema;
	
	// make the constructor private so that this class cannot be instantiated
	private RelationFactory() {
		spec = new DbSpec();
		schema = spec.addDefaultSchema();
	}
	
	// get the unique object
	public static RelationFactory getInstance() {
		return relation_factory;
	}
	
	// method for creating a Catalog relation if it doesn't exist, or return it if it exists
	public CatalogRelation getCatalogRelation(String relation_name) {
		if (catalog.containsKey(relation_name))
			return catalog.get(relation_name);
		else {
			CatalogRelation cat_relation = new CatalogRelation(relation_name);
			cat_relation.setDbTable(schema.addTable(relation_name));
			catalog.put(relation_name, cat_relation);
			return cat_relation;
		}
			
	}
	
	// method for creating a Repository relation if it doesn't exist, or return it if it exists
	public RepositoryRelation getRepositoryRelation(String relation_name) {
		if (repository.containsKey(relation_name))
			return repository.get(relation_name);
		else {
			RepositoryRelation rep_relation = new RepositoryRelation(relation_name);
			rep_relation.setDbTable(schema.addTable(relation_name));
			repository.put(relation_name, rep_relation);
			return rep_relation;
		}	
	}

	// method for getting the Strings representing the catalog
	public String printCatalog() {
		String result = "";
		for (CatalogRelation value : catalog.values()) {
		    result += value; 
		}
		return result;
	}
	
	// method for getting the Strings representing the repository
	public String printRepository() {
		String result = "";
		for (RepositoryRelation value : repository.values()) {
		    result += value; 
		}
		return result;
	}
	
	// method for printing the list of catalog function representing the attributes of catalog relations
	public String printFunctions () {
		String result = ":db_functions ";
		for (CatalogRelation value : catalog.values()) {
		    result += value.getFunctionNames();
		}
		return result;
	}
	
	public String initialize() {
		String result = "";
		for (RepositoryRelation value : repository.values()) {
		    result += value.initialize(); 
		}
		return result;
	}
	
	
}
