package proofbuilder;

import static proofbuilder.coq.Term.*;

import java.util.HashMap;
import java.util.Map;

import proofbuilder.coq.Constant;
import proofbuilder.coq.Context;
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
	
	public static void main(String[] args) {
		parameter("and", "Prop -> Prop -> Prop");
		parameter("and_cases", "forall (P: Prop) (Q: Prop) (R: Prop), and P Q -> (P -> Q -> R) -> R");
		
		parameter("object", "Type");
		parameter("m", "object -> Prop");
		parameter("s", "object -> Prop");
		parameter("S", "object");
		
		Term socratesProof = parse("""
				fun H: and (forall x: object, m x -> s x) (m S) =>
				and_cases (forall x: object, m x -> s x) (m S) (s S) H (fun (H1: forall x: object, m x -> s x) (H2: m S) =>
				  H1 S H2
				)
				""");
		socratesProof.checkAgainst(Context.empty, parseType("and (forall x: object, m x -> s x) (m S) -> s S"));
	}

}
