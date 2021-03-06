package proofbuilder.coq;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HolesContext {

	private ArrayList<Hole> holes = new ArrayList<>();
	
	private class UndoStackEntry {
		final int nbHoles;
		final int nbUndoActions;
		
		UndoStackEntry(int nbHoles, int nbUndoActions) {
			this.nbHoles = nbHoles;
			this.nbUndoActions = nbUndoActions;
		}
	}
	
	private ArrayList<UndoStackEntry> undoStack = new ArrayList<>();
	private ArrayList<Runnable> undoActions = new ArrayList<>();
	
	public Hole createHole() {
		Hole result = new Hole(this, 1 + holes.size());
		holes.add(result);
		return result;
	}
	
	public void addUndoAction(Runnable runnable) {
		undoActions.add(runnable);
	}
	
	public void push() {
		undoStack.add(new UndoStackEntry(holes.size(), undoActions.size()));
	}
	
	public void pop() {
		UndoStackEntry undoStackEntry = undoStack.remove(undoStack.size() - 1);
		while (holes.size() > undoStackEntry.nbHoles)
			holes.remove(holes.size() - 1);
		while (undoActions.size() > undoStackEntry.nbUndoActions)
			undoActions.remove(undoActions.size() - 1).run();
	}
	
}