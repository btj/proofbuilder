package proofbuilder;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import proofbuilder.coq.Application;
import proofbuilder.coq.Context;
import proofbuilder.coq.Hole;
import proofbuilder.coq.Lambda;
import proofbuilder.coq.NonemptyContext;
import proofbuilder.coq.Product;
import proofbuilder.coq.ProofTree;
import proofbuilder.coq.Term;
import proofbuilder.coq.Variable;

public class ProofTreeView extends ProofViewComponent {
	
	static final int CHILDREN_SPACING = 20;
	static final int RULE_ICON_SPACING = 3;
	static final int RULE_SPACING = 2;
	static final int RULE_HEIGHT = 1;
	static final int LATEX_POINT_SIZE = 20;

	ProofTree proofTree;
	TeXIcon typeIcon;
	TeXIcon ruleIcon;
	ProofTreeView[] children;
	
	int childrenWidth;
	int childrenHeight;
	int ruleWidth;
	int ruleCenter;
	int ruleY;
	
	ProofTreeView(ProofBuilderPanel proofBuilderPanel, ProofViewComponent parent, ProofTree proofTree) {
		super(proofBuilderPanel, parent);
		proofTree = proofTree.getHoleContents();
		this.proofTree = proofTree;
		this.typeIcon = new TeXFormula(proofTree.getType().toLaTeX(proofTree.context, 0)).createTeXIcon(TeXConstants.STYLE_DISPLAY, LATEX_POINT_SIZE);
		this.ruleIcon = new TeXFormula(proofTree.getRuleAsLaTeX()).createTeXIcon(TeXConstants.STYLE_DISPLAY, LATEX_POINT_SIZE);
		children = proofTree.uncurriedChildren.stream().filter(tree -> tree != null && tree.actualType.isAProp(tree.context)).map(tree -> new ProofTreeView(proofBuilderPanel, this, tree)).toArray(n -> new ProofTreeView[n]);
		childComponents.addAll(Arrays.asList(children));
	}
	
	void computeLayout() {
		childrenHeight = 0;
		for (ProofTreeView child : children) {
			child.computeLayout();
			childrenWidth += child.width;
			childrenHeight = Math.max(childrenHeight, child.height);
		}
		int childrenHeadsWidth = 0;
		int firstChildLeftOverhang = 0;
		int lastChildRightOverhang = 0;
		if (children.length > 0) {
			childrenWidth += (children.length - 1) * CHILDREN_SPACING;
			ProofTreeView firstChild = children[0];
			ProofTreeView lastChild = children[children.length - 1];
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
		for (ProofTreeView child : children) {
			child.x = childX;
			child.y = childrenHeight - child.height;
			childX += child.width + CHILDREN_SPACING;
		}
		
		childComponents.add(new ProofIcon(proofBuilderPanel, this, typeIcon, ruleCenter - typeIcon.getIconWidth() / 2, ruleY + RULE_HEIGHT + RULE_SPACING));
		childComponents.add(new ProofIcon(proofBuilderPanel, this, ruleIcon, ruleCenter + ruleWidth / 2 + RULE_ICON_SPACING, ruleY + RULE_HEIGHT / 2 - ruleIcon.getIconHeight() / 2) {
			@Override
			void handleMouseEvent(MouseEvent event) {
				JPopupMenu menu = new JPopupMenu();
				boolean show = false;
				
				Term term = proofTree.term.getHoleContents();
				if (term instanceof Hole hole) {
					Term type = hole.getType().getHoleContents();
					
					Context context = proofTree.context;
					int index = 0;
					while (context instanceof NonemptyContext nec) {
						if (nec.type.isAProp(nec.outerContext)) {
							Term liftedType = nec.type.lift(0, index);
							if (liftedType.unifiesWith(proofBuilderPanel.holesContext, type)) {
								JMenuItem item = new JMenuItem(new TeXFormula(nec.name).createTeXIcon(TeXConstants.STYLE_DISPLAY, LATEX_POINT_SIZE));
								int itemIndex = index;
								item.addActionListener((ActionEvent e) -> {
									hole.checkEquals(new Variable(itemIndex));
									proofBuilderPanel.termChanged();
								});
								menu.add(item);
								show = true;
							}
						}
						context = nec.outerContext;
						index++;
					}
					
					if (type != null && type instanceof Product product) {
						if (product.boundVariable == null) {
							JMenuItem item = new JMenuItem(new TeXFormula("\\Rightarrow_I").createTeXIcon(TeXConstants.STYLE_DISPLAY, LATEX_POINT_SIZE));
							item.addActionListener((ActionEvent e) -> {
								hole.checkEquals(new Lambda("u", product.domain, proofBuilderPanel.holesContext.createHole()));
								proofBuilderPanel.termChanged();
							});
							menu.add(item);
							show = true;
						}
					}
					
					{
						JMenuItem item = new JMenuItem(new TeXFormula("\\Rightarrow_E").createTeXIcon(TeXConstants.STYLE_DISPLAY, LATEX_POINT_SIZE));
						item.addActionListener((ActionEvent e) -> {
							Hole antecedentHole = proofBuilderPanel.holesContext.createHole();
							antecedentHole.checkAgainst(proofTree.context, Term.prop);
							Hole functionHole = proofBuilderPanel.holesContext.createHole();
							functionHole.checkAgainst(proofTree.context, new Product(null, antecedentHole, type));
							Hole argumentHole = proofBuilderPanel.holesContext.createHole();
							argumentHole.checkAgainst(proofTree.context, antecedentHole);
							hole.checkEquals(new Application(functionHole, argumentHole));
							proofBuilderPanel.termChanged();
						});
						menu.add(item);
						show = true;
					}
				}
				
				if (show) {
					Point eventCoords = toPanelCoordinates(event.getX(), event.getY());
					menu.show(proofBuilderPanel, eventCoords.x, eventCoords.y);
				}
			}
		});
	}
	
	void paintComponent(Graphics g) {
		g.drawLine(ruleCenter - ruleWidth / 2, ruleY, ruleCenter + ruleWidth / 2, ruleY);
	}
}
