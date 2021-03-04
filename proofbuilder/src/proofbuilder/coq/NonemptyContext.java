package proofbuilder.coq;

public class NonemptyContext extends Context {
	
	public final Context outerContext;
	public final String name;
	public final Term type;
	
	public NonemptyContext(Context outerContext, String name, Term type) {
		this.outerContext = outerContext;
		this.name = name;
		this.type = type;
	}

}
