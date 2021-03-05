package proofbuilder.coq;

import java.util.List;

public class PropSort extends Sort {
	
	PropSort() {}

	@Override
	public boolean equals(Term other) {
		return other == this;
	}
	
	public ProofTree check(Context context) {
		return new ProofTree(context, this, Term.type(1), null, List.of());
	}
	
	public String toLaTeX(Context context, int precedence) {
		return "\\mathsf{Prop}";
	}
}
