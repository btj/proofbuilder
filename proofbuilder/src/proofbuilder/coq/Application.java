package proofbuilder.coq;

import java.util.List;
import java.util.Map;

public class Application extends Term {
	
	public final Term function;
	public final Term argument;
	public final Constant uncurriedFunction;
	public final int uncurriedNbArguments;
	public final List<Term> uncurriedArguments;
	
	public Application(Term function, Term argument) {
		this.function = function;
		this.argument = argument;
		
		Constant uncurriedFunction = null;
		int uncurriedNbArguments = 0;
		List<Term> uncurriedArguments = null;
		if (function instanceof Constant constant && constant.nbArguments >= 1) {
			uncurriedFunction = constant;
			uncurriedNbArguments = 1;
		} else if (function instanceof Application application && application.uncurriedFunction != null && application.uncurriedNbArguments < application.uncurriedFunction.nbArguments) {
			uncurriedFunction = application.uncurriedFunction;
			uncurriedNbArguments = application.uncurriedNbArguments + 1;
		}
		if (uncurriedFunction != null && uncurriedNbArguments == uncurriedFunction.nbArguments) {
			Term[] uncurriedArgumentsArray = new Term[uncurriedNbArguments];
			Term term = this;
			for (int i = uncurriedNbArguments - 1; 0 <= i; i--) {
				Application application = (Application)term;
				uncurriedArgumentsArray[i] = application.argument;
				term = application.function;
			}
			uncurriedArguments = List.of(uncurriedArgumentsArray);
		}
		this.uncurriedFunction = uncurriedFunction;
		this.uncurriedNbArguments = uncurriedNbArguments;
		this.uncurriedArguments = uncurriedArguments;
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
		if (uncurriedFunction != null && uncurriedFunction.nbArguments == uncurriedNbArguments)
			return uncurriedFunction.toLaTeX(context, uncurriedArguments, precedence);
		else
			return function.toLaTeX(context, PREC_FUNC) + "(" + argument.toLaTeX(context, 0) + ")";
	}
	
	public Term getHead() { return function.getHead(); }

}
