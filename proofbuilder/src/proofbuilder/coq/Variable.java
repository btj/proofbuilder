package proofbuilder.coq;

import java.util.List;

public class Variable extends Term {
	
	public final int deBruijnIndex;
	
	public String toString() { return "#" + deBruijnIndex; }
	
	public Variable(int deBruijnIndex) {
		this.deBruijnIndex = deBruijnIndex;
	}
	
	@Override
	public void checkEqualsCore(Term other) {
		if (!(other instanceof Variable otherVariable && deBruijnIndex == otherVariable.deBruijnIndex))
			throw typeMismatchError(other, this);
	}
	
	public Term lift(int startIndex, int nbBindings) {
		if (deBruijnIndex < startIndex)
			return this;
		if (deBruijnIndex + nbBindings < 0)
			throw typeError("Trying to unify a term with a hole that uses variables that are not in scope at the hole.");
		return new Variable(deBruijnIndex + nbBindings);
	}
	
	public Term with(Term term, int index, boolean returnNullOnFailure) {
		if (deBruijnIndex == index)
			return term.lift(0, index);
		else if (deBruijnIndex > index)
			return new Variable(deBruijnIndex - 1);
		else
			return this;
	}
	
	public ProofTree check(Context context) {
		Context bindingContext = context;
		for (int i = 0; i < deBruijnIndex; i++) {
			if (bindingContext instanceof NonemptyContext nonemptyContext) {
				bindingContext = nonemptyContext.outerContext;
			} else
				throw typeError("Unbound variable");
		}
		if (bindingContext instanceof NonemptyContext nonemptyContext) {
			return new ProofTree(context, this, nonemptyContext.type.lift(0, deBruijnIndex + 1), null, List.of());
		} else
			throw typeError("Unbound variable");
	}
	
	@Override
	public String toLaTeX(Context context, int precedence) {
		return context.getVariableName(deBruijnIndex);
	}

}
