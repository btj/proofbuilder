package proofbuilder.coq;

public abstract class Sort extends Term {

	public Term lift(int startIndex, int nbBindings) { return this; }

	public Term with(Term term, int index) { return this; }
	
	public void checkEquals(Term other) {
		if (this != other)
			throw typeMismatchError(other, this);
	}

}
