package proofbuilder.coq;

public class Product extends Term {

	public final String boundVariable;
	public final Term domain;
	public final Term range;
	
	public Product(String boundVariable, Term domain, Term range) {
		this.boundVariable = boundVariable;
		this.domain = domain;
		this.range = range;
	}
	
}
