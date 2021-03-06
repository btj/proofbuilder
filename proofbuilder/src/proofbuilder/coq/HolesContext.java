package proofbuilder.coq;

import java.util.HashSet;
import java.util.Set;

public class HolesContext {

	private Set<Hole> holes = new HashSet<>();
	
	public Hole createHole() {
		Hole result = new Hole(this, 1 + holes.size());
		holes.add(result);
		return result;
	}
	
}
