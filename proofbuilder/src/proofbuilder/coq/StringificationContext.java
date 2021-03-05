package proofbuilder.coq;

import java.util.ArrayList;

import java.util.List;
import java.util.function.Supplier;

public class StringificationContext {
	
	private List<String> variableNames = new ArrayList<String>();
	
	public void pushVariable(String name) {
		while (variableNames.contains(name))
			 name = name + "'";
		variableNames.add(name);
	}
	
	public void popVariable() {
		variableNames.remove(variableNames.size() - 1);
	}
	
	public <T> T withVariable(String name, Supplier<T> body) {
		pushVariable(name);
		T result = body.get();
		popVariable();
		return result;
	}

	public String getVariableName(int deBruijnIndex) {
		return variableNames.get(variableNames.size() - 1 - deBruijnIndex);
	}

}
