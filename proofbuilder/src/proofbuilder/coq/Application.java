package proofbuilder.coq;

import java.util.List;
import java.util.Map;

public class Application extends Term {
	
	public final Term function;
	public final Term argument;
	
	public Application(Term function, Term argument) {
		this.function = function;
		this.argument = argument;
	}
	
	public boolean equals(Term other) {
		if (other instanceof Application app)
			return function.equals(app.function) && argument.equals(app.argument); 
		else
			return false;
	}
	
	public Term lift(int startIndex, int nbBindings) {
		Term newFunction = function.lift(startIndex, nbBindings);
		Term newArgument = argument.lift(startIndex, nbBindings);
		if (newFunction == function && newArgument == argument)
			return this;
		return new Application(newFunction, newArgument);
	}
	
	public Term with(Term term, int index) {
		Term newFunction = function.with(term, index);
		Term newArgument = argument.with(term, index);
		if (newFunction == function && newArgument == argument)
			return this;
		return new Application(newFunction, newArgument);
	}
	
	public ProofTree check(Context context) {
		ProofTree functionTree = function.check(context);
		if (functionTree.actualType instanceof Product prod) {
			ProofTree argumentTree = argument.checkAgainst(context, prod.domain);
			return new ProofTree(context, this, prod.range(argument), null, List.of(functionTree, argumentTree));
		} else
			throw typeError("Trying to apply a term that is not a function");
	}
	
	@Override
	public String toLaTeX(Context context, int precedence) {
		return function.toLaTeX(context, PREC_FUNC) + "(" + argument.toLaTeX(context, 0) + ")";
	}
	
	public Term getHead() { return function.getHead(); }

}
