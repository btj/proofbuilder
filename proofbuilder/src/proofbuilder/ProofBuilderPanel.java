package proofbuilder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import proofbuilder.coq.HolesContext;
import proofbuilder.coq.ProofTree;

public class ProofBuilderPanel extends JPanel {
	
	static final int MARGIN = 20;

	HolesContext holesContext;
	ProofTree proofTree;
	ProofTreeView proofView;
	int nbChanges;
	
	ProofBuilderPanel(HolesContext holesContext, ProofTree proofTree) {
		this.holesContext = holesContext;
		setBackground(Color.white);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				proofView.handleParentMouseEvent(e);
			}
		});
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == 26)
					undo();
			}
		});
		
		this.proofTree = proofTree;
		refreshProofTreeView();
	}
	
	@Override
	public boolean isFocusable() {
		return true;
	}
	
	void refreshProofTreeView() {
		proofView = new ProofTreeView(this, null, proofTree);
		proofView.computeLayout();
		proofView.x = MARGIN;
		proofView.y = MARGIN;
		holesContext.push();
		revalidate();
		repaint();
	}
	
	void termChanged() {
		refreshProofTreeView();
		nbChanges++;
	}
	
	void undo() {
		if (nbChanges > 0) {
			holesContext.pop();
			holesContext.pop();
			refreshProofTreeView();
			nbChanges--;
		}
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
