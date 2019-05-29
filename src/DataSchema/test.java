package DataSchema;



public class test {

	public static void main(String[] args) {
		
		// SCENARIO. Two catalog relations: R1 (id_r1, a1_r1, a2_r1, a3_r1) where id_r1 is PK, a1_r1 FK and rest strings 
		//									R2 (id_r2, a1_r2) where id_r2 is PK and rest strings
		// 			 One repos relation:	S (a1_s, a2_s) 
		RelationFactory relation_factory = RelationFactory.getInstance();
		SortFactory sort_factory = SortFactory.getInstance();
		CaseVariableFactory cv_factory = CaseVariableFactory.getInstance();
		
		
		
		Sort r1_sort = sort_factory.getSort("R1_sort");
		Sort r2_sort = sort_factory.getSort("R2_sort");
		Sort string_sort = sort_factory.getSort("String_sort");
		
		CaseVariable c = cv_factory.getCaseVariable("winner", r2_sort, true);

		
		
		CatalogRelation r1 = relation_factory.getCatalogRelation("R1");
		r1.addAttribute("id_r1", r1_sort);
		r1.addAttribute("a1_r1", r2_sort);
		r1.addAttribute("a2_r1", string_sort);
		r1.addAttribute("a3_r1", string_sort);
		
		CatalogRelation r2 = relation_factory.getCatalogRelation("R2");
		r2.addAttribute("id_r2", r2_sort);
		r2.addAttribute("a1_r2", string_sort);
		
		RepositoryRelation s = relation_factory.getRepositoryRelation("S");
		s.addAttribute("a1_s", r1_sort);
		s.addAttribute("a2_s", string_sort);

		printDeclaration(relation_factory, sort_factory, cv_factory);


		
		/*// try queries on tables
		// creation
		String createR1Table = new CreateTableQuery(r1.getDbTable(), true).validate().toString();
		System.out.println(createR1Table);
		// insertion
		String insertR1Query = new InsertQuery(r1.getDbTable()).addColumn(r1.getAttributes().get(0).getDbColumn(), 1)
				.addColumn(r1.getAttributes().get(1).getDbColumn(), "bob").validate().toString();
		System.out.println(insertR1Query);
		// selection
		String query1 = new SelectQuery().addColumns(r1.getAttributes().get(0).getDbColumn()).addCondition(BinaryCondition.equalTo(r1.getAttributes().get(0).getDbColumn(), 1))
				.validate().toString();
		System.out.println(query1.toString());*/
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//											Conjunctive Query												//
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// create alias for join between same table r1. Ask for confirmation
		//CatalogRelation aliasr1 = r1.getAlias();

		// create conjunctive query with list of columns of the select part
		ConjunctiveSelectQuery prova = new ConjunctiveSelectQuery(r1.getAttribute(0), r2.getAttribute(1), s.getAttribute(0));
		// add FROM part
		prova.addFrom(r1);
		// add WHERE part
		prova.addBinaryCondition(true, r1.getAttribute(1), c);
		prova.addBinaryCondition(true, s.getAttribute(0), r2.getAttribute(1));

		


		System.out.println(prova.printEevar());

		System.out.println(prova.getQueryString());
		System.out.println();
		System.out.println(prova.getMCMT());
		
//		////////////////////////////////////////////////////////////////////////////////////////////////////////////
//		//											Union Conjunctive Query											//
//		////////////////////////////////////////////////////////////////////////////////////////////////////////////
//		 //create conjunctive query with list of columns of the select part
//		ConjunctiveSelectQuery prova2 = new ConjunctiveSelectQuery(r1.getAttributeColumn(0), r2.getAttributeColumn(1));
//		// add WHERE part
//		prova2.addBinaryCondition(false, 1, r1.getAttributeColumn(1));
//		prova2.addBinaryCondition(true, 3, r1.getAttributeColumn(1));
//		
//		UnionConjunctiveQuery u = new UnionConjunctiveQuery(prova, prova2);
//		
//		System.out.println();
//		System.out.println(u.getQueryString());
	}
	
	
	public static void printDeclaration(RelationFactory rf, SortFactory sf, CaseVariableFactory cvf) {
		System.out.println(sf.printSort());
		System.out.println(rf.printCatalog());
		System.out.println(rf.printRepository());
		System.out.println(cvf.printCaseVariables());
		System.out.println(sf.printSortList());
		System.out.println(rf.printFunctions());
	}
	

}
