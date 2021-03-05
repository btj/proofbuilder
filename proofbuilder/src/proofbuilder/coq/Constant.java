package proofbuilder.coq;

import java.util.List;

public class Constant extends Term {
	
	public final String name;
	public final String ruleLaTeX;
	public final Term type;
	public final int nbArguments;
	
	public Constant(String name, Term type, String ruleLaTeX, int nbArguments) {
		this.name = name;
		this.type = type;
		this.ruleLaTeX = ruleLaTeX;
		this.nbArguments = nbArguments;
	}
	
	public Constant(String name, Term type, String ruleLaTeX) {
		this(name, type, ruleLaTeX, 0);
	}
	
	public Constant(String name, Term type) {
		this(name, type, "\\textsc{" + name.replace("_", "\\_") + "}");
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
		return ruleLaTeX;
	}

}
