package proofbuilder;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class ProofViewComponent {
	
	final ProofBuilderPanel proofBuilderPanel;
	ProofViewComponent parent;
	int width;
	int height;
	int x;
	int y;
	List<ProofViewComponent> childComponents = new ArrayList<>();
	
	ProofViewComponent(ProofBuilderPanel proofBuilderPanel, ProofViewComponent parent) {
		this.proofBuilderPanel = proofBuilderPanel;
		this.parent = parent;
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
	
	boolean contains(Point point) {
		return
				x <= point.x && point.x - x <= width &&
				y <= point.y && point.y - y <= height;
	}
	
	boolean handleParentMouseEvent(MouseEvent event) {
		if (contains(event.getPoint())) {
			event.translatePoint(-x, -y);
			handleMouseEvent(event);
			return true;
		} else
			return false;
	}
	
	void handleMouseEvent(MouseEvent event) {
		for (ProofViewComponent child : childComponents) {
			if (child.handleParentMouseEvent(event))
				return;
		}
	}
	
	Point toPanelCoordinates(int x, int y) {
		if (parent == null)
			return new Point(this.x + x, this.y + y);
		else
			return parent.toPanelCoordinates(this.x + x, this.y + y);
	}
	
}
