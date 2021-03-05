package proofbuilder.coq;

import java.util.List;

public class TypeSort extends Sort {

	public final int level;
	
	TypeSort(int level) {
		this.level = level;
	}
	
	@Override
	public boolean equals(Term other) {
		return other == this;
	}
	
	public ProofTree check(Context context) {
		return new ProofTree(context, this, Term.type(level + 1), null, List.of());
	}
	
	public String toLaTeX(Context context, int precedence) {
		return "Type" + (level == 0 ? "" : "_" + level);
	}
	
}
