package proofbuilder.coq;

import java.util.Arrays;

public class LiftedHoleProxy extends AbstractHole {
	
	public final AbstractHole hole;
	public final int startIndex;
	public final int nbBindings;
	private ProofTree[] childProofTrees = new ProofTree[1];
	
	@Override
	public HolesContext getHolesContext() {
		return hole.getHolesContext();
	}
	public String toString() {
		return "lift(" + hole + ", " + startIndex + ", " + nbBindings + ")";
	}
	
	public LiftedHoleProxy(AbstractHole hole, int startIndex, int nbBindings) {
		if (nbBindings < 0)
			throw new AssertionError();
		this.hole = hole;
		this.startIndex = startIndex;
		this.nbBindings = nbBindings;
	}
	
	@Override
	public void checkIsSort() {
		hole.checkIsSort();
	}
	
	@Override
	public ProofTree check(Context context) {
		ProofTree holeProofTree = hole.check(context.unlift(startIndex, nbBindings));
		return new ProofTree(context, this, holeProofTree.actualType.lift(startIndex, nbBindings), null, Arrays.asList(childProofTrees));
	}
	
	@Override
	public void checkEqualsCore(Term other) {
		if (other == this)
			return;
		hole.checkEqualsCore(other.lift(startIndex, -nbBindings));
	}
	
	@Override
	public Term lift(int startIndex, int nbBindings) {
		if (startIndex == this.startIndex && nbBindings == -this.nbBindings)
			return hole;
		if (startIndex == this.startIndex)
			return new LiftedHoleProxy(hole, startIndex, nbBindings + this.nbBindings);
		return new LiftedHoleProxy(this, startIndex, nbBindings);
	}
	
	@Override
	public String toLaTeX(Context context, int precedence) {
		return hole.toLaTeX(context.unlift(startIndex, nbBindings), precedence);
	}

	@Override
	public Term with(Term term, int index, boolean returnNullOnFailure) {
		if (startIndex <= index && index < startIndex + nbBindings) {
			if (startIndex == 0 && nbBindings == 1)
				return hole;
			return new LiftedHoleProxy(hole, startIndex, nbBindings - 1);
		}
		if (returnNullOnFailure)
			return null;
		throw new RuntimeException("Not yet implemented");
	}
	
	@Override
	public Term getHoleContents() {
		Term holeContents = hole.getHoleContents();
		if (holeContents instanceof Hole)
			return this;
		return holeContents.lift(startIndex, nbBindings);
	}
	
	@Override
	public Term reduce() {
		Term newHole = hole.reduce();
		if (newHole == hole)
			return this;
		return newHole.lift(startIndex, nbBindings);
	}

}
