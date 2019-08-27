package data_aware_bpmn.dataschema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

import data_aware_bpmn.exception.EevarOverflowException;

public class ConjunctiveSelectQuery {
	
	private SelectQuery select_query;
	private HashMap<String, DbTable> tables;
	private String mcmt = "";
	private String negated_mcmt = "";
	private HashMap<String, String> ref_manager; // key is internal name, value is global eevar
	private boolean index_present = false;
	private Attribute [] selected_attributes;
	private ArrayList <Relation> from_relations;

	// constructor
	public ConjunctiveSelectQuery(Attribute...attributes) throws EevarOverflowException {
		this.selected_attributes = attributes;
		this.from_relations = new ArrayList<Relation>();
		this.tables = new HashMap<String, DbTable>();
		this.select_query = new SelectQuery();
		this.ref_manager = new HashMap<String, String>();
		
		for(Attribute att : attributes) {
			this.select_query.addColumns(att.getDbColumn());
			
			// check if the table of the column is already written
			if(!tables.containsKey(att.getDbColumn().getTable().getAlias())) {
				this.addFrom(att.getIn_relation());
				this.tables.put(att.getDbColumn().getTable().getAlias(), att.getDbColumn().getTable());
			}
			
			// add the method if a column in select clause is not primary key
			if (!att.getFunction_name().equals("") && (att.getIn_relation() instanceof CatalogRelation)) {
				this.addReferenceVariable(att);
				mcmt += "(= (" + att.getFunction_name() + " "
						+ this.ref_manager.get(((CatalogRelation) att.getIn_relation()).getPrimary_key().getName()) + ") " + this.ref_manager.get(att.getName())
						+ ") ";
				
			}
			
		}
	}
	
	// add condition method
	public void addBinaryCondition(boolean equal, Object first, Object second) {
		
		String first_mcmt = first + "";
		String second_mcmt = second + "";
			
		// control if one of the two objects is an attribute. In that case, extract the db column
		if (first instanceof Attribute) { 
			Attribute first_att = (Attribute) first;
			first = first_att.getDbColumn();
			
			// prova
			if(ref_manager.containsKey(first_att.getName()))
				first_mcmt = "" + ref_manager.get(first_att.getName());
			else 
				first_mcmt = "" + first_att.toString(ref_manager.get(((CatalogRelation)first_att.getIn_relation()).getPrimary_key().getName()));
			
		}
		if (second instanceof Attribute) {
			Attribute second_att = (Attribute) second;
			second = ((Attribute) second).getDbColumn();
			if(ref_manager.containsKey(second_att.getName()))
				second_mcmt = "" + ref_manager.get(second_att.getName());
			else 
				second_mcmt = "" + second_att.toString(ref_manager.get(((CatalogRelation)second_att.getIn_relation()).getPrimary_key().getName()));
		}
		
		String cond = "(= " + first_mcmt + " " + second_mcmt + ")";
		

		if(equal) {
			select_query.addCondition(BinaryCondition.equalTo(first, second));
			mcmt += cond + " ";
			negated_mcmt += "(not " + cond + ") ";
		}
		else {
			select_query.addCondition(BinaryCondition.notEqualTo(first, second));
			mcmt += "(not " + cond + ") ";
			negated_mcmt += cond + " ";
		}
	}
	
	
	// add relation in from clause
	public void addFrom(Relation r) throws EevarOverflowException {
		if (r instanceof CatalogRelation) {
			
			addFrom((CatalogRelation) r);
		}
		else {
			addFrom((RepositoryRelation) r);
		}

	}
	
	// add from catalog relation
	public void addFrom(CatalogRelation rel) throws EevarOverflowException {
		DbTable table = rel.getDbTable();

		if (!tables.containsKey(table.getAlias())) {
			this.tables.put(table.getAlias(), table);
			this.select_query.addFromTable(table);

			// prova
			this.addReferenceVariable(rel.getPrimary_key());
			
			mcmt += "(not (= " + this.ref_manager.get(rel.getPrimary_key().getName()) + " null)) ";
		}

	}
	
	// add from tables
	public void addFrom(RepositoryRelation rel) throws EevarOverflowException {
		DbTable table = rel.getDbTable();

		if (!tables.containsKey(table.getAlias())) {
			this.tables.put(table.getAlias(), table);
			this.select_query.addFromTable(table);
			
			for (int i = 0; i < rel.getAttributes().size(); i++) {
				this.addReferenceVariable(rel.getAttribute(i));
				
				mcmt += "(= " + rel.getName() + (i + 1) + "[x] " + this.ref_manager.get(rel.getAttribute(i).getName()) + ") ";
				this.setIndex_present(true);
			}
		}
	}
	
	// getQuery
	public String getQueryString() {
		return this.select_query.validate().toString();
	}

//	// getters and setters
	public SelectQuery getSelect_query() {
		return select_query;
	}

	public void setSelect_query(SelectQuery select_query) {
		this.select_query = select_query;
	}
	
	public HashMap<String, DbTable> getTables() {
		return tables;
	}

	public void setTables(HashMap<String, DbTable> tables) {
		this.tables = tables;
	}

	public void setMCTM(String mcmt) {
		this.mcmt = mcmt;
	}
	
	public String getMCMT() {
		return this.mcmt;
	}
	
	public boolean isIndex_present() {
		return index_present;
	}

	public void setIndex_present(boolean index_present) {
		this.index_present = index_present;
	}
	public HashMap<String, String> getRef_manager() {
		return ref_manager;
	}
	public void setRef_manager(HashMap<String, String> ref_manager) {
		this.ref_manager = ref_manager;
	}
	public String getNegated_mcmt() {
		return negated_mcmt;
	}
	public void setNegated_mcmt(String negated_mcmt) {
		this.negated_mcmt = negated_mcmt;
	}

	// method that performs eevar association
	public void addReferenceVariable(Attribute att) throws EevarOverflowException {
		// 1 ) check in the reference manager
		if (this.ref_manager.containsKey(att.getName()))
			return;
		
		// 2 ) check if in the global manager there are eevar with that sort
		Collection<String> eevar_available = EevarManager.getEevarWithSort(att.getSort());
		
		if(eevar_available.isEmpty()) {
			// add eevar to the global eevar manager
			String global_reference = EevarManager.addEevar(att.getSort());
			// add association locally
			this.ref_manager.put(att.getName(), global_reference);
		}
		// 3 process the array and look if one is free (it means it is not in the local map)
		else {
			for (String glb_name : eevar_available) {
				// case in which current one is not already used, I can use it
				if (!this.ref_manager.containsValue(glb_name)) {
					this.ref_manager.put(att.getName(), glb_name);
					return;
				}
			}
			// case in which all eevar already used
			String global_reference = EevarManager.addEevar(att.getSort());
			this.ref_manager.put(att.getName(), global_reference);

		}
		
	}
	
	

}
