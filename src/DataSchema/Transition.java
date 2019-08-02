package DataSchema;

import Exception.InvalidInputException;
import Exception.UnmatchingSortException;

public interface Transition {
	public String generateMCMT() throws InvalidInputException;
	public void addTaskGuard(String toAdd);
	public void set(CaseVariable cv, String new_value) throws InvalidInputException, UnmatchingSortException;
}
