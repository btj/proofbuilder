package proofbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import proofbuilder.coq.Constant;
import proofbuilder.coq.Context;
import proofbuilder.coq.HolesContext;
import proofbuilder.coq.Lambda;
import proofbuilder.coq.ProofTree;
import proofbuilder.coq.Term;
import proofbuilder.coq.parser.Parser;

public class NamedProofTreesFactory {
	
	Map<String, Constant> constants = new HashMap<String, Constant>();
	HolesContext holesContext = new HolesContext();
	Map<String, Constant> pythonConstants;
	
	Term parse(String text) {
		return Parser.parseTerm(holesContext, constants, text);
	}
	
	Term parseType(String text) {
		Term result = parse(text);
		result.checkIsType();
		return result;
	}
	
	void parameter(String name, String type) {
		if (constants.containsKey(name))
			throw new RuntimeException("Constant already defined");
		constants.put(name, new Constant(name, parseType(type)));
	}
	
	void parameter(String name, String type, String laTeX) {
		if (constants.containsKey(name))
			throw new RuntimeException("Constant already defined");
		constants.put(name, new Constant(name, parseType(type)) {
			@Override
			public String toLaTeX(Context context, int precedence) {
				return laTeX;
			}
		});
	}
	
	void infixOperator(String name, String type, String operatorLaTeX, int precedence, int leftPrec, int rightPrec) {
		if (constants.containsKey(name))
			throw new RuntimeException("Constant already defined");
		constants.put(name, new Constant(name, parseType(type), operatorLaTeX, 2) {
			@Override
			public String toLaTeX(Context context, List<Term> arguments, int targetPrecedence) {
				return parenthesize(targetPrecedence, precedence, arguments.get(0).toLaTeX(context, leftPrec) + " " + operatorLaTeX + " " + arguments.get(1).toLaTeX(context, rightPrec));
			}
		});
	}
	
	void rule(String name, String type, String laTeX, int nbArgs) {
		if (constants.containsKey(name))
			throw new RuntimeException("Constant already defined");
		constants.put(name, new Constant(name, parseType(type), laTeX, nbArgs));
	}
	
	public List<NamedProofTree> createNamedProofTrees() {
		ArrayList<NamedProofTree> result = new ArrayList<>();
		
		infixOperator("or", "Prop -> Prop -> Prop", "\\lor", Term.PREC_DISJ, Term.PREC_DISJ + 1, Term.PREC_DISJ);
		constants.put("exists", new Constant("exists", parseType("forall (T: Type), (T -> Prop) -> Prop"), "\\exists", 2) {
			@Override
			public String toLaTeX(Context context, List<Term> arguments, int targetPrecedence) {
				String latex;
				Term body = arguments.get(1);
				if (body instanceof Lambda lambda)
					latex = "\\exists " + lambda.boundVariable + ".\\ " + lambda.body.toLaTeX(Context.cons(context, lambda.boundVariable, lambda.domain), 0);
				else
					latex = "\\exists(" + arguments.get(0).toLaTeX(context, 0) + ")(" + body.toLaTeX(context, 0) + ")";
				return parenthesize(targetPrecedence, Term.PREC_QUANT, latex);
			}
			
		});
		constants.put("not", new Constant("not", parseType("Prop -> Prop"), "\\lnot", 1) {
			@Override
			public String toLaTeX(Context context, List<Term> arguments, int targetPrecedence) {
				return parenthesize(targetPrecedence, Term.PREC_NOT, "\\lnot " + arguments.get(0).toLaTeX(context, Term.PREC_NOT));
			}
		});
		parameter("False", "Prop");
		
		rule("not_intro", "forall P: Prop, (P -> False) -> not P", "\\lnot_I", 2);
		rule("not_elim", "forall P: Prop, not P -> P -> False", "\\lnot_E", 3);
		rule("False_elim", "forall P: Prop, False -> P", "\\mathsf{False}_E", 2);
		rule("or_elim", "forall (P Q R: Prop), or P Q -> (P -> R) -> (Q -> R) -> R", "\\lor_E", 6);
		rule("exists_elim", "forall (T: Type) (P: T -> Prop) (Q: Prop), (forall x, P x -> Q) -> exists T P -> Q", "\\exists_E", 5);
		rule("exists_intro", "forall (T: Type) (P: T -> Prop) (x: T), P x -> exists T P", "\\exists_I", 4);
		
		parameter("object", "Type");
		parameter("P", "object -> Prop");
		parameter("c", "object");
		parameter("f", "object -> object");
		
		{
			assert holesContext.getNbUnfilledHoles() == 0;
			holesContext = new HolesContext();
			Term exampleProof = parse("?");
			ProofTree proofTree = exampleProof.checkAgainst(Context.empty, parseType("P c -> exists object (fun x: object => P x)"));
			result.add(new NamedProofTree("Voorbeeld 0", constants, holesContext, Map.of(), proofTree));
			holesContext = new HolesContext();
		}
		
		{
			assert holesContext.getNbUnfilledHoles() == 0;
			holesContext = new HolesContext();
			Term exampleProof = parse("?");
			ProofTree proofTree = exampleProof.checkAgainst(Context.empty, parseType("(forall x: object, P x) -> not (exists object (fun x: object => or (not (P x)) (not (P (f x)))))"));
			result.add(new NamedProofTree("Voorbeeld 1", constants, holesContext, Map.of(), proofTree));
			holesContext = new HolesContext();
		}
		
		infixOperator("and", "Prop -> Prop -> Prop", "\\land", Term.PREC_CONJ, Term.PREC_CONJ + 1, Term.PREC_CONJ);
		rule("and_proj1", "forall (P: Prop) (Q: Prop), and P Q -> P", "\\land_{E^1}", 3);
		rule("and_proj2", "forall (P: Prop) (Q: Prop), and P Q -> Q", "\\land_{E^2}", 3);
		
		parameter("mens", "object -> Prop");
		parameter("sterfelijk", "object -> Prop");
		parameter("Socrates", "object");
		
		{
			assert holesContext.getNbUnfilledHoles() == 0;
			holesContext = new HolesContext();
			Term socratesProof = parse("?");
			ProofTree proofTree = socratesProof.checkAgainst(Context.empty, parseType("and (forall x: object, mens x -> sterfelijk x) (mens Socrates) -> sterfelijk Socrates"));
			result.add(new NamedProofTree("Socrates", constants, holesContext, Map.of(), proofTree));
			holesContext = new HolesContext();
		}
		
		{
			assert holesContext.getNbUnfilledHoles() == 0;
			holesContext = new HolesContext();
			Term socratesProof = parse("""
				fun u: and (forall x: object, mens x -> sterfelijk x) (mens Socrates) =>
				  and_proj1 (forall x: object, mens x -> sterfelijk x) (mens Socrates) u Socrates (and_proj2 (forall x: object, mens x -> sterfelijk x) (mens Socrates) u)
				""");
			ProofTree proofTree = socratesProof.checkAgainst(Context.empty, parseType("and (forall x: object, mens x -> sterfelijk x) (mens Socrates) -> sterfelijk Socrates"));
			result.add(new NamedProofTree("Socrates (bewijs)", constants, holesContext, Map.of(), proofTree));
			holesContext = new HolesContext();
		}
		
		parameter("aexp", "Type");
		parameter("i", "aexp", "\\texttt{i}");
		parameter("0", "aexp", "\\texttt{0}");
		parameter("bexp", "Type");
		parameter("btrue", "bexp", "\\texttt{True}");
		parameter("bfalse", "bexp", "\\texttt{False}");
		infixOperator("beq", "aexp -> aexp -> bexp", "\\;\\texttt{==}\\;", Term.PREC_EXP_EQ, Term.PREC_EXP_EQ + 1, Term.PREC_EXP_EQ + 1);
		parameter("stmt", "Type");
		infixOperator("gets", "aexp -> aexp -> stmt", "\\;\\texttt{=}\\;", Term.PREC_STMT, Term.PREC_STMT + 1, Term.PREC_STMT + 1);
		constants.put("correct", new Constant("correct", parseType("bexp -> stmt -> bexp -> Prop"), "\\mathsf{correct}", 3) {
			@Override
			public String toLaTeX(Context context, List<Term> arguments, int precedence) {
				return """
						\\begin{array}{@{} l @{}}
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
		constants.put("bsubst", new Constant("bsubst", parseType("bexp -> aexp -> aexp -> bexp"), "\\mathsf{bsubst}", 3) {
			@Override
			public String toLaTeX(Context context, List<Term> arguments, int precedence) {
				return
						arguments.get(0).toLaTeX(context, PREC_POSTFIX) + "[" +
						arguments.get(1).toLaTeX(context, 0) + "/" +
						arguments.get(2).toLaTeX(context, 0) + "]";
			}
		});
		infixOperator("bimplies", "bexp -> bexp -> Prop", "\\Rightarrow_\\texttt{exp}", Term.PREC_BIMPLIES, Term.PREC_BIMPLIES + 1, Term.PREC_BIMPLIES);
		rule("Cassign", "forall (Q: bexp) (E: aexp) (x: aexp), correct (bsubst Q E x) (gets x E) Q", "\\texttt{=}", 3);

		{
			assert holesContext.getNbUnfilledHoles() == 0;
			holesContext = new HolesContext();
			Term minimalCorrectProof = parse("?");
			Term minimalCorrectGoal = parse("correct btrue (gets i 0) (beq i 0)");
			ProofTree proofTree = minimalCorrectProof.checkAgainst(Context.empty, minimalCorrectGoal);
			result.add(new NamedProofTree("i == 0", constants, holesContext, Map.of(), proofTree));
			holesContext = new HolesContext();
		}
		
		constants.put("seq", new Constant("seq", parseType("stmt -> stmt -> stmt"), "\\mathsf{seq}", 2) {
			@Override
			public String toLaTeX(Context context, List<Term> arguments, int precedence) {
				return """
						\\begin{array}{@{} l @{}}
						%s\\\\
						%s
						\\end{array}
						""".formatted(
								arguments.get(0).toLaTeX(context, PREC_STMT),
								arguments.get(1).toLaTeX(context, PREC_STMT));
			}
		});
		
		rule("bimplies_refl", "forall P, bimplies P P", "\\Rightarrow_\\texttt{exp}_\\mathsf{id}", 1);
		parameter("som", "aexp", "\\texttt{som}");
		infixOperator("band", "bexp -> bexp -> bexp", "\\;\\texttt{and}\\;", Term.PREC_EXP_CONJ, Term.PREC_EXP_CONJ + 1, Term.PREC_EXP_CONJ);
		rule("Cseq", "forall (P Q R: bexp) (p1 p2: stmt), correct P p1 R -> correct R p2 Q -> correct P (seq p1 p2) Q", "\\textrm{\\textsc{Seq}}", 7);
		{
			assert holesContext.getNbUnfilledHoles() == 0;
			holesContext = new HolesContext();
			Term seqProof = parse("?");
			Term seqGoal = parse("correct btrue (seq (gets i 0) (gets som 0)) (band (beq i 0) (beq som 0))");
			ProofTree proofTree = seqProof.checkAgainst(Context.empty, seqGoal);
			result.add(new NamedProofTree("i == 0 and som == 0", constants, holesContext, Map.of(), proofTree));
			holesContext = new HolesContext();
		}
		
		constants.put("while", new Constant("while", parseType("bexp -> stmt -> stmt"), "\\texttt{while}", 2) {
			@Override
			public String toLaTeX(Context context, List<Term> arguments, int precedence) {
				return """
						\\begin{array}{@{} l @{}}
						\\texttt{while}\\ %s:\\\\
						\\quad %s
						\\end{array}
						""".formatted(
								arguments.get(0).toLaTeX(context, 0),
								arguments.get(1).toLaTeX(context, 0));
			}
		});
		parameter("n", "aexp", "\\texttt{n}");
		infixOperator("ble", "aexp -> aexp -> bexp", "\\;\\texttt{{<}\\!\\!{=}}\\;", Term.PREC_EXP_EQ, Term.PREC_EXP_EQ + 1, Term.PREC_EXP_EQ + 1);
		infixOperator("blt", "aexp -> aexp -> bexp", "\\;\\texttt{{<}}\\;", Term.PREC_EXP_EQ, Term.PREC_EXP_EQ + 1, Term.PREC_EXP_EQ + 1);
		infixOperator("bneq", "aexp -> aexp -> bexp", "\\;\\texttt{{!}{=}}\\;", Term.PREC_EXP_EQ, Term.PREC_EXP_EQ + 1, Term.PREC_EXP_EQ + 1);
		infixOperator("aplus", "aexp -> aexp -> aexp", "\\;\\texttt{+}\\;", Term.PREC_EXP_PLUS, Term.PREC_EXP_PLUS, Term.PREC_EXP_PLUS + 1);
		parameter("1", "aexp", "\\texttt{1}");
		parameter("lexp", "Type");
		parameter("asum", "lexp -> aexp", "\\texttt{sum}");
		parameter("range", "aexp -> lexp", "\\texttt{range}");
		constants.put("bnot", new Constant("bnot", parseType("bexp -> bexp"), "\\mathsf{bnot}", 1) {
			@Override
			public String toLaTeX(Context context, List<Term> arguments, int precedence) {
				return parenthesize(precedence, PREC_EXP_NOT, "\\texttt{not}\\;" + arguments.get(0).toLaTeX(context, PREC_EXP_NOT));
			}
		});
		rule("Cconseq", "forall P1 P2 Q1 Q2 p, (bimplies P1 P2) -> correct P2 p Q1 -> (bimplies Q1 Q2) -> correct P1 p Q2", "\\textrm{\\textsc{Conseq}}", 8);
		rule("Cwhile", "forall I E p, correct (band I E) p I -> correct I (while E p) (band I (bnot E))", "\\texttt{while}", 4);
		
		{
			assert holesContext.getNbUnfilledHoles() == 0;
			holesContext = new HolesContext();
			Term whileProof = parse("?");
			Term whileGoal = parse("""
					correct
						(band (ble 0 n) (band (beq i 0) (beq som 0)))
						(while (bneq i n) (seq (gets som (aplus som i)) (gets i (aplus i 1))))
						(beq som (asum (range n)))
					""");
			ProofTree proofTree = whileProof.checkAgainst(Context.empty, whileGoal);
			
			pythonConstants = Map.ofEntries(
					Map.entry("0", constants.get("0")),
					Map.entry("i", constants.get("i")),
					Map.entry("n", constants.get("n")),
					Map.entry("som", constants.get("som")),
					Map.entry("sum", constants.get("asum")),
					Map.entry("range", constants.get("range")),
					Map.entry("#EQ", constants.get("beq")),
					Map.entry("#LE", constants.get("ble")),
					Map.entry("#AND", constants.get("band"))
			);
			result.add(new NamedProofTree("som == sum(range(n))", constants, holesContext, pythonConstants, proofTree));
			holesContext = new HolesContext();
		}
		
		constants.put("if", new Constant("if", parseType("bexp -> stmt -> stmt -> stmt"), "\\mathsf{if}", 3) {
			@Override
			public String toLaTeX(Context context, List<Term> arguments, int precedence) {
				return """
						\\begin{array}{l}
						\\texttt{if}\\ %s\\texttt{:}\\\\
						\\quad %s\\\\
						\\texttt{else{:}}\\\\
						\\quad %s
						\\end{array}
						""".formatted(
								arguments.get(0).toLaTeX(context, 0),
								arguments.get(1).toLaTeX(context, 0),
								arguments.get(2).toLaTeX(context, 0));
			}
		});
		parameter("min3", "aexp", "\\texttt{min}");
		parameter("x", "aexp", "\\texttt{x}");
		parameter("y", "aexp", "\\texttt{y}");
		parameter("z", "aexp", "\\texttt{z}");
		constants.put("amin3", new Constant("amin3", parseType("aexp -> aexp -> aexp -> aexp"), "\\mathsf{min}", 3));
		rule("Cif", "forall P E p1 p2 Q, correct (band P E) p1 Q -> correct (band P (bnot E)) p2 Q -> correct P (if E p1 p2) Q", "\\texttt{if}", 7);
		
		{
			assert holesContext.getNbUnfilledHoles() == 0;
			holesContext = new HolesContext();
			Term min3Goal = parse("""
					correct
						btrue
						(if (blt x y)
						   (if (blt x z)
						      (gets min3 x)
						      (gets min3 z))
						   (if (blt y z)
						      (gets min3 y)
						      (gets min3 z)))
					    (beq min3 (amin3 x y z))
					""");
			Term min3Proof = parse("?");
			ProofTree proofTree = min3Proof.checkAgainst(Context.empty, min3Goal);
			result.add(new NamedProofTree("min3", constants, holesContext, pythonConstants, proofTree));
			holesContext = new HolesContext();
		}
		
		return List.copyOf(result);
	}

}
