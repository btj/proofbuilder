package proofbuilder.coq;

import java.util.List;

public class Constant extends Term {
	
	public final String name;
	public final Term type;
	
	public Constant(String name, Term type) {
		this.name = name;
		this.type = type;
	}
	
	@Override
	public boolean equals(Term other) {
		return this == other;
	}
	
	public Term lift(int startIndex, int nbBindings) { return this; }
	
	public Term with(Term term, int index) { return this; }
	
	public ProofTree check(Context context) { return new ProofTree(context, this, type, null, List.of()); }
	
	public String toLaTeX(Context context, int precedence) {
		return "\\mathsf{" + name + "}";
	}
	
	public String getRuleAsLaTeX(Context context) {
		return "\\textsc{" + name + "}";
	}

}
