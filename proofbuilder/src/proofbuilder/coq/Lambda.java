package proofbuilder.coq;

import java.util.List;

public class Lambda extends Term {

	public final String boundVariable;
	public final Term domain;
	public final Term body;
	
	public Lambda(String boundVariable, Term domain, Term body) {
		this.boundVariable = boundVariable;
		this.domain = domain;
		this.body = body;
	}
	
	@Override
	public boolean equals(Term other) {
		if (other instanceof Lambda otherLambda) {
			if (!domain.equals(otherLambda.domain))
				return false;
			return body.equals(otherLambda.body);
		}
		return false;
	}
	
	public Term lift(int startIndex, int nbBindings) {
		Term newDomain = domain.lift(startIndex, nbBindings);
		Term newBody = body.lift(startIndex + 1, nbBindings);
		if (newDomain == domain && newBody == body)
			return this;
		return new Lambda(boundVariable, newDomain, newBody);
	}
	
	public Term with(Term term, int index) {
		Term newDomain = domain.with(term, index);
		Term newBody = body.with(term, index + 1);
		if (newDomain == domain && newBody == body)
			return this;
		return new Lambda(boundVariable, newDomain, newBody);
	}
	
	public ProofTree check(Context context) {
		ProofTree domainTree = domain.check(context);
		if (!(domainTree.actualType instanceof Sort))
			throw typeError("Domain of lambda must be a type");
		ProofTree bodyTree = body.check(Context.cons(context, boundVariable, domain));
		return new ProofTree(context, this, new Product(boundVariable, domain, bodyTree.actualType), null, List.of(domainTree, bodyTree));
	}
	
	public String toLaTeX(Context context, int precedence) {
		return parenthesize(precedence, 0, "\\lambda " + boundVariable + ": " + domain.toLaTeX(context, 0) + ".\\; " +
				body.toLaTeX(Context.cons(context, boundVariable, domain), 0));
	}
	
}
