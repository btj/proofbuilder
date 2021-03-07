package proofbuilder;

import java.util.Map;

import proofbuilder.coq.Constant;
import proofbuilder.coq.HolesContext;
import proofbuilder.coq.ProofTree;

public class NamedProofTree {
	
	String name;
	Map<String, Constant> constants;
	HolesContext holesContext;
	Map<String, Constant> pythonConstants;
	ProofTree proofTree;
	
	NamedProofTree(String name, Map<String, Constant> constants, HolesContext holesContext, Map<String, Constant> pythonConstants, ProofTree proofTree) {
		this.name = name;
		this.constants = constants;
		this.holesContext = holesContext;
		this.pythonConstants = pythonConstants;
		this.proofTree = proofTree;
	}
	
	@Override
	public String toString() { return name; }

}
