package proofbuilder.coq;

import java.util.ArrayList;

public abstract class Term {
	
	public static final PropSort prop = new PropSort();
	private static ArrayList<TypeSort> typeSorts = new ArrayList<>();
	public static TypeSort type(int level) {
		while (typeSorts.size() <= level)
			typeSorts.add(new TypeSort(typeSorts.size()));
		return typeSorts.get(level);
	}
	public static final TypeSort type = type(0);
	public static Term var(String name) { return new Variable(name); }
	public static Term abs(String x, Term domain, Term body) { return new Lambda(x, domain, body); }
	public static Term prod(String x, Term domain, Term range) { return new Product(x, domain, range); }
	public static Term impl(Term domain, Term range) { return new Product(null, domain, range); }
	public static Term app(Term f, Term arg) { return new Application(f, arg); }
	public static Term app(Term f, Term... args) {
		for (Term arg : args)
			f = app(f, arg);
		return f;
	}

}
