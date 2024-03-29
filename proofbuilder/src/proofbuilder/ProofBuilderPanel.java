package proofbuilder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JPanel;

import proofbuilder.coq.Constant;
import proofbuilder.coq.Context;
import proofbuilder.coq.EmptyContext;
import proofbuilder.coq.HolesContext;
import proofbuilder.coq.ProofTree;

public class ProofBuilderPanel extends JPanel {
	
	static final int MARGIN = 20;

	Map<String, Constant> constants;
	HolesContext holesContext;
	ProofTree proofTree;
	ArrayList<ProofTree> proofTreeUndoStack = new ArrayList<>();
	ProofTreeView proofView;
	int nbChanges;
	int latexPointSize = 20;
	int zoomExponent = 0;
	
	ProofBuilderPanel(NamedProofTree namedProofTree) {
		setBackground(Color.white);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				proofView.handleParentMouseEvent(e);
			}
		});
		
		setNamedProofTree(namedProofTree);
	}
	
	public void setNamedProofTree(NamedProofTree namedProofTree) {
		this.constants = namedProofTree.constants;
		this.holesContext = namedProofTree.holesContext;
		this.proofTree = namedProofTree.proofTree;
		refreshProofTreeView();
	}

	void refreshProofTreeView() {
		proofView = new ProofTreeView(this, null, proofTree);
		proofView.computeLayout();
		proofView.x = MARGIN;
		proofView.y = MARGIN;
		revalidate();
		repaint();
	}
	
	void setZoomExponent(int zoomExponent) {
		this.zoomExponent = zoomExponent;
		latexPointSize = (int)(20 * Math.pow(1.25, zoomExponent));
		refreshProofTreeView();
	}
	
	void zoomIn() {
		setZoomExponent(zoomExponent + 1);
	}
	
	void zoomOut() {
		setZoomExponent(zoomExponent - 1);
	}
	
	void changeTerm(Runnable body) {
		holesContext.push();
		proofTreeUndoStack.add(this.proofTree);
		body.run();
		this.proofTree = this.proofTree.term.reduce().checkAgainst(Context.empty, this.proofTree.expectedType);
		refreshProofTreeView();
		nbChanges++;
	}
	
	void undo() {
		if (nbChanges > 0) {
			holesContext.pop();
			this.proofTree = proofTreeUndoStack.remove(proofTreeUndoStack.size() - 1);
			refreshProofTreeView();
			nbChanges--;
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(2 * MARGIN + proofView.width, 2 * MARGIN + proofView.height);
	}
	
	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.translate(MARGIN, MARGIN);
		proofView.paint(g);
	}
	
}
