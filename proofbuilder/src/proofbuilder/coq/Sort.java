package proofbuilder.coq;

public abstract class Sort extends Term {

	public Term lift(int startIndex, int nbBindings) { return this; }

	public Term with(Term term, int index, boolean returnNullOnFailure) { return this; }
	
	public void checkEqualsCore(Term other) {
		if (this != other)
			throw typeMismatchError(other, this);
	}
	
	public void checkIsSort() {}

}
