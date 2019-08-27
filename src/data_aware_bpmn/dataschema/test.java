package data_aware_bpmn.dataschema;

import data_aware_bpmn.controlflow.*;
import data_aware_bpmn.exception.EevarOverflowException;
import data_aware_bpmn.exception.InvalidInputException;
import data_aware_bpmn.exception.UnmatchingSortException;

public class test {

	public static void main(String[] args) throws UnmatchingSortException, InvalidInputException, EevarOverflowException {
		
		// Wrappers of relations, sorts, casevariables and constants
		RelationFactory relation_factory = RelationFactory.getInstance();
		SortFactory sort_factory = SortFactory.getInstance();
		CaseVariableFactory cv_factory = CaseVariableFactory.getInstance();
		ConstantFactory constant_factory = ConstantFactory.getInstance();

//		// Sorts
//		Sort r1_sort = sort_factory.getSort("R1_sort");
//		Sort r2_sort = sort_factory.getSort("R2_sort");
//		Sort string_sort = sort_factory.getSort("String_sort");
//		
//		// CaseVariables
//		CaseVariable c = cv_factory.getCaseVariable("winner", r2_sort, true);
//		CaseVariable c2 = cv_factory.getCaseVariable("looser", string_sort, true);
//
//		// Constants, the constants strictly related to the blocks are already set at the beginning
//		// into the wrapper class ConstantFactory
//		Constant refused = constant_factory.getConstant("Refused", string_sort);
//		Constant modified = constant_factory.getConstant("Modified", string_sort);
//		Constant accepted = constant_factory.getConstant("Accepted", string_sort);
//		
//		// Relations
//		CatalogRelation r1 = relation_factory.getCatalogRelation("R1");
//		r1.addAttribute("id_r1", r1_sort);
//		r1.addAttribute("a1_r1", r2_sort);
//		r1.addAttribute("a2_r1", string_sort);
//		r1.addAttribute("a3_r1", string_sort);
//		
//		CatalogRelation r2 = relation_factory.getCatalogRelation("R2");
//		r2.addAttribute("id_r2", r2_sort);
//		r2.addAttribute("a1_r2", string_sort);
//		
//		RepositoryRelation s = relation_factory.getRepositoryRelation("S");
//		s.addAttribute("a1_s", r1_sort);
//		s.addAttribute("a2_s", string_sort);
//		
//		RepositoryRelation z = relation_factory.getRepositoryRelation("Z");
//		z.addAttribute("a1_z", r1_sort);
//		z.addAttribute("a2_z", string_sort);
//
//		
//		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//		//											Process definition												//
//		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//		
//		// create conjunctive query with list of columns of the select part
//		ConjunctiveSelectQuery prova = new ConjunctiveSelectQuery(r1.getAttribute(0), r2.getAttribute(1));
//		prova.addFrom(r1);
//		prova.addBinaryCondition(true, r1.getAttribute(0), modified);
//		prova.addBinaryCondition(true, r2.getAttribute(1), r1.getAttribute(3));
//		
//		InsertTransition it = new InsertTransition("InsertTrans1", prova);
//		it.insert(s, "id_r1", "a1_r2");
//		it.set(c, "id_r2");
//		
//		InsertTransition it2 = new InsertTransition("InsertTrans2", prova);
//		it2.set(c, "id_r2");
//		
//		
//		// Case in which the three looks like this, look photo
//		BulkUpdate upd = new BulkUpdate ("superprova", prova, s);
//		
//		upd.root.addGreaterCondition(s.getAttribute(0), 5);
//		upd.root.addSmallerCondition(s.getAttribute(0), 10);
//		
//		upd.root.addTrueChild().set(s.getAttribute(1), "Modified");
//		BulkCondition cond1 = upd.root.addFalseChild();
//		cond1.addGreaterCondition(s.getAttribute(1), 14);
//		cond1.addTrueChild().set(s.getAttribute(1), "Refused");
//		cond1.addFalseChild().set(s.getAttribute(1), "Accepted");
//
////		// example
////		
////		Task t1 = new Task ("task1", it);
////		Task t2 = new Task ("task2", it2);
////		Task t3 = new Task ("task3", upd);
////
////		ConjunctiveSelectQuery ciao = new ConjunctiveSelectQuery();
////		ciao.addBinaryCondition(true, c, "Ciao");
////		LoopBlock lb = new LoopBlock("loop", ciao);
////		lb.addB1(t2);
////		lb.addB2(t3);
////		
////		SequenceBlock seq = new SequenceBlock("sequence_block");
////		seq.addB1(t1);
////		seq.addB2(lb);
////		
////		ProcessBlock b = new ProcessBlock("root_process");
////		b.addB1(seq);
//		
//		
//		Task t1 = new Task ("task1", it);
//		Task t2 = new Task ("task2", it2);
//		Task t3 = new Task("task3", upd);
////		
////		
////		
////		ForwardExceptionBlock exc = new ForwardExceptionBlock("exception");
////		exc.addB1(t2);
////		exc.addB2(t3);
////		exc.addB3(t1);
////		
////		ProcessBlock b = new ProcessBlock("root_process");
////		b.addB1(exc);
////		
////		
////		
////		
////		
////		MainProcess bigProcess = new MainProcess(b);
//		ForwardExceptionBlock exc = new ForwardExceptionBlock("exception");
//		
//
//		Event e1 = new Event ("app_received", it);
//		Event e2 = new Event ("stop");
//		
//		ErrorEventBlock ee= new ErrorEventBlock("error_event", exc);
//		ee.addB1(e1);
//		ee.addB2(e2);
//		
//		exc.addB1(t1);
//		exc.addB2(t2);
//		exc.addB3(ee);
//		
//		System.out.println(ee.mcmt_translation());
//		
//		printDeclaration(relation_factory, sort_factory, cv_factory, constant_factory);
//		System.out.println(EevarManager.printEevar());
//		//System.out.print(bigProcess.process_mcmt_generation());
	
		
		//////////////////////////////// JOB HIRING EXAMPLE ////////////////////////////////
		
		// sorts
		Sort job_id = SortFactory.getInstance().getSort("jobcatID");
		Sort user_id = sort_factory.getSort("userID");
		Sort string_sort = sort_factory.getSort("String_sort");
		Sort int_sort = sort_factory.getSort("Num_score");
		Sort bool = sort_factory.getSort("bool");
		Sort num_age = sort_factory.getSort("Num_age");


		// catalog 
		CatalogRelation job_category = relation_factory.getCatalogRelation("Job_Category");
		job_category.addAttribute("jcid", job_id);
		
		CatalogRelation user = relation_factory.getCatalogRelation("user");
		user.addAttribute("uid", user_id);
		user.addAttribute("name",string_sort);
		user.addAttribute("age", num_age);
		
		// repository
		RepositoryRelation application = relation_factory.getRepositoryRelation("Application");
		application.addAttribute("jcid_app", job_id);
		application.addAttribute("uid_app", user_id);
		application.addAttribute("score", int_sort);
		application.addAttribute("eligible", bool);
		
		
		
		// Constant
		constant_factory.getConstant("True", bool);
		constant_factory.getConstant("False", bool);
		
		// case variables
		CaseVariable jcid = cv_factory.getCaseVariable("jcid", job_id, true);
		CaseVariable uid = cv_factory.getCaseVariable("uid", user_id, true);
		CaseVariable qualif = cv_factory.getCaseVariable("qualif", bool, true);
		CaseVariable result = cv_factory.getCaseVariable("result", bool, true);
		CaseVariable winner = cv_factory.getCaseVariable("winner", user_id, true);


		
		// some transitions
		ConjunctiveSelectQuery ins_job_cat_guard = new ConjunctiveSelectQuery(job_category.getAttribute(0));
		InsertTransition ins_job_cat = new InsertTransition("Insert_job_category", ins_job_cat_guard);
		ins_job_cat.set(jcid, "jcid");
		
		ConjunctiveSelectQuery ins_user_guard = new ConjunctiveSelectQuery(user.getAttribute(0));
		InsertTransition ins_user = new InsertTransition("Insert_User", ins_user_guard);
		ins_user.set(uid, "uid");
		
		InsertTransition check_qual = new InsertTransition("Check_Qual", new ConjunctiveSelectQuery());
		check_qual.set(qualif, "True");
		
		BulkUpdate markE = new BulkUpdate("MarkE", new ConjunctiveSelectQuery(), application);
		markE.root.addGreaterCondition(application.getAttribute(2), 80);
		markE.root.addTrueChild().set(application.getAttribute(3), "True");
		markE.root.addFalseChild().set(application.getAttribute(3), "False");
		
		ConjunctiveSelectQuery sel_winner_guard = new ConjunctiveSelectQuery(application.getAttribute(0), application.getAttribute(1),application.getAttribute(2),application.getAttribute(3));
		sel_winner_guard.addBinaryCondition(true, application.getAttribute(3), "True");
		DeleteTransition sel_winner = new DeleteTransition("Insert_User", sel_winner_guard);
		sel_winner.delete(application, "jcid_app", "uid_app", "score", "eligible");
		sel_winner.set(jcid, "jcid_app");
		sel_winner.set(uid, "uid_app");
		sel_winner.set(winner, "uid_app");
		sel_winner.set(result, "eligible");
		sel_winner.set(qualif, "False");
				
		// BPMN specification
		ConjunctiveSelectQuery prova = new ConjunctiveSelectQuery(job_category.getAttribute(0));
		prova.addBinaryCondition(true, job_category.getAttribute(0), "HR");
		System.out.println(prova.getQueryString());
		

		

		

		


		

		
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
