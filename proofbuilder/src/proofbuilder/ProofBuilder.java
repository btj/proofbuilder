package proofbuilder;

import static proofbuilder.coq.Term.*;

import proofbuilder.coq.Constant;
import proofbuilder.coq.Term;

public class ProofBuilder {
	
	static Constant and = new Constant("and", impl(prop, impl(prop, prop)));
	
	public static Term and(Term conj1, Term conj2) {
		return app(and, conj1, conj2);
	}
	
	public static void main(String[] args) {
		Term object = new Constant("object", type);
		Term m = new Constant("m", impl(object, prop));
		Term s = new Constant("s", impl(object, prop));
		Term S = new Constant("S", object);
		Term and_cases = new Constant("and_cases",
				prod("P", prop,
						prod("Q", prop,
								prod("R", prop,
										impl(and(var("P"), var("Q")),
												impl(impl(var("P"), impl(var("Q"), var("R"))),
														var("R")))))));
		
		Term premiseType = and(
				prod("x", object, impl(app(m, var("x")), app(s, var("x")))),
				app(m, S));
		Term socratesProof = abs("H", premiseType,
				app(and_cases, var("H"),
						abs("H1", prod("x", object, impl(app(m, var("x")), app(s, var("x")))),
							abs("H2", app(m, S),
									app(var("H1"), S, var("H2"))))));
						
	}

}
