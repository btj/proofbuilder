package proofbuilder.coq;

public class Lambda extends Term {

	public final String boundVariable;
	public final Term domain;
	public final Term body;
	
	public Lambda(String boundVariable, Term domain, Term body) {
		this.boundVariable = boundVariable;
		this.domain = domain;
		this.body = body;
	}
	
}
