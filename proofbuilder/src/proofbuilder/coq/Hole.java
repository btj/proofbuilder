package proofbuilder.coq;

import java.util.List;

public class Hole extends Term {
	
	public final HolesContext holesContext;
	public final int id;
	private Context context;
	private Term type;
	private Term contents;

	Hole(HolesContext holesContext, int id) {
		this.holesContext = holesContext;
		this.id = id;
	}
	
	@Override
	public ProofTree check(Context context) {
		this.context = context;
		if (type == null)
			type = holesContext.createHole();
		return new ProofTree(context, this, type, null, List.of());
	}
	
	public Term getHoleContents() {
		if (contents != null)
			return contents;
		return this;
	}
	
	@Override
	public void checkEquals(Term other) {
		if (contents != null)
			contents.checkEquals(other);
		other = other.getHoleContents();
		if (other == this)
			return;
		if (type != null) {
			if (context == null) throw new AssertionError();
			other.checkAgainst(context, type);
		}
		contents = other;
	}
	
	@Override
	public Term lift(int startIndex, int nbBindings) {
		if (contents != null)
			return contents.lift(startIndex, nbBindings);
		throw new RuntimeException("Not yet implemented");
	}
	
	@Override
	public String toLaTeX(Context context, int precedence) {
		if (contents != null)
			return contents.toLaTeX(context, precedence);
		return "?_{" + id + "}";
	}
	
	@Override
	public Term with(Term term, int index) {
		if (contents != null)
			return contents.with(term, index);
		throw new RuntimeException("Not yet implemented");
	}
	
}
