package proofbuilder.coq;

public abstract class Context {

	public static EmptyContext empty = new EmptyContext();
	public static Context cons(Context context, String boundVariable, Term domain) {
		return new NonemptyContext(context, boundVariable, domain);
	}
	
	public abstract boolean containsName(String name);

	public abstract String getVariableName(int deBruijnIndex);

}
