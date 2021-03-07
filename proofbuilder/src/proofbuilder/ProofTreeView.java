package proofbuilder;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import proofbuilder.coq.Application;
import proofbuilder.coq.Constant;
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
		this.typeIcon = new TeXFormula(proofTree.getType().toLaTeX(proofTree.context, 0)).createTeXIcon(TeXConstants.STYLE_DISPLAY, proofBuilderPanel.latexPointSize);
		this.ruleIcon = new TeXFormula(proofTree.getRuleAsLaTeX()).createTeXIcon(TeXConstants.STYLE_DISPLAY, proofBuilderPanel.latexPointSize);
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
					
					{
						Context context = proofTree.context;
						int index = 0;
						while (context instanceof NonemptyContext nec) {
							if (nec.type.isAProp(nec.outerContext)) {
								Term liftedType = nec.type.lift(0, index);
								if (liftedType.unifiesWith(proofBuilderPanel.holesContext, type)) {
									JMenuItem item = new JMenuItem(new TeXFormula(nec.name).createTeXIcon(TeXConstants.STYLE_DISPLAY, proofBuilderPanel.latexPointSize));
									int itemIndex = index;
									item.addActionListener((ActionEvent e) -> {
										proofBuilderPanel.changeTerm(() -> {
											hole.checkEquals(new Variable(itemIndex));
										});
									});
									menu.add(item);
									show = true;
								}
							}
							context = nec.outerContext;
							index++;
						}
					}
					
					{
						for (Constant constant : proofBuilderPanel.constants.values()) {
							boolean use = false;
							{
								Term constantType = constant.type;
								proofBuilderPanel.holesContext.push();
								for (int argCount = 0; argCount < constant.nbArguments; argCount++) {
									Product productType = (Product)constantType;
									if (productType.boundVariable != null) {
										Hole placeholderArgument = proofBuilderPanel.holesContext.createHole();
										placeholderArgument.checkAgainst(Context.empty, productType.domain);
										constantType = productType.range.getHoleContents().with(placeholderArgument, 0);
									} else
										constantType = productType.range.getHoleContents();
								}
								if (constantType.unifiesWith(proofBuilderPanel.holesContext, type))
									use = true;
								proofBuilderPanel.holesContext.pop();
							}
							if (use) {
								JMenuItem item = new JMenuItem(new TeXFormula(constant.getRuleAsLaTeX(Context.empty)).createTeXIcon(TeXConstants.STYLE_DISPLAY, proofBuilderPanel.latexPointSize));
								item.addActionListener((ActionEvent e) -> {
									proofBuilderPanel.changeTerm(() -> {
										Term constantApplication = constant;
										
										Term constantType = constant.type;
										for (int i = 0; i < constant.nbArguments; i++) {
											Product productType = (Product)constantType;
											Term argument = proofBuilderPanel.holesContext.createHole();
											constantApplication = new Application(constantApplication, argument);
											if (productType.boundVariable != null) {
												constantType = productType.range.getHoleContents().with(argument, 0);
											} else
												constantType = productType.range.getHoleContents();
										}
										
										hole.checkEquals(constantApplication);
									});
								});
								menu.add(item);
								show = true;
							}
						}
					}
					
					if (type != null && type instanceof Product product) {
						if (product.boundVariable == null) {
							{
								JMenuItem item = new JMenuItem(new TeXFormula("\\Rightarrow_I").createTeXIcon(TeXConstants.STYLE_DISPLAY, proofBuilderPanel.latexPointSize));
								item.addActionListener((ActionEvent e) -> {
									proofBuilderPanel.changeTerm(() -> {
										hole.checkEquals(new Lambda("u", product.domain, proofBuilderPanel.holesContext.createHole()));
									});
								});
								menu.add(item);
								show = true;
							}
							
							// Forward reasoning
							if (product.domain.getHoleContents() instanceof Product domainProduct) {
								String ruleName = domainProduct.boundVariable == null ? "\\Rightarrow_E" : "\\forall_E";
								JMenuItem item = new JMenuItem(new TeXFormula("\\textrm{forward with }" + ruleName).createTeXIcon(TeXConstants.STYLE_DISPLAY, proofBuilderPanel.latexPointSize));
								item.addActionListener((ActionEvent e) -> {
									proofBuilderPanel.changeTerm(() -> {
										Hole functionHole = proofBuilderPanel.holesContext.createHole();
										//functionHole.checkAgainst(Context.cons(proofTree.context, "u", product.domain), new Product(null, ))
										Hole argumentHole = proofBuilderPanel.holesContext.createHole();
										hole.checkEquals(new Lambda("u", product.domain, new Application(functionHole, new Application(new Variable(0), argumentHole))));
									});
								});
								menu.add(item);
								show = true;
							}
							
							for (Constant constant : proofBuilderPanel.constants.values()) {
								ArrayList<Integer> matchingParameters = new ArrayList<>();
								boolean use;
								{
									int parameterIndex = 0;
									Term constantType = constant.type;
									proofBuilderPanel.holesContext.push();
									while (constantType instanceof Product productType) {
										if (productType.domain.unifiesWith(proofBuilderPanel.holesContext, product.domain)) {
											matchingParameters.add(parameterIndex);
										}
										if (productType.boundVariable != null) {
											Hole placeholderArgument = proofBuilderPanel.holesContext.createHole();
											placeholderArgument.checkAgainst(Context.empty, productType.domain);
											constantType = productType.range.getHoleContents().with(placeholderArgument, 0);
										} else
											constantType = productType.range.getHoleContents();
										parameterIndex++;
									}
									use = constantType.isAProp(Context.empty);
									proofBuilderPanel.holesContext.pop();
								}
								if (use && matchingParameters.size() > 0) {
									for (int matchingParameter : matchingParameters) {
										JMenuItem item = new JMenuItem(new TeXFormula("\\textrm{forward with }" + constant.getRuleAsLaTeX(Context.empty)).createTeXIcon(TeXConstants.STYLE_DISPLAY, proofBuilderPanel.latexPointSize));
										item.addActionListener((ActionEvent e) -> {
											proofBuilderPanel.changeTerm(() -> {
												Term constantApplication = constant;
												
												int parameterIndex = 0;
												Term constantType = constant.type;
												while (constantType instanceof Product productType) {
													Term argument;
													if (parameterIndex == matchingParameter) {
														argument = new Variable(0);
													} else {
														argument = proofBuilderPanel.holesContext.createHole();
													}
													constantApplication = new Application(constantApplication, argument);
													if (productType.boundVariable != null) {
														constantType = productType.range.getHoleContents().with(argument, 0);
													} else
														constantType = productType.range.getHoleContents();
													parameterIndex++;
												}
												
												Term functionHole = proofBuilderPanel.holesContext.createHole();
												functionHole.checkAgainst(Context.cons(proofTree.context, "u", product.domain), new Product(null, constantType, product.range));
												
												Term lambdaBody = new Application(functionHole, constantApplication);
												hole.checkEquals(new Lambda("u", product.domain, lambdaBody));
											});
										});
										menu.add(item);
										show = true;
									}
								}
							}
						}
					}
					
					{
						JMenuItem item = new JMenuItem(new TeXFormula("\\Rightarrow_E").createTeXIcon(TeXConstants.STYLE_DISPLAY, proofBuilderPanel.latexPointSize));
						item.addActionListener((ActionEvent e) -> {
							proofBuilderPanel.changeTerm(() -> {
								Hole antecedentHole = proofBuilderPanel.holesContext.createHole();
								antecedentHole.checkAgainst(proofTree.context, Term.prop);
								Hole functionHole = proofBuilderPanel.holesContext.createHole();
								functionHole.checkAgainst(proofTree.context, new Product(null, antecedentHole, type));
								Hole argumentHole = proofBuilderPanel.holesContext.createHole();
								argumentHole.checkAgainst(proofTree.context, antecedentHole);
								hole.checkEquals(new Application(functionHole, argumentHole));
							});
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
