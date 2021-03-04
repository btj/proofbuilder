package proofbuilder.coq;

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
	
	public Term check(Context context) {
		Term functionType = function.check(context);
		if (functionType instanceof Product prod) {
			argument.checkAgainst(context, prod.domain);
			return prod.range(argument);
		} else
			throw typeError("Trying to apply a term that is not a function");
	}

}
