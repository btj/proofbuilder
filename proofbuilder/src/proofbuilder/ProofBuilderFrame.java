package proofbuilder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import proofbuilder.coq.Constant;
import proofbuilder.coq.Hole;
import proofbuilder.coq.HolesContext;
import proofbuilder.coq.ProofTree;
import proofbuilder.coq.Term;
import proofbuilder.coq.TypeException;
import proofbuilder.coq.parser.ParserException;

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
			
			@Override
			void showFillHoleDialog() {
				ArrayList<Integer> unfilledHoleIds = new ArrayList<>();
				List<Hole> holes = ProofBuilder.holesContext.getHoles();
				for (Hole hole : holes) {
					if (!hole.isFilled())
						unfilledHoleIds.add(hole.id);
				}
				if (unfilledHoleIds.size() == 0) {
					JOptionPane.showMessageDialog(ProofBuilderFrame.this, "There are no holes to fill!");
					return;
				}
				JDialog dialog = new JDialog(ProofBuilderFrame.this, "Fill Hole", true);
				JLabel holeLabel = new JLabel("Hole");
				JComboBox<Integer> holeComboBox = new JComboBox<>(unfilledHoleIds.toArray(new Integer[unfilledHoleIds.size()]));
				JLabel contentsLabel = new JLabel("Contents");
				JTextField contentsField = new JTextField();
				JButton okButton = new JButton("OK");
				okButton.addActionListener((ActionEvent e) -> {
					try {
						Term term = proofbuilder.pythonparser.Parser.parseExpression(ProofBuilder.pythonConstants, contentsField.getText());
						Hole hole = holes.get(unfilledHoleIds.get(holeComboBox.getSelectedIndex()) - 1); 
						try {
							changeTerm(() -> hole.checkEquals(term));
							dialog.dispose();
						} catch (TypeException ex) {
							holesContext.pop(); // Undo the push performed by changeTerm
							JOptionPane.showMessageDialog(dialog, "Type error: " + ex.getMessage(), "Fill Hole: Type Error", JOptionPane.ERROR_MESSAGE);
						}
					} catch (ParserException ex) {
						JOptionPane.showMessageDialog(dialog, "Parse error: " + ex.getMessage(), "Fill Hole: Parse Error", JOptionPane.ERROR_MESSAGE);
					}
				});
				GridBagLayout layout = new GridBagLayout();
				GridBagConstraints c = new GridBagConstraints();
				dialog.getContentPane().setLayout(layout);
				c.gridx = 0;
				c.gridy = 0;
				c.anchor = GridBagConstraints.LINE_END;
				dialog.getContentPane().add(holeLabel, c);
				c.gridx = 1;
				c.anchor = GridBagConstraints.LINE_START;
				dialog.getContentPane().add(holeComboBox, c);
				c.gridx = 0;
				c.gridy = 1;
				c.anchor = GridBagConstraints.LINE_END;
				dialog.getContentPane().add(contentsLabel, c);
				c.gridx = 1;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1.0;
				dialog.getContentPane().add(contentsField, c);
				c.gridx = 0;
				c.gridy = 2;
				c.gridwidth = 2;
				c.fill = GridBagConstraints.NONE;
				c.anchor = GridBagConstraints.LINE_END;
				dialog.getContentPane().add(okButton, c);
				dialog.setPreferredSize(new Dimension(400, 200));
				dialog.pack();
				dialog.setLocationRelativeTo(ProofBuilderFrame.this);
				dialog.setVisible(true);
			}
		};
		JPanel scrollPaneContentsPanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(scrollPaneContentsPanel, BoxLayout.Y_AXIS);
		scrollPaneContentsPanel.setLayout(boxLayout);
		scrollPaneContentsPanel.add(Box.createVerticalGlue());
		scrollPaneContentsPanel.add(proofBuilderPanel);
		scrollPaneContentsPanel.setBackground(Color.white);
		JScrollPane scrollPane = new JScrollPane(scrollPaneContentsPanel);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		getContentPane().add(scrollPane);
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
