package ms.views;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import ms.model.Field;
import ms.model.FieldEvent;
import ms.model.FieldObserver;

@SuppressWarnings("serial")
public class ButtonField extends JButton 
	implements FieldObserver, MouseListener {
	
	private Field field;
	private final Color BG_DEFAULT = new Color(184, 184, 184);
	private final Color BG_MARK = new Color(8, 179, 247);
	private final Color BG_EXPLODE = new Color(189, 66, 68);
	private final Color TEXT_GREEN = new Color(0, 100, 0);
	
	public ButtonField(Field field) {
		this.field = field;
		setBackground(BG_DEFAULT);
		setBorder(BorderFactory.createBevelBorder(0));
		setOpaque(true);
		
		addMouseListener(this);
		field.registObservers(this);
	}
	
	@Override
	public void EventOccured(Field field, FieldEvent event) {
		switch(event) {
			case OPEN:
				applyStyleOnOpen();
				break;
			case MARK:
				applyStyleOnMark();
				break;
			case EXPLODE:
				applyStyleOnExplode();
				break;
			default:
				applyStyleDefault();
		}
		// force redesigned of components
		SwingUtilities.invokeLater(() -> {
			repaint();
			validate();
		});
	}
	
	public void applyStyleOnOpen() {
		
		setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		if(field.isMined()) {
			setBackground(BG_EXPLODE);
			return;
		}
		
		setBackground(BG_DEFAULT);
		
		
		switch(field.nearMines()) {
			case 1:
				setForeground(TEXT_GREEN);
				break;
			case 2:
				setForeground(Color.BLUE);
				break;
			case 3:
				setForeground(Color.YELLOW);
				break;
			case 4:
			case 5:
			case 6:
				setForeground(Color.RED);
				break;
			default:
				setForeground(Color.PINK);
		}
		
		String value = !field.nearSqrSecure() ? 
				field.nearMines() + "" : "";
		setText(value);
	}
	
	public void applyStyleOnMark() {
		setBackground(BG_MARK);
		setForeground(Color.DARK_GRAY);
		setText("M");
	}
	
	public void applyStyleOnExplode() {
		setBackground(BG_EXPLODE);
		setForeground(Color.BLACK);
		setText("X");
	}
	
	public void applyStyleDefault() {
		setBorder(BorderFactory.createBevelBorder(0));
		setBackground(BG_DEFAULT);
		setText("");
	}
	
	//Interface of Mouse events
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == 1) {
			field.openSqr();
		} else {
			field.markedOrNot();
		}
	}
	
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
}
