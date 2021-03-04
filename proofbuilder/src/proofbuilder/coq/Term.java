package proofbuilder.coq;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Term {
	
	public static final PropSort prop = new PropSort();
	private static ArrayList<TypeSort> typeSorts = new ArrayList<>();
	public static TypeSort type(int level) {
		while (typeSorts.size() <= level)
			typeSorts.add(new TypeSort(typeSorts.size()));
		return typeSorts.get(level);
	}
	public static final TypeSort type = type(0);
	public static Term var(int index) { return new Variable(index); }
	public static Term abs(String x, Term domain, Term body) { return new Lambda(x, domain, body); }
	public static Term prod(String x, Term domain, Term range) { return new Product(x, domain, range); }
	public static Term impl(Term domain, Term range) { return new Product(null, domain, range); }
	public static Term app(Term f, Term arg) { return new Application(f, arg); }
	public static Term app(Term f, Term... args) {
		for (Term arg : args)
			f = app(f, arg);
		return f;
	}
	
	public RuntimeException typeError(String message) {
		return new TypeException(message);
	}
	
	public <K, V> Map<K, V> mapCons(K key, V value, Map<K, V> map) {
		return new AbstractMap<K, V>() {
			@Override
			public V get(Object key1) {
				if (key1.equals(key))
					return value;
				return map.get(key1);
			}

			@Override
			public Set<Entry<K, V>> entrySet() {
				var mapSet = map.entrySet();
				var result = new HashSet<>(mapSet);
				result.add(new SimpleEntry<>(key, value));
				return result;
			}
		};
	}
	
	public abstract Term check(Context context);
	
	public void checkIsType() {
		Term type = check(Context.empty);
		if (!(type instanceof Sort))
			throw typeError("Type expected");
	}
	
	public void checkAgainst(Context context, Term expectedType) {
		Term actualType = check(context);
		if (!expectedType.equals(actualType))
			throw typeError("Expected type: " + expectedType + "; actual type: " + actualType);
	}
	
	public abstract boolean equals(Term other);
	
	public abstract Term lift(int startIndex, int nbBindings);
	
	public abstract Term with(Term term, int index);

}
