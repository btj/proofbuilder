package proofbuilder.coq;

import java.util.List;

public class Lambda extends Term {

	public final String boundVariable;
	public final Term domain;
	public final Term body;
	
	public String toString() {
		return "(fun " + boundVariable + ": " + domain + " => " + body + ")";
	}
	
	public Lambda(String boundVariable, Term domain, Term body) {
		this.boundVariable = boundVariable;
		this.domain = domain;
		this.body = body;
	}
	
	@Override
	public void checkEqualsCore(Term other) {
		if (other instanceof Lambda otherLambda) {
			domain.checkEquals(otherLambda.domain);
			body.checkEquals(otherLambda.body);
		} else
			throw typeMismatchError(other, this);
	}
	
	public Term lift(int startIndex, int nbBindings) {
		Term newDomain = domain.lift(startIndex, nbBindings);
		Term newBody = body.lift(startIndex + 1, nbBindings);
		if (newDomain == domain && newBody == body)
			return this;
		return new Lambda(boundVariable, newDomain, newBody);
	}
	
	public Term with(Term term, int index, boolean returnNullOnFailure) {
		Term newDomain = domain.with(term, index, returnNullOnFailure);
		if (newDomain == null)
			return null;
		Term newBody = body.with(term, index + 1, returnNullOnFailure);
		if (newBody == null)
			return null;
		if (newDomain == domain && newBody == body)
			return this;
		return new Lambda(boundVariable, newDomain, newBody);
	}
	
	public ProofTree check(Context context) {
		ProofTree domainTree = domain.check(context);
		if (!(domainTree.actualType instanceof Sort))
			throw typeError("Domain of lambda must be a type");
		ProofTree bodyTree = body.check(Context.cons(context, boundVariable, domain));
		boolean isImplication = domainTree.actualType instanceof PropSort;
		Term rangeType = bodyTree.actualType;
		if (isImplication)
			rangeType = rangeType.lift(0, -1); // TODO: First check if #0 appears free in the range? This will crash if it does.
		Term type = new Product(isImplication ? null : boundVariable, domain, rangeType);
		return new ProofTree(context, this, type, null, List.of(domainTree, bodyTree));
	}
	
	public String toLaTeX(Context context, int precedence) {
		return parenthesize(precedence, 0, "\\lambda " + boundVariable + (showDomains ? ": " + domain.toLaTeX(context, 0) : "") + ".\\; " +
				body.toLaTeX(Context.cons(context, boundVariable, domain), 0));
	}
	
	@Override
	public Term applyTo(Term argument) {
		Term result = this.body.with(argument, 0, true);
		if (result != null)
			return result;
		return super.applyTo(argument);
	}
	
	@Override
	public Term reduce() {
		Term newDomain = domain.reduce();
		Term newBody = body.reduce();
		if (newDomain == domain && newBody == body)
			return this;
		return new Lambda(boundVariable, newDomain, newBody);
	}
	
}
