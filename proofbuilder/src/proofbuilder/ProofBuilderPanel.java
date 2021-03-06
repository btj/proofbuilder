package proofbuilder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import proofbuilder.coq.ProofTree;

public class ProofBuilderPanel extends JPanel {
	
	static final int MARGIN = 20;

	ProofTreeView proofView;
	
	ProofBuilderPanel(ProofTree proofTree) {
		proofView = new ProofTreeView(this, null, proofTree);
		proofView.computeLayout();
		proofView.x = MARGIN;
		proofView.y = MARGIN;
		setBackground(Color.white);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				proofView.handleParentMouseEvent(e);
			}
		});
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
