package proofbuilder.coq;

import java.util.List;

public class PropSort extends Sort {
	
	PropSort() {}
	
	public ProofTree check(Context context) {
		return new ProofTree(context, this, Term.type(1), null, List.of());
	}
	
	public String toLaTeX(Context context, int precedence) {
		return "\\mathsf{Prop}";
	}
}
