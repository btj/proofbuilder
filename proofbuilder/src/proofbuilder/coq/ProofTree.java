package proofbuilder.coq;

import java.util.List;

public class ProofTree {
	
	public final Context context;
	public final Term term;
	public final Term actualType;
	public final Term expectedType;
	public final List<ProofTree> children;
	public final Constant uncurriedFunction;
	public final int uncurriedNbArgs;
	public final List<ProofTree> uncurriedChildren;
	
	public ProofTree(Context context, Term term, Term actualType, Term expectedType, List<ProofTree> children) {
		this.context = context;
		this.term = term;
		this.actualType = actualType;
		this.expectedType = expectedType;
		this.children = List.copyOf(children);
		
		Constant uncurriedFunction = null;
		int uncurriedNbArgs = 0;
		if (term instanceof Constant constant) {
			uncurriedFunction = constant;
			uncurriedNbArgs = 0;
		} else if (term instanceof Application application) {
			ProofTree functionTree = children.get(0);
			if (functionTree.uncurriedFunction != null && functionTree.uncurriedNbArgs < functionTree.uncurriedFunction.nbArguments) {
				uncurriedFunction = functionTree.uncurriedFunction;
				uncurriedNbArgs = functionTree.uncurriedNbArgs + 1;
			}
		}
		this.uncurriedFunction = uncurriedFunction;
		this.uncurriedNbArgs = uncurriedNbArgs;
		if (uncurriedFunction != null && uncurriedNbArgs == uncurriedFunction.nbArguments) {
			ProofTree[] uncurriedChildren = new ProofTree[uncurriedNbArgs];
			ProofTree tree = this;
			for (int i = uncurriedNbArgs - 1; 0 <= i; i--) {
				uncurriedChildren[i] = tree.children.get(1);
				tree = tree.children.get(0);
			}
			this.uncurriedChildren = List.of(uncurriedChildren);
		} else
			this.uncurriedChildren = children;
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
		Term term = this.term.getHoleContents();
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
			if (uncurriedFunction != null && uncurriedNbArgs == uncurriedFunction.nbArguments)
				return uncurriedFunction.getRuleAsLaTeX(context);
			else
				return ((Product)children.get(0).getType()).boundVariable == null ? "\\Rightarrow_E" : "\\forall_E";
		else if (term instanceof Constant constant) {
			return constant.getRuleAsLaTeX(context);
		} else if (term instanceof Hole hole) {
			return "?_{" + hole.id + "}";
		} else
			throw new AssertionError();
	}
}
