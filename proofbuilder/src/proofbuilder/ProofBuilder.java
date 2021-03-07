package proofbuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import proofbuilder.coq.Constant;
import proofbuilder.coq.Context;
import proofbuilder.coq.HolesContext;
import proofbuilder.coq.ProofTree;
import proofbuilder.coq.Term;
import proofbuilder.coq.parser.Parser;

public class ProofBuilder {
	
	static Map<String, Constant> constants = new HashMap<String, Constant>();
	static HolesContext holesContext = new HolesContext();
	
	static Term parse(String text) {
		return Parser.parseTerm(holesContext, constants, text);
	}
	
	static Term parseType(String text) {
		Term result = parse(text);
		result.checkIsType();
		return result;
	}
	
	static void parameter(String name, String type) {
		constants.put(name, new Constant(name, parseType(type)));
	}
	
	static void parameter(String name, String type, String laTeX) {
		constants.put(name, new Constant(name, parseType(type)) {
			@Override
			public String toLaTeX(Context context, int precedence) {
				return laTeX;
			}
		});
	}
	
	static void infixOperator(String name, String type, String operatorLaTeX, int precedence, int leftPrec, int rightPrec) {
		constants.put(name, new Constant(name, parseType(type), operatorLaTeX, 2) {
			@Override
			public String toLaTeX(Context context, List<Term> arguments, int targetPrecedence) {
				return parenthesize(targetPrecedence, precedence, arguments.get(0).toLaTeX(context, leftPrec) + " " + operatorLaTeX + " " + arguments.get(1).toLaTeX(context, rightPrec));
			}
		});
	}
	
	static void rule(String name, String type, String laTeX, int nbArgs) {
		constants.put(name, new Constant(name, parseType(type), laTeX, nbArgs));
	}
	
	public static void main(String[] args) {
		infixOperator("and", "Prop -> Prop -> Prop", "\\land", Term.PREC_CONJ, Term.PREC_CONJ + 1, Term.PREC_CONJ);
		rule("and_proj1", "forall (P: Prop) (Q: Prop), and P Q -> P", "\\land_{E^1}", 3);
		rule("and_proj2", "forall (P: Prop) (Q: Prop), and P Q -> Q", "\\land_{E^2}", 3);
		
//		parameter("object", "Type");
//		parameter("mens", "object -> Prop");
//		parameter("sterfelijk", "object -> Prop");
//		parameter("Socrates", "object");
		
		parameter("aexp", "Type");
		parameter("i", "aexp", "\\texttt{i}");
		parameter("0", "aexp", "\\texttt{0}");
		parameter("bexp", "Type");
		infixOperator("beq", "aexp -> aexp -> bexp", "\\;\\texttt{==}\\;", Term.PREC_EQ, Term.PREC_EQ + 1, Term.PREC_EQ + 1);
		parameter("stmt", "Type");
		infixOperator("gets", "aexp -> aexp -> stmt", "\\;\\texttt{=}\\;", Term.PREC_ASSIGN, Term.PREC_ASSIGN + 1, Term.PREC_ASSIGN + 1);
		constants.put("correct", new Constant("correct", parseType("bexp -> stmt -> bexp -> Prop"), "\\mathsf{correct}", 3) {
			@Override
			public String toLaTeX(Context context, List<Term> arguments, int precedence) {
				return """
						\\begin{array}{l}
						\\textcolor{blue}{\\{%s\\}}\\\\
						%s\\\\
						\\textcolor{blue}{\\{%s\\}}
						\\end{array}
						""".formatted(
								arguments.get(0).toLaTeX(context, 0),
								arguments.get(1).toLaTeX(context, 0),
								arguments.get(2).toLaTeX(context, 0));
			}
		});

		Term minimalCorrectGoal = parse("correct (beq 0 0) (gets i 0) (beq i 0)");
		ProofTree proofTree = parse("?").checkAgainst(Context.empty, minimalCorrectGoal);
		
//		Term socratesProof = parse("""
//				fun u: and (forall x: object, m x -> s x) (m S) =>
//				  and_proj1 (forall x: object, m x -> s x) (m S) u S (and_proj2 (forall x: object, m x -> s x) (m S) u)
//				""");
//		Term socratesProof = parse("?");
//		ProofTree proofTree = socratesProof.checkAgainst(Context.empty, parseType("and (forall x: object, mens x -> sterfelijk x) (mens Socrates) -> sterfelijk Socrates"));
		
		ProofBuilderFrame.showFrame(constants, holesContext, proofTree);
	}

}
