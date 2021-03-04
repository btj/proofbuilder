package proofbuilder.coq;

import java.util.Map;

public class Variable extends Term {
	
	public final int deBruijnIndex;
	
	public Variable(int deBruijnIndex) {
		this.deBruijnIndex = deBruijnIndex;
	}
	
	@Override
	public boolean equals(Term other) {
		if (other instanceof Variable otherVariable) {
			return deBruijnIndex == otherVariable.deBruijnIndex;
		}
		return false;
	}
	
	public Term lift(int startIndex, int nbBindings) {
		if (deBruijnIndex < startIndex)
			return this;
		return new Variable(deBruijnIndex + nbBindings);
	}
	
	public Term with(Term term, int index) {
		if (deBruijnIndex == index)
			return term.lift(0, index);
		return this;
	}
	
	public Term check(Context context) {
		for (int i = 0; i < deBruijnIndex; i++) {
			if (context instanceof NonemptyContext nonemptyContext) {
				context = nonemptyContext.outerContext;
			} else
				throw typeError("Unbound variable");
		}
		if (context instanceof NonemptyContext nonemptyContext) {
			return nonemptyContext.type.lift(0, deBruijnIndex + 1);
		} else
			throw typeError("Unbound variable");
	}

}
