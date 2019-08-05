package DataSchema;

import ControlFlow.*;
import Exception.EevarOverflowException;
import Exception.InvalidInputException;
import Exception.UnmatchingSortException;

public class test {

	public static void main(String[] args) throws UnmatchingSortException, InvalidInputException, EevarOverflowException {
		
		// SCENARIO. Two catalog relations: R1 (id_r1, a1_r1, a2_r1, a3_r1) where id_r1 is PK, a1_r1 FK and rest strings 
		//									R2 (id_r2, a1_r2) where id_r2 is PK and rest strings
		// 			 Two repos relation:	S (a1_s, a2_s) 
		//									Z (a1_z, a2_z)
		RelationFactory relation_factory = RelationFactory.getInstance();
		SortFactory sort_factory = SortFactory.getInstance();
		CaseVariableFactory cv_factory = CaseVariableFactory.getInstance();
		ConstantFactory constant_factory = ConstantFactory.getInstance();

		
		
		
		Sort r1_sort = sort_factory.getSort("R1_sort");
		Sort r2_sort = sort_factory.getSort("R2_sort");
		Sort string_sort = sort_factory.getSort("String_sort");
		
		CaseVariable c = cv_factory.getCaseVariable("winner", r2_sort, true);
		CaseVariable c2 = cv_factory.getCaseVariable("looser", string_sort, true);

		Constant refused = constant_factory.getConstant("Refused", string_sort);
		Constant modified = constant_factory.getConstant("Modified", string_sort);
		Constant accepted = constant_factory.getConstant("Accepted", string_sort);
		Constant idle = constant_factory.getConstant("Idle", string_sort);
		Constant enabled = constant_factory.getConstant("Enabled", string_sort);
		Constant active = constant_factory.getConstant("Active", string_sort);
		Constant completed = constant_factory.getConstant("Completed", string_sort);
		Constant active1 = constant_factory.getConstant("Active1", string_sort);
		Constant active2 = constant_factory.getConstant("Active2", string_sort);

		
		
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
		
		RepositoryRelation z = relation_factory.getRepositoryRelation("Z");
		z.addAttribute("a1_z", r1_sort);
		z.addAttribute("a2_z", string_sort);

		printDeclaration(relation_factory, sort_factory, cv_factory, constant_factory);
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//											Conjunctive Query												//
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// create alias for join between same table r1. Ask for confirmation
		//CatalogRelation aliasr1 = r1.getAlias();

		// create conjunctive query with list of columns of the select part
		ConjunctiveSelectQuery prova = new ConjunctiveSelectQuery(r1.getAttribute(0), r2.getAttribute(1));
		// add FROM part
		prova.addFrom(r1);
		// add WHERE part
		prova.addBinaryCondition(true, r1.getAttribute(0), modified);
		prova.addBinaryCondition(true, r2.getAttribute(1), r1.getAttribute(3));
		



		System.out.println(prova.getQueryString());
		System.out.println();
		System.out.println(prova.getMCMT());
		
		System.out.println();
		System.out.println(EevarManager.printEevar());
		
		
//		// create conjunctive query with list of columns of the select part
//		ConjunctiveSelectQuery prova2 = new ConjunctiveSelectQuery(r2.getAttribute(1));
//		// add FROM part
//		prova2.addFrom(s);
//		// add WHERE part
//		prova2.addBinaryCondition(true, s.getAttribute(0), c);
//		prova2.addBinaryCondition(true, r2.getAttribute(1), c2);
//		
//		System.out.println();
//		System.out.println(prova2.getQueryString());
//		System.out.println();
//		System.out.println(prova2.getMCMT());
		
		
		
		
		InsertTransition it = new InsertTransition("InsertTrans1", prova);
		it.insert(s, "id_r1", "a1_r2");
		it.set(c, "id_r2");
		

		System.out.println();
		System.out.println(it.generateMCMT());
		
		
		// Case in which the three looks like this, look photo
		BulkUpdate upd = new BulkUpdate ("superprova", prova, s);
		
		upd.root.addGreaterCondition(s.getAttribute(0), 5);
		upd.root.addSmallerCondition(s.getAttribute(0), 10);
		
		upd.root.addTrueChild().set(s.getAttribute(1), "Modified");
		BulkCondition cond1 = upd.root.addFalseChild();
		cond1.addGreaterCondition(s.getAttribute(1), 14);
		cond1.addTrueChild().set(s.getAttribute(1), "Refused");
		cond1.addFalseChild().set(s.getAttribute(1), "Accepted");

		

		

		
		
		System.out.println(upd.generateMCMT());
		
		
//		Block b = new ParallelBlock("Ciao");
//		Block ccc = new Task("task_prova", it);
//		((ParallelBlock)b).addB1(ccc);
//		((ParallelBlock)b).addB2(new SequenceBlock("prova2"));
//		System.out.println(b.mcmt_translation());
//		System.out.println(ccc.mcmt_translation());
		
		ExclusiveChoiceBlock b = new ExclusiveChoiceBlock("exclusive");
		b.addB1(new SequenceBlock("prova1"));
		b.addB2(new SequenceBlock("prova2"));
		ConjunctiveSelectQuery ei = new ConjunctiveSelectQuery();
		ei.addBinaryCondition(true, c, "Modified");
		b.addCond(ei);
		System.out.print(b.mcmt_translation());

		


		

		


		
		
//		DeleteTransition del = new DeleteTransition("DeleteTrans1", prova);
//		del.delete(s, "id_r1", "Modified");
//		del.set(c, "id_r2");
//		System.out.println();
//		System.out.println(del.generateMCMT());
		
		
		// to do
		// reference variables and reference relations
		// change name to InsertSet
		// eevar system
		// builder pattern = starting from precondition
		
		
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
	
	
	public static void printDeclaration(RelationFactory rf, SortFactory sf, CaseVariableFactory cvf, ConstantFactory c) {
		System.out.println(sf.printSort());
		System.out.println(c.printConstants());
		System.out.println(rf.printCatalog());
		System.out.println(rf.printRepository());
		System.out.println(cvf.printCaseVariables());
		System.out.println(sf.printSortList());
		System.out.println(rf.printFunctions());
		System.out.println(c.printConstantList());

	}
	

}
