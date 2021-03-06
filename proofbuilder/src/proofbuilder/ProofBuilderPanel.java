package proofbuilder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import proofbuilder.coq.ProofTree;

public class ProofBuilderPanel extends JPanel {
	
	static final int MARGIN = 20;

	ProofTreeView proofView;
	
	ProofBuilderPanel(ProofTree proofTree) {
		proofView = new ProofTreeView(this, proofTree);
		proofView.computeLayout();
		setBackground(Color.white);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(2 * MARGIN + proofView.width, 2 * MARGIN + proofView.height);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.translate(MARGIN, MARGIN);
		proofView.paint(g);
	}
	
}
