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
				
		ConjunctiveSelectQuery sel_winner_guard = new ConjunctiveSelectQuery(application.getAttribute(0), 
				application.getAttribute(1),application.getAttribute(2),application.getAttribute(3));
		sel_winner_guard.addBinaryCondition(true, application.getAttribute(3), "True");
		DeleteTransition sel_winner = new DeleteTransition("Sel_Winner", sel_winner_guard);
		sel_winner.delete(application, "jcid_app", "uid_app", "score", "eligible");
		sel_winner.set(jcid, "jcid_app");
		sel_winner.set(uid, "uid_app");
		sel_winner.set(winner, "uid_app");
		sel_winner.set(result, "eligible");
		sel_winner.set(qualif, "False");
		
//		ConjunctiveSelectQuery sel_winner_guard = new ConjunctiveSelectQuery(application.getAttribute(1));
//		sel_winner_guard.addBinaryCondition(true, application.getAttribute(3), "True");
//		InsertTransition sel_winner = new InsertTransition("Sel_Winner", sel_winner_guard);
//		sel_winner.set(winner, "uid_app");
//		sel_winner.set(result, "eligible");
//		sel_winner.set(qualif, "False");
			
		// BPMN specification
		ConjunctiveSelectQuery prova = new ConjunctiveSelectQuery(job_category.getAttribute(0));
		prova.addBinaryCondition(true, job_category.getAttribute(0), "HR");
		
		// example
		Task decide_eligible = new Task ("decide_eligible", markE);
		Task select_winner = new Task ("select_winner", sel_winner);

		
		SequenceBlock seq = new SequenceBlock("sequence_block");
		seq.addB1(decide_eligible);
		seq.addB2(select_winner);

		ProcessBlock root = new ProcessBlock("root_process");
		root.addB1(seq);
		
		MainProcess assign_job_process = new MainProcess(root);
	
		ConjunctiveSelectQuery safety_property = new ConjunctiveSelectQuery();
		safety_property.addBinaryCondition(true, root.life_cycle, "Enabled");
		safety_property.addBinaryCondition(true, winner, "Undef");
		assign_job_process.setSafety_formula(safety_property);
		
		assign_job_process.generateMCMT();

		


		

		
	}
}
