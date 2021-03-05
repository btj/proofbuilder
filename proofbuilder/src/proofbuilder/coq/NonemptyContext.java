package proofbuilder.coq;

public class NonemptyContext extends Context {
	
	public final Context outerContext;
	public final String name;
	public final Term type;
	
	public NonemptyContext(Context outerContext, String name, Term type) {
		this.outerContext = outerContext;
		while (outerContext.containsName(name))
			name = name + "'";
		this.name = name;
		this.type = type;
	}
	
	public boolean containsName(String name) {
		return this.name.equals(name) || outerContext.containsName(name);
	}
	
	public String getVariableName(int index) {
		return index == 0 ? name : outerContext.getVariableName(index - 1);
	}

}
