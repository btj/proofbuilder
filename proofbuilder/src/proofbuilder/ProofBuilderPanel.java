package proofbuilder;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import proofbuilder.coq.ProofTree;

public class ProofBuilderPanel extends JPanel {

	ProofView proofView;
	
	ProofBuilderPanel(ProofTree proofTree) {
		proofView = new ProofView(proofTree);
		proofView.computeLayout();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(proofView.width, proofView.height);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		proofView.paint(this, g);
	}
	
}
