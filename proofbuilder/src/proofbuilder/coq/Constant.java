package proofbuilder.coq;

public class Constant extends Term {
	
	public final String name;
	public final Term type;
	
	public Constant(String name, Term type) {
		this.name = name;
		this.type = type;
	}

}
