package DataSchema;

import java.util.HashMap;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

public class ConjunctiveSelectQuery {
	
	private SelectQuery select_query;
	private HashMap<String, DbTable> tables;
	private String mcmt = "";
	private HashMap<String, Sort> eevar_list;
	private boolean index_present = false;

	// constructor
	public ConjunctiveSelectQuery(Attribute...attributes) {
		this.tables = new HashMap<String, DbTable>();
		this.select_query = new SelectQuery();
		this.eevar_list = new HashMap<String, Sort>();
		
		for(Attribute att : attributes) {
			this.select_query.addColumns(att.getDbColumn());
			
			// check if the table of the column is already written
			if(!tables.containsKey(att.getDbColumn().getTable().getAlias())) {
				this.addFrom(att.getIn_relation());
				this.tables.put(att.getDbColumn().getTable().getAlias(), att.getDbColumn().getTable());
			}
			
			// add the method if a column in select clause is not primary key: can be useful
			if (!att.getFunction_name().equals("") && (att.getIn_relation() instanceof CatalogRelation)) {
				mcmt += "(= (" + att.getFunction_name() + " "
						+ ((CatalogRelation) att.getIn_relation()).getPrimary_key().getName() + ") " + att.getName()
						+ ") ";
				eevar_list.put(att.getName(), att.getSort());
				
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
			if (eevar_list.containsKey(first_att.getName())) 
				first_mcmt = "" + first_att.toString(true);
			
		}
		if (second instanceof Attribute) {
			Attribute second_att = (Attribute) second;
			second = ((Attribute) second).getDbColumn();
			if (eevar_list.containsKey(second_att.getName())) 
				second_mcmt = "" + second_att.toString(true);
		}
		
		String cond = "(= " + first_mcmt + " " + second_mcmt + ")";
		

		if(equal) {
			select_query.addCondition(BinaryCondition.equalTo(first, second));
			mcmt += cond + " ";
		}
		else {
			select_query.addCondition(BinaryCondition.notEqualTo(first, second));
			mcmt += "(not " + cond + ") ";
		}
	}
	
	
	// add relation in from clause
	public void addFrom(Relation r) {
		if (r instanceof CatalogRelation)
			addFrom((CatalogRelation) r);
		else
			addFrom((RepositoryRelation) r);

	}
	
	// add from catalog relation
	public void addFrom(CatalogRelation rel) {
		DbTable table = rel.getDbTable();

		if (!tables.containsKey(table.getAlias())) {
			this.tables.put(table.getAlias(), table);
			this.select_query.addFromTable(table);

			mcmt += "(not (= " + rel.getPrimary_key().getName() + " null)) ";
			if(!eevar_list.containsKey(rel.getPrimary_key().getName())) {
				eevar_list.put(rel.getPrimary_key().getName(), rel.getPrimary_key().getSort());
			}
		}

	}
	
	// add from tables
	public void addFrom(RepositoryRelation rel) {
		DbTable table = rel.getDbTable();

		if (!tables.containsKey(table.getAlias())) {
			this.tables.put(table.getAlias(), table);
			this.select_query.addFromTable(table);
			
			for (int i = 0; i < rel.getAttributes().size(); i++) {
				mcmt += "(= " + rel.getName() + (i + 1) + "[x] " + rel.getAttribute(i).getName() + ") ";
				this.setIndex_present(true);
				if(!eevar_list.containsKey(rel.getAttribute(i).getName())) {
					eevar_list.put(rel.getAttribute(i).getName(), rel.getAttribute(i).getSort());
				}
			}
		}
	}
	
	// getQuery
	public String getQueryString() {
		return this.select_query.validate().toString();
	}

	// getters and setters
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
	
	public HashMap<String, Sort> getEevar_List() {
		return eevar_list;
	}

	public void setEevar_List(HashMap<String, Sort> eevar) {
		this.eevar_list = eevar;
	}
	
	public boolean isIndex_present() {
		return index_present;
	}

	public void setIndex_present(boolean index_present) {
		this.index_present = index_present;
	}

}
