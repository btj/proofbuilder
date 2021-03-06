package proofbuilder;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import proofbuilder.coq.HolesContext;
import proofbuilder.coq.ProofTree;

public class ProofBuilderFrame extends JFrame {
	
	ProofBuilderPanel proofBuilderPanel;
	
	public ProofBuilderFrame(HolesContext holesContext, ProofTree proofTree) {
		super("Proof Builder");
		
		proofBuilderPanel = new ProofBuilderPanel(holesContext, proofTree);
		JPanel scrollPaneContentsPanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(scrollPaneContentsPanel, BoxLayout.Y_AXIS);
		scrollPaneContentsPanel.setLayout(boxLayout);
		scrollPaneContentsPanel.add(Box.createVerticalGlue());
		scrollPaneContentsPanel.add(proofBuilderPanel);
		scrollPaneContentsPanel.setBackground(Color.white);
		getContentPane().add(new JScrollPane(scrollPaneContentsPanel));
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static void showFrame(HolesContext holesContext, ProofTree proofTree) {
		EventQueue.invokeLater(() -> {
			new ProofBuilderFrame(holesContext, proofTree);
		});
	}

}
