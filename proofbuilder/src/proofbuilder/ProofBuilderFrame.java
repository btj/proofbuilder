package proofbuilder;

import java.awt.EventQueue;

import javax.swing.JFrame;

public class ProofBuilderFrame extends JFrame {
	
	ProofBuilderPanel proofBuilderPanel = new ProofBuilderPanel();
	
	public ProofBuilderFrame() {
		super("Proof Builder");
		
		getContentPane().add(proofBuilderPanel);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static void showFrame(String[] args) {
		EventQueue.invokeLater(() -> {
			new ProofBuilderFrame();
		});
	}

}
