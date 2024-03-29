package proofbuilder.coq;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Term {
	
	public static boolean showDomains = false;
	
	public Constant getUncurriedFunction() { return null; }
	public int getUncurriedNbArguments() { throw new IllegalStateException(); }
	public List<Term> getUncurriedArguments() { throw new IllegalStateException(); }
	
	public static final PropSort prop = new PropSort();
	private static ArrayList<TypeSort> typeSorts = new ArrayList<>();
	public static TypeSort type(int level) {
		while (typeSorts.size() <= level)
			typeSorts.add(new TypeSort(typeSorts.size()));
		return typeSorts.get(level);
	}
	public static final TypeSort typeSort = type(0);
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
	
	public abstract ProofTree check(Context context);
	
	public boolean isAProp(Context context) {
		return check(context).actualType instanceof PropSort;
	}
	
	public void checkIsSort() {
		throw typeError("Sort expected");
	}
	
	public void checkIsType() {
		check(Context.empty).actualType.checkIsSort();
		
	}
	
	public RuntimeException typeMismatchError(Term expectedType, Term actualType) {
		return typeError("Expected type: " + expectedType + "; actual type: " + actualType);
	}
	
	public ProofTree checkAgainst(Context context, Term expectedType) {
		ProofTree proofTree = check(context);
		proofTree.actualType.checkEquals(expectedType);
		return proofTree.withExpectedType(expectedType);
	}
	
	public abstract void checkEqualsCore(Term other);
	
	public final void checkEquals(Term other) {
		Term thisTerm = this.getHoleContents();
		Term otherTerm = other.getHoleContents();
		if (other instanceof AbstractHole)
			other.checkEqualsCore(this);
		else
			checkEqualsCore(other);
	}
	
	/**
	 * @param startIndex
	 * @param nbBindings Can be negative to unlift terms to be unified with lifted holes
	 */
	public abstract Term lift(int startIndex, int nbBindings);
	
	public abstract Term with(Term term, int index, boolean returnNullOnFailure);
	public final Term with(Term term, int index) { return with(term, index, false); } 
	
	public static final int PREC_POSTFIX = 2000;
	public static final int PREC_FUNC = 1000;
	public static final int PREC_EXP_POW = 598;
	public static final int PREC_EXP_TIMES = 596;
	public static final int PREC_EXP_PLUS = 594;
	public static final int PREC_EXP_EQ = 592;
	public static final int PREC_EXP_NOT = 591;
	public static final int PREC_EXP_CONJ = 590;
	public static final int PREC_EXP_DISJ = 588;
	public static final int PREC_EXP_IMPL = 580;
	public static final int PREC_STMT = 550;
	
	public static final int PREC_BIMPLIES = 400;
	
	public static final int PREC_POW = 98;
	public static final int PREC_TIMES = 96;
	public static final int PREC_PLUS = 94;
	public static final int PREC_EQ = 92;
	public static final int PREC_NOT = 91;
	public static final int PREC_CONJ = 90;
	public static final int PREC_DISJ = 88;
	public static final int PREC_IMPL = 80;
	public static final int PREC_QUANT = 50;
	
	public static String parenthesize(int targetPrecedence, int actualPrecedence, String text) {
		if (targetPrecedence > actualPrecedence)
			return "(" + text + ")";
		else
			return text;
	}
	
	public abstract String toLaTeX(Context context, int precedence);
	
	public Term getHead() { return this; }
	
	public Term getHoleContents() { return this; }
	public boolean unifiesWith(HolesContext holesContext, Term other) {
		holesContext.push();
		try {
			this.checkEquals(other);
			return true;
		} catch (TypeException e) {
		} finally {
			holesContext.pop();
		}
		return false;
	}
	
	public Term applyTo(Term argument) {
		return new Application(this, argument);
	}
	
	public Term reduce() {
		return this;
	}
	
}
