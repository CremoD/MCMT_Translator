package DataSchema;

import ControlFlow.*;
import Exception.EevarOverflowException;
import Exception.InvalidInputException;
import Exception.UnmatchingSortException;

public class test {

	public static void main(String[] args) throws UnmatchingSortException, InvalidInputException, EevarOverflowException {
		
		// Wrappers of relations, sorts, casevariables and constants
		RelationFactory relation_factory = RelationFactory.getInstance();
		SortFactory sort_factory = SortFactory.getInstance();
		CaseVariableFactory cv_factory = CaseVariableFactory.getInstance();
		ConstantFactory constant_factory = ConstantFactory.getInstance();

		// Sorts
		Sort r1_sort = sort_factory.getSort("R1_sort");
		Sort r2_sort = sort_factory.getSort("R2_sort");
		Sort string_sort = sort_factory.getSort("String_sort");
		
		// CaseVariables
		CaseVariable c = cv_factory.getCaseVariable("winner", r2_sort, true);
		CaseVariable c2 = cv_factory.getCaseVariable("looser", string_sort, true);

		// Constants, the constants strictly related to the blocks are already set at the beginning
		// into the wrapper class ConstantFactory
		Constant refused = constant_factory.getConstant("Refused", string_sort);
		Constant modified = constant_factory.getConstant("Modified", string_sort);
		Constant accepted = constant_factory.getConstant("Accepted", string_sort);
		
		// Relations
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

		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//											Process definition												//
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// create conjunctive query with list of columns of the select part
		ConjunctiveSelectQuery prova = new ConjunctiveSelectQuery(r1.getAttribute(0), r2.getAttribute(1));
		prova.addFrom(r1);
		prova.addBinaryCondition(true, r1.getAttribute(0), modified);
		prova.addBinaryCondition(true, r2.getAttribute(1), r1.getAttribute(3));
		
		InsertTransition it = new InsertTransition("InsertTrans1", prova);
		it.insert(s, "id_r1", "a1_r2");
		it.set(c, "id_r2");
		
		InsertTransition it2 = new InsertTransition("InsertTrans2", prova);
		it2.set(c, "id_r2");
		
		
		// Case in which the three looks like this, look photo
		BulkUpdate upd = new BulkUpdate ("superprova", prova, s);
		
		upd.root.addGreaterCondition(s.getAttribute(0), 5);
		upd.root.addSmallerCondition(s.getAttribute(0), 10);
		
		upd.root.addTrueChild().set(s.getAttribute(1), "Modified");
		BulkCondition cond1 = upd.root.addFalseChild();
		cond1.addGreaterCondition(s.getAttribute(1), 14);
		cond1.addTrueChild().set(s.getAttribute(1), "Refused");
		cond1.addFalseChild().set(s.getAttribute(1), "Accepted");

		// example
		
		Task t1 = new Task ("task1", it);
		Task t2 = new Task ("task2", it2);
		Task t3 = new Task ("task3", upd);

		ConjunctiveSelectQuery ciao = new ConjunctiveSelectQuery();
		ciao.addBinaryCondition(true, c, "Ciao");
		LoopBlock lb = new LoopBlock("loop", ciao);
		lb.addB1(t2);
		lb.addB2(t3);
		
		SequenceBlock seq = new SequenceBlock("sequence_block");
		seq.addB1(t1);
		seq.addB2(lb);
		
		ProcessBlock b = new ProcessBlock("root_process");
		b.addB1(seq);
		
		MainProcess bigProcess = new MainProcess(b);
		
		printDeclaration(relation_factory, sort_factory, cv_factory, constant_factory);
		System.out.println(EevarManager.printEevar());
		System.out.print(bigProcess.process_mcmt_generation());
		
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
