package proofbuilder.coq;

import java.util.List;

public class TypeSort extends Sort {

	public final int level;
	
	public String toString() {
		return "Type(" + level + ")";
	}
	
	TypeSort(int level) {
		this.level = level;
	}
	
	public ProofTree check(Context context) {
		return new ProofTree(context, this, Term.type(level + 1), null, List.of());
	}
	
	public String toLaTeX(Context context, int precedence) {
		return "Type" + (level == 0 ? "" : "_" + level);
	}
	
}
