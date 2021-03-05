package proofbuilder;

import java.awt.Component;
import java.awt.Graphics;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import proofbuilder.coq.ProofTree;
import proofbuilder.coq.Sort;

public class ProofView {
	
	static final int CHILDREN_SPACING = 20;
	static final int RULE_ICON_SPACING = 3;
	static final int RULE_SPACING = 2;
	static final int RULE_HEIGHT = 1;
	static final int LATEX_POINT_SIZE = 20;

	ProofTree proofTree;
	TeXIcon typeIcon;
	TeXIcon ruleIcon;
	ProofView[] children;
	
	int childrenWidth;
	int childrenHeight;
	int ruleWidth;
	int ruleCenter;
	int ruleY;
	int width;
	int height;
	int x;
	int y;
	
	ProofView(ProofTree proofTree) {
		this.proofTree = proofTree;
		this.typeIcon = new TeXFormula(proofTree.getType().toLaTeX(proofTree.context, 0)).createTeXIcon(TeXConstants.STYLE_DISPLAY, LATEX_POINT_SIZE);
		this.ruleIcon = new TeXFormula(proofTree.getRuleAsLaTeX()).createTeXIcon(TeXConstants.STYLE_DISPLAY, LATEX_POINT_SIZE);
		children = proofTree.uncurriedChildren.stream().filter(tree -> tree.actualType.isAProp(tree.context)).map(tree -> new ProofView(tree)).toArray(n -> new ProofView[n]);
	}
	
	void computeLayout() {
		childrenHeight = 0;
		for (ProofView child : children) {
			child.computeLayout();
			childrenWidth += child.width;
			childrenHeight = Math.max(childrenHeight, child.height);
		}
		int childrenHeadsWidth = 0;
		int firstChildLeftOverhang = 0;
		int lastChildRightOverhang = 0;
		if (children.length > 0) {
			childrenWidth += (children.length - 1) * CHILDREN_SPACING;
			ProofView firstChild = children[0];
			ProofView lastChild = children[children.length - 1];
			firstChildLeftOverhang = firstChild.ruleCenter - firstChild.typeIcon.getIconWidth() / 2;
			lastChildRightOverhang = lastChild.width - lastChild.ruleCenter - lastChild.typeIcon.getIconWidth() / 2;
			childrenHeadsWidth = childrenWidth - firstChildLeftOverhang - lastChildRightOverhang;
		}
		ruleWidth = Math.max(childrenHeadsWidth, typeIcon.getIconWidth());
		ruleCenter = Math.max(ruleWidth / 2, childrenHeadsWidth / 2 + firstChildLeftOverhang);
		width = ruleCenter + Math.max(ruleWidth / 2 + RULE_ICON_SPACING + ruleIcon.getIconWidth(), childrenHeadsWidth / 2 + lastChildRightOverhang);
		ruleY = Math.max(childrenHeight + RULE_SPACING + RULE_HEIGHT, ruleIcon.getIconHeight() / 2);
		height = ruleY + RULE_SPACING + typeIcon.getIconHeight();
		
		int childX = ruleCenter - childrenHeadsWidth / 2 - firstChildLeftOverhang;
		for (ProofView child : children) {
			child.x = childX;
			child.y = childrenHeight - child.height;
			childX += child.width + CHILDREN_SPACING;
		}
	}
	
	void paint(Component component, Graphics g) {
		g.drawLine(ruleCenter - ruleWidth / 2, ruleY, ruleCenter + ruleWidth / 2, ruleY);
		typeIcon.paintIcon(component, g, ruleCenter - typeIcon.getIconWidth() / 2, ruleY + RULE_HEIGHT + RULE_SPACING);
		ruleIcon.paintIcon(component, g, ruleCenter + ruleWidth / 2 + RULE_ICON_SPACING, ruleY + RULE_HEIGHT / 2 - ruleIcon.getIconHeight() / 2);
		for (ProofView child : children) {
			Graphics childGraphics = g.create(child.x, child.y, child.width, child.height);
			child.paint(component, childGraphics);
			childGraphics.dispose();
		}
	}
}
