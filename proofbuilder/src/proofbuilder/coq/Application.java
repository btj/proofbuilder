package proofbuilder.coq;

public class Application extends Term {
	
	public final Term function;
	public final Term argument;
	
	public Application(Term function, Term argument) {
		this.function = function;
		this.argument = argument;
	}

}
