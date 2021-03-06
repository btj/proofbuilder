package proofbuilder;

import java.awt.Graphics;

import javax.swing.Icon;

public class ProofIcon extends ProofViewComponent {
	
	Icon icon;
	
	ProofIcon(ProofBuilderPanel proofBuilderPanel, Icon icon, int x, int y) {
		super(proofBuilderPanel);
		this.icon = icon;
		this.x = x;
		this.y = y;
		this.width = icon.getIconWidth();
		this.height = icon.getIconHeight();
	}
	
	@Override
	void paintComponent(Graphics g) {
		icon.paintIcon(proofBuilderPanel, g, 0, 0);
	}

}
