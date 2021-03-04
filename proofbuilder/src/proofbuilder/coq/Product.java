package proofbuilder.coq;

import java.util.Map;

public class Product extends Term {

	public final String boundVariable;
	public final Term domain;
	public final Term range;
	
	public Product(String boundVariable, Term domain, Term range) {
		this.boundVariable = boundVariable;
		this.domain = domain;
		this.range = range;
	}
	
	@Override
	public boolean equals(Term other) {
		if (other instanceof Product otherProduct) {
			if (!domain.equals(otherProduct.domain))
				return false;
			if (boundVariable == null)
				if (otherProduct.boundVariable == null)
					return range.equals(otherProduct.range);
				else
					return range.lift(0, 1).equals(otherProduct.range);
			else
				if (otherProduct.boundVariable == null)
					return range.equals(otherProduct.range.lift(0,  1));
				else
					return range.equals(otherProduct.range);
		}
		return false;
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
	
	public Term check(Context context) {
		Term domainType = domain.check(context);
		if (!(domainType instanceof Sort))
			throw typeError("Domain of product must be a type");
		Term rangeType;
		if (boundVariable == null)
			rangeType = range.check(context);
		else
			rangeType = range.check(Context.cons(context, boundVariable, domain));
		if (!(rangeType instanceof Sort))
			throw typeError("Range of product must be a type");
		
		if (domainType instanceof PropSort) {
			return rangeType;
		} else if (rangeType instanceof PropSort) {
			return rangeType;
		} else {
			if (((TypeSort)domainType).level > ((TypeSort)rangeType).level)
				return domainType;
			else
				return rangeType;
		}
	}
	
}
