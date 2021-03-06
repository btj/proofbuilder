package proofbuilder;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public abstract class ProofViewComponent {
	
	final ProofBuilderPanel proofBuilderPanel;
	int width;
	int height;
	int x;
	int y;
	List<ProofViewComponent> childComponents = new ArrayList<>();
	
	ProofViewComponent(ProofBuilderPanel proofBuilderPanel) {
		this.proofBuilderPanel = proofBuilderPanel;
	}
	
	abstract void paintComponent(Graphics g);
	
	void paint(Graphics g) {
		for (ProofViewComponent child : childComponents) {
			Graphics childGraphics = g.create(child.x, child.y, child.width, child.height);
			child.paint(childGraphics);
			childGraphics.dispose();
		}
		paintComponent(g);
	}
	
}
