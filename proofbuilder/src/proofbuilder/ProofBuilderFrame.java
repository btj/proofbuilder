package proofbuilder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import proofbuilder.coq.Constant;
import proofbuilder.coq.HolesContext;
import proofbuilder.coq.ProofTree;

public class ProofBuilderFrame extends JFrame {
	
	ProofBuilderPanel proofBuilderPanel;
	
	public ProofBuilderFrame(Map<String, Constant> constants, HolesContext holesContext, ProofTree proofTree) {
		super("Proof Builder");
		
		proofBuilderPanel = new ProofBuilderPanel(constants, holesContext, proofTree) {
			@Override
			public void refreshProofTreeView() {
				super.refreshProofTreeView();
				int nbUnfilledHoles = holesContext.getNbUnfilledHoles();
				setTitle("Proof Builder - " + (nbUnfilledHoles == 1 ? "1 hole" : nbUnfilledHoles + " holes"));
				if (holesContext.getNbUnfilledHoles() == 0)
					JOptionPane.showMessageDialog(ProofBuilderFrame.this, "Proof complete!");
			}
		};
		JPanel scrollPaneContentsPanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(scrollPaneContentsPanel, BoxLayout.Y_AXIS);
		scrollPaneContentsPanel.setLayout(boxLayout);
		scrollPaneContentsPanel.add(Box.createVerticalGlue());
		scrollPaneContentsPanel.add(proofBuilderPanel);
		scrollPaneContentsPanel.setBackground(Color.white);
		getContentPane().add(new JScrollPane(scrollPaneContentsPanel));
		setPreferredSize(new Dimension(800, 600));
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static void showFrame(Map<String, Constant> constants, HolesContext holesContext, ProofTree proofTree) {
		EventQueue.invokeLater(() -> {
			new ProofBuilderFrame(constants, holesContext, proofTree);
		});
	}

}
