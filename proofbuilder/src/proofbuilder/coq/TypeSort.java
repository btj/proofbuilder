package proofbuilder.coq;

import java.util.Map;

public class TypeSort extends Sort {

	public final int level;
	
	TypeSort(int level) {
		this.level = level;
	}
	
	@Override
	public boolean equals(Term other) {
		return other == this;
	}
	
	public Term check(Context context) {
		return Term.type(level + 1);
	}
	
}
