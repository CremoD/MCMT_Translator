package DataSchema;

import java.util.Date;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.CustomSql;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.JdbcEscape;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.SetOperationQuery;
import com.healthmarketscience.sqlbuilder.UnionQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.*;

public class sql_Test {

	public static void main(String[] args) {
		////////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////			SET UP SCHEMA			////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// create default schema
	    DbSpec spec = new DbSpec();
	    DbSchema schema = spec.addDefaultSchema();
	 
	    // add table with basic customer info
	    DbTable customerTable = schema.addTable("customer");
	    DbColumn custIdCol = customerTable.addColumn("cust_id", "sort1", null);
	    DbColumn custNameCol = customerTable.addColumn("name", "varchar", 255);
	    
	    DbTable customer2 = schema.addTable("customer");
	    customer2.addColumn(custIdCol.getName(), custIdCol.getTypeNameSQL(), custIdCol.getTypeLength());
	    customer2.addColumn(custNameCol.getName(), custNameCol.getTypeNameSQL(), custNameCol.getTypeLength());

	    System.out.println(custNameCol.getName());
	    System.out.println(custNameCol.getTypeLength());
	    System.out.println(custNameCol.getTypeNameSQL());

	    //customer2.addColumn(custIdCol);
	    //customer2.addColumn(custNameCol);
	    // add order table with basic order info
	    DbTable orderTable = schema.addTable("order");
	    DbColumn orderIdCol = orderTable.addColumn("order_id", "number", null);
	    DbColumn orderCustIdCol = orderTable.addColumn("cust_id", "number", null);
	    DbColumn orderTotalCol = orderTable.addColumn("total", "number", null);
	    DbColumn orderDateCol = orderTable.addColumn("order_date", "timestamp", null);
	    
	    
	 
	    // add a join from the customer table to the order table (on cust_id)
		DbJoin custOrderJoin = spec.addJoin(null, "customer", null, "order", "cust_id");
		DbJoin myJoin = spec.addJoin(null, "customer", null, "customer", "cust_id");

		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////
	    //////////////////////////////////				CREATE SCHEMAS			////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		String createCustomerTable = new CreateTableQuery(customerTable, true).validate().toString();
		System.out.println(createCustomerTable);

		// => CREATE TABLE customer (cust_id number,name varchar(255))

		String createOrderTable = new CreateTableQuery(orderTable, true).validate().toString();
		System.out.println(createOrderTable);

		// => CREATE TABLE order (order_id number,cust_id number,total number,order_date timestamp)
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////				POPULATE SCHEMA			////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		String insertCustomerQuery = new InsertQuery(customerTable).addColumn(custIdCol, 1)
				.addColumn(custNameCol, "bob").validate().toString();
		System.out.println(insertCustomerQuery);

		// => INSERT INTO customer (cust_id,name)
		// VALUES (1,'bob')

		String preparedInsertCustomerQuery = new InsertQuery(customerTable).addPreparedColumns(custIdCol, custNameCol)
				.validate().toString();
		System.out.println(preparedInsertCustomerQuery);

		// => INSERT INTO customer (cust_id,name)
		// VALUES (?,?)

		String insertOrderQuery = new InsertQuery(orderTable).addColumn(orderIdCol, 37).addColumn(orderCustIdCol, 1)
				.addColumn(orderTotalCol, 37.56).addColumn(orderDateCol, JdbcEscape.timestamp(new Date())).validate()
				.toString();
		System.out.println(insertOrderQuery);

		// => INSERT INTO order (order_id,cust_id,total,order_date)
		// VALUES (37,1,37.56,{ts '2008-04-01 14:39:00.914'})
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////				RUN QUERIES				////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// find a customer name by id
		SelectQuery query1 = new SelectQuery().addColumns(custNameCol).addColumns(customer2.getColumns().get(0)).addCondition(BinaryCondition.equalTo(custIdCol, 1))
				;
		System.out.println(query1.toString());
		SelectQuery query3 = new SelectQuery().addColumns(custIdCol).addColumns(customer2.getColumns().get(0)).addCondition(BinaryCondition.equalTo(custIdCol, 1))
				;
		System.out.println(query3.toString());
		
		UnionQuery u = new UnionQuery(SetOperationQuery.Type.UNION, query1, query3);
		System.out.println(u.validate().toString());

		// => SELECT t0.name FROM customer t0
		// WHERE (t0.cust_id = 1)

		////
		// find all the orders for a customer, given name, order by date
		SelectQuery query2 = new SelectQuery().addAllTableColumns(orderTable)
				.addJoins(SelectQuery.JoinType.INNER, myJoin)
				.addCondition(BinaryCondition.equalTo(custNameCol, "bob")).addCondition(BinaryCondition.notEqualTo(custNameCol, "jane")).addOrderings(orderDateCol).validate()
				;
		System.out.println(query2);

		// => SELECT t1.*
		// FROM customer t0 INNER JOIN order t1 ON (t0.cust_id = t1.cust_id)
		// WHERE (t0.name = 'bob')
		// ORDER BY t1.order_date
		
		

	}
	
	

}
