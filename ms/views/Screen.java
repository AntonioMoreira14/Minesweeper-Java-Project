package ms.views;

import javax.swing.JFrame;

import ms.model.Table;

@SuppressWarnings("serial")
public class Screen extends JFrame {
	
	public Screen() {
		Table table = new Table(16, 30, 50);
		add(new Dashboard(table));
		
		setTitle("Minesweeper Project JAVA");
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(690, 438);
	}
	
	public static void main(String[] args) {
		new Screen();
	}
}
