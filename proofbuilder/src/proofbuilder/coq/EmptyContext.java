package proofbuilder.coq;

public class EmptyContext extends Context {

	EmptyContext() {}
	
	public boolean containsName(String name) { return false; }
	
	public String getVariableName(int index) { throw new RuntimeException("Unbound variable"); }
	
	@Override
	public Context unlift(int startIndex, int nbBindings) {
		return this;
	}
}
