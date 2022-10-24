package ms.views;

import java.awt.GridLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ms.model.Table;

@SuppressWarnings("serial")
public class Dashboard extends JPanel {
	
	public Dashboard(Table table) {
		setLayout(new GridLayout(table.getRows(), table.getColumns()));
		
		table.forEachField(t -> add(new ButtonField(t)));
		table.registTableObs(e -> {
			
			SwingUtilities.invokeLater(() -> {
				if(e.isWon()) {
					JOptionPane.showMessageDialog(this, "You won :)");
				} else {
					JOptionPane.showMessageDialog(this, "You lose :(");
				}
			
				table.resetGame();
			});
		});
	}
}
