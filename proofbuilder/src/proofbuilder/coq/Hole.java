package proofbuilder.coq;

import java.util.Arrays;
import java.util.List;

public class Hole extends Term {
	
	public final HolesContext holesContext;
	public final int id;
	private Context context;
	private Term type;
	private Term contents;
	private ProofTree[] childProofTrees = new ProofTree[1];
	
	public Term getType() {
		return type;
	}

	Hole(HolesContext holesContext, int id) {
		this.holesContext = holesContext;
		this.id = id;
	}
	
	@Override
	public ProofTree check(Context context) {
		if (contents != null) {
			ProofTree contentsTree = contents.check(context);
			childProofTrees[0] = contentsTree;
			return new ProofTree(context, this, contentsTree.actualType, null, Arrays.asList(childProofTrees));
		}
		this.context = context;
		if (type == null) {
			Hole typeHole = holesContext.createHole();
			typeHole.context = context;
			type = typeHole;
		}
		return new ProofTree(context, this, type, null, Arrays.asList(childProofTrees));
	}
	
	@Override
	public ProofTree checkAgainst(Context context, Term expectedType) {
		if (contents != null) {
			ProofTree contentsTree = contents.checkAgainst(context, expectedType);
			childProofTrees[0] = contentsTree;
			return new ProofTree(context, this, contentsTree.actualType, expectedType, Arrays.asList(childProofTrees));
		}
		this.context = context;
		if (type == null)
			type = expectedType;
		return new ProofTree(context, this, type, type, Arrays.asList(childProofTrees));
	}
	
	public Term getHoleContents() {
		if (contents != null)
			return contents;
		return this;
	}
	
	@Override
	public void checkEqualsCore(Term other) {
		if (contents != null)
			contents.checkEquals(other);
		if (other == this)
			return;
		if (context == null) throw new AssertionError();
		holesContext.addUndoAction(() -> {
			childProofTrees[0] = null;
			contents = null;
		});
		if (type != null) {
			childProofTrees[0] = other.checkAgainst(context, type);
		} else
			childProofTrees[0] = other.check(context);
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
