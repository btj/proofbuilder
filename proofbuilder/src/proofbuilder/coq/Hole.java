package proofbuilder.coq;

import java.util.Arrays;
import java.util.List;

public class Hole extends AbstractHole {
	
	public final HolesContext holesContext;
	public final int id;
	private Context context;
	private Term type;
	private boolean mustBeSort;
	private Term contents;
	private ProofTree[] childProofTrees = new ProofTree[1];
	
	@Override
	public HolesContext getHolesContext() {
		return holesContext;
	}
	
	public String toString() {
		return "hole(" + id + ", " + contents + (contents == null ? ", " + type : "") + ")";
	}
	
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
	
	@Override
	public void checkIsSort() {
		if (contents != null)
			contents.checkIsSort();
		mustBeSort = true;
	}
	
	public Term getHoleContents() {
		if (contents != null)
			return contents.getHoleContents();
		return this;
	}
	
	@Override
	public void checkEqualsCore(Term other) {
		if (contents != null) {
			contents.checkEquals(other);
			return;
		}
		if (other == this)
			return;
		if (context == null) throw new AssertionError();
		ProofTree oldProofTree = childProofTrees[0];
		Term oldContents = contents;
		holesContext.addUndoAction(() -> {
			childProofTrees[0] = oldProofTree;
			contents = oldContents;
		});
		if (type != null) {
			childProofTrees[0] = other.checkAgainst(context, type);
		} else
			childProofTrees[0] = other.check(context);
		if (mustBeSort)
			other.checkIsSort();
		contents = other;
	}
	
	@Override
	public Term lift(int startIndex, int nbBindings) {
		if (contents != null)
			return contents.lift(startIndex, nbBindings);
		if (nbBindings == 0)
			return this;
	    if (startIndex == 0 && nbBindings < 0) {
	    	this.checkEqualsCore(new LiftedHoleProxy(holesContext.createHole(), 0, -nbBindings));
	    	return contents.lift(startIndex, nbBindings);
	    }
		return new LiftedHoleProxy(this, startIndex, nbBindings);
	}
	
	@Override
	public String toLaTeX(Context context, int precedence) {
		if (contents != null)
			return contents.toLaTeX(context, precedence);
		return "?_{" + id + "}";
	}
	
	@Override
	public Term with(Term term, int index, boolean returnNullOnFailure) {
		if (contents != null)
			return contents.with(term, index);
		if (returnNullOnFailure)
			return null;
		throw new RuntimeException("Not yet implemented");
	}

	public boolean isFilled() {
		return contents != null;
	}
	
	@Override
	public Term applyTo(Term argument) {
		if (contents != null)
			return contents.applyTo(argument);
		return super.applyTo(argument);
	}
	
	public Term reduce() {
		if (contents != null)
			return contents.reduce();
		this.reduceType();
		return this;
	}
	
	public void reduceType() {
		Term oldType = this.type;
		holesContext.addUndoAction(() -> {
			this.type = oldType;
		});
		this.type = this.type.reduce();
	}
	
}
