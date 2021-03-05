package proofbuilder;

import static proofbuilder.coq.Term.*;

import java.util.HashMap;
import java.util.Map;

import proofbuilder.coq.Constant;
import proofbuilder.coq.Context;
import proofbuilder.coq.ProofTree;
import proofbuilder.coq.Term;
import proofbuilder.coq.parser.Parser;

public class ProofBuilder {
	
	static Map<String, Constant> constants = new HashMap<String, Constant>();
	
	static Term parse(String text) {
		return Parser.parseTerm(constants, text);
	}
	
	static Term parseType(String text) {
		Term result = parse(text);
		result.checkIsType();
		return result;
	}
	
	static void parameter(String name, String type) {
		constants.put(name, new Constant(name, parseType(type)));
	}
	
	static void rule(String name, String type, String laTeX, int nbArgs) {
		constants.put(name, new Constant(name, parseType(type), laTeX, nbArgs));
	}
	
	public static void main(String[] args) {
		parameter("and", "Prop -> Prop -> Prop");
		rule("and_proj1", "forall (P: Prop) (Q: Prop), and P Q -> P", "\\land_{E^1}", 3);
		rule("and_proj2", "forall (P: Prop) (Q: Prop), and P Q -> Q", "\\land_{E^2}", 3);
		
		parameter("object", "Type");
		parameter("m", "object -> Prop");
		parameter("s", "object -> Prop");
		parameter("S", "object");
		
		Term socratesProof = parse("""
				fun u: and (forall x: object, m x -> s x) (m S) =>
				  and_proj1 (forall x: object, m x -> s x) (m S) u S (and_proj2 (forall x: object, m x -> s x) (m S) u)
				""");
		ProofTree proofTree = socratesProof.checkAgainst(Context.empty, parseType("and (forall x: object, m x -> s x) (m S) -> s S"));
		
		ProofBuilderFrame.showFrame(proofTree);
	}

}
