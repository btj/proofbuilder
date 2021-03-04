package proofbuilder.coq;

import java.util.Map;

public class PropSort extends Sort {
	
	PropSort() {}

	@Override
	public boolean equals(Term other) {
		return other == this;
	}
	
	public Term check(Context context) {
		return Term.type(1);
	}
	
}
