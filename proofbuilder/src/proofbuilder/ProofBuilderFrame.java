package proofbuilder;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import proofbuilder.coq.HolesContext;
import proofbuilder.coq.ProofTree;

public class ProofBuilderFrame extends JFrame {
	
	ProofBuilderPanel proofBuilderPanel;
	
	public ProofBuilderFrame(HolesContext holesContext, ProofTree proofTree) {
		super("Proof Builder");
		
		proofBuilderPanel = new ProofBuilderPanel(holesContext, proofTree);
		getContentPane().add(new JScrollPane(proofBuilderPanel));
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
