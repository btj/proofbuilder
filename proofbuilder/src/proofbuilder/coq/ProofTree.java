package proofbuilder.coq;

import java.util.List;

public class ProofTree {
	
	public final Context context;
	public final Term term;
	public final Term actualType;
	public final Term expectedType;
	public final List<ProofTree> children;
	
	public ProofTree(Context context, Term term, Term actualType, Term expectedType, List<ProofTree> children) {
		this.context = context;
		this.term = term;
		this.actualType = actualType;
		this.expectedType = expectedType;
		this.children = List.copyOf(children);
	}
	
	public ProofTree withExpectedType(Term expectedType) {
		return new ProofTree(context, term, actualType, expectedType, children);
	}

	public Term getType() {
		if (expectedType != null)
			return expectedType;
		else
			return actualType;
	}
	
	public String getRuleAsLaTeX() {
		if (term instanceof Lambda)
			return (((Product)getType()).boundVariable == null ? "\\Rightarrow" : "\\forall") + "_{I^" + children.get(1).context.getVariableName(0) + "}";
		else if (term instanceof PropSort)
			return "\\mathsf{Prop}";
		else if (term instanceof TypeSort)
			return "\\mathsf{Type}";
		else if (term instanceof Product)
			return "\\Pi";
		else if (term instanceof Variable)
			return term.toLaTeX(context, 0);
		else if (term instanceof Application)
			return ((Product)children.get(0).getType()).boundVariable == null ? "\\Rightarrow_E" : "\\forall_E";
		else if (term instanceof Constant constant) {
			return constant.getRuleAsLaTeX(context);
		} else
			throw new AssertionError();
	}
}
