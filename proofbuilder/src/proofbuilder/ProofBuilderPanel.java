package proofbuilder;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

public class ProofBuilderPanel extends JPanel {
	
	TeXIcon icon;
	
	ProofBuilderPanel() {
		TeXFormula formula = new TeXFormula("\\textcolor{blue}{\\texttt{Hello world!}}");
				//"(\\forall x.\\;\\mathsf{m}(x) \\Rightarrow \\mathsf{s}(x)) \\land \\mathsf{m}(\\mathsf{S}) \\Rightarrow \\mathsf{s}(\\mathsf{S})");
		icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(600, 400);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		icon.paintIcon(this, g, (getWidth() - icon.getIconWidth()) / 2, (getHeight() - icon.getIconHeight()) / 2);
	}
	
}
