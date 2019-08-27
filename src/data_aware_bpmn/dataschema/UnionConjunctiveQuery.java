package data_aware_bpmn.dataschema;

import com.healthmarketscience.sqlbuilder.SetOperationQuery;
import com.healthmarketscience.sqlbuilder.UnionQuery;

public class UnionConjunctiveQuery {
	
	// list of attributes
	private UnionQuery union_query;
	private ConjunctiveSelectQuery [] queries; 
	
	// constructor
	public UnionConjunctiveQuery(ConjunctiveSelectQuery...conjunctiveSelectQueries) {
		union_query = new UnionQuery(SetOperationQuery.Type.UNION);
		for (ConjunctiveSelectQuery q : conjunctiveSelectQueries) 
			union_query.addQueries(q.getSelect_query());
		union_query.validate();
		this.queries = conjunctiveSelectQueries;
	}
	

	// method for adding other queries
	public void addQueries(ConjunctiveSelectQuery...conjunctiveSelectQueries) {
		for (ConjunctiveSelectQuery q : conjunctiveSelectQueries) 
			union_query.addQueries(q.getSelect_query());
		union_query.validate();
	}
	
	// method for printing the query
	public String getQueryString() {
		return union_query.toString();
	}

	// getters and setters
	public UnionQuery getUnion_query() {
		return union_query;
	}

	public void setUnion_query(UnionQuery union_query) {
		this.union_query = union_query;
	}
	

	public ConjunctiveSelectQuery[] getQueries() {
		return queries;
	}

	public void setQueries(ConjunctiveSelectQuery[] queries) {
		this.queries = queries;
	}

}
