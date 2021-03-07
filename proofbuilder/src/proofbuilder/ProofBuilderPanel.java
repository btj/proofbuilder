package proofbuilder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.JPanel;

import proofbuilder.coq.Constant;
import proofbuilder.coq.HolesContext;
import proofbuilder.coq.ProofTree;

public abstract class ProofBuilderPanel extends JPanel {
	
	static final int MARGIN = 20;

	Map<String, Constant> constants;
	HolesContext holesContext;
	ProofTree proofTree;
	ProofTreeView proofView;
	int nbChanges;
	
	ProofBuilderPanel(Map<String, Constant> constants, HolesContext holesContext, ProofTree proofTree) {
		this.constants = constants;
		this.holesContext = holesContext;
		setBackground(Color.white);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				proofView.handleParentMouseEvent(e);
			}
		});
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == 26) // Ctrl+Z
					undo();
				if (e.getKeyChar() == 6) // Ctrl+F
					showFillHoleDialog();
			}
		});
		
		this.proofTree = proofTree;
		refreshProofTreeView();
	}
	
	abstract void showFillHoleDialog();
	
	@Override
	public boolean isFocusable() {
		return true;
	}
	
	void refreshProofTreeView() {
		proofView = new ProofTreeView(this, null, proofTree);
		proofView.computeLayout();
		proofView.x = MARGIN;
		proofView.y = MARGIN;
		revalidate();
		repaint();
	}
	
	void changeTerm(Runnable body) {
		holesContext.push();
		body.run();
		refreshProofTreeView();
		nbChanges++;
	}
	
	void undo() {
		if (nbChanges > 0) {
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
