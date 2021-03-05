package proofbuilder;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import proofbuilder.coq.ProofTree;

public class ProofBuilderFrame extends JFrame {
	
	ProofBuilderPanel proofBuilderPanel;
	
	public ProofBuilderFrame(ProofTree proofTree) {
		super("Proof Builder");
		
		proofBuilderPanel = new ProofBuilderPanel(proofTree);
		getContentPane().add(new JScrollPane(proofBuilderPanel));
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static void showFrame(ProofTree proofTree) {
		EventQueue.invokeLater(() -> {
			new ProofBuilderFrame(proofTree);
		});
	}

}
