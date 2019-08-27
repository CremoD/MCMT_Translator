package data_aware_bpmn.dataschema;

import data_aware_bpmn.exception.InvalidInputException;
import data_aware_bpmn.exception.UnmatchingSortException;

public interface Transition {
	public String generateMCMT() throws InvalidInputException;
	public void addTaskGuard(String toAdd);
	public void set(CaseVariable cv, String new_value) throws InvalidInputException, UnmatchingSortException;
}
