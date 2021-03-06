package proofbuilder.coq;

import java.util.Arrays;

public class LiftedHoleProxy extends AbstractHole {
	
	public final Term hole;
	public final int startIndex;
	public final int nbBindings;
	private ProofTree[] childProofTrees = new ProofTree[1];
	
	public LiftedHoleProxy(Term hole, int startIndex, int nbBindings) {
		if (nbBindings < 0)
			throw new AssertionError();
		this.hole = hole;
		this.startIndex = startIndex;
		this.nbBindings = nbBindings;
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
	public Term with(Term term, int index) {
		if (startIndex <= index && index < startIndex + nbBindings)
			return this;
		throw new RuntimeException("Not yet implemented");
	}
	
	@Override
	public Term getHoleContents() {
		Term holeContents = hole.getHoleContents();
		if (holeContents instanceof Hole)
			return this;
		return holeContents.lift(startIndex, nbBindings);
	}

}
