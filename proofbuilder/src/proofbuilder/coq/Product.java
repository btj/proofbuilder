package proofbuilder.coq;

import java.util.List;

public class Product extends Term {

	public final String boundVariable;
	public final Term domain;
	public final Term range;
	
	public String toString() {
		return boundVariable == null ? "(" + domain + " -> " + range + ")" : "(forall " + boundVariable + ": " + domain + ", " + range + ")";
	}
	
	public Product(String boundVariable, Term domain, Term range) {
		this.boundVariable = boundVariable;
		this.domain = domain;
		this.range = range;
	}
	
	@Override
	public void checkEqualsCore(Term other) {
		if (other instanceof Product otherProduct) {
			domain.checkEquals(otherProduct.domain);
			if (boundVariable == null)
				if (otherProduct.boundVariable == null)
					range.checkEquals(otherProduct.range);
				else
					range.lift(0, 1).checkEquals(otherProduct.range);
			else
				if (otherProduct.boundVariable == null)
					range.checkEquals(otherProduct.range.lift(0,  1));
				else
					range.checkEquals(otherProduct.range);
		} else
			throw typeMismatchError(other, this);
	}
	
	public Term lift(int startIndex, int nbBindings) {
		Term newDomain = domain.lift(startIndex, nbBindings);
		Term newRange;
		if (boundVariable == null)
			newRange = range.lift(startIndex, nbBindings);
		else
			newRange = range.lift(startIndex + 1, nbBindings);
		if (newDomain == domain && newRange == range)
			return this;
		return new Product(boundVariable, domain, range);
	}
	
	public Term with(Term term, int index) {
		Term newDomain = domain.with(term, index);
		Term newRange;
		if (boundVariable == null)
			newRange = range.with(term, index);
		else
			newRange = range.with(term, index + 1);
		if (newDomain == domain && newRange == range)
			return this;
		return new Product(boundVariable, newDomain, newRange);
	}
	
	public Term range(Term argument) {
		if (boundVariable == null)
			return range;
		else
			return range.with(argument, 0);
	}
	
	public ProofTree check(Context context) {
		ProofTree domainTree = domain.check(context);
		if (!(domainTree.actualType instanceof Sort))
			throw typeError("Domain of product must be a type");
		ProofTree rangeTree;
		if (boundVariable == null)
			rangeTree = range.check(context);
		else
			rangeTree = range.check(Context.cons(context, boundVariable, domain));
		rangeTree.actualType.checkIsSort();
		
		Term type;
		if (domainTree.actualType instanceof PropSort) {
			type = rangeTree.actualType;
		} else if (rangeTree.actualType instanceof PropSort) {
			type = rangeTree.actualType;
		} else {
			if (((TypeSort)domainTree.actualType).level > ((TypeSort)rangeTree.actualType).level)
				type = domainTree.actualType;
			else
				type = rangeTree.actualType;
		}
		return new ProofTree(context, this, type, null, List.of(domainTree, rangeTree));
	}
	
	public String toLaTeX(Context context, int precedence) {
		if (boundVariable == null)
			return parenthesize(precedence, PREC_IMPL, domain.toLaTeX(context, PREC_IMPL + 1) + " \\Rightarrow " + range.toLaTeX(context, PREC_IMPL));
		else
			return parenthesize(precedence, 0, "\\forall " + boundVariable + (showDomains ? ": " + domain.toLaTeX(context, 0) : "") + ".\\; " +
					range.toLaTeX(Context.cons(context, boundVariable, domain), 0));
	}
	
}
