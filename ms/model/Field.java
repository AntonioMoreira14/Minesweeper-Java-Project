package ms.model;

import java.util.ArrayList;
import java.util.List;

public class Field {
	private final int row;
	private final int column;
	
	private boolean open = false;
	private boolean mined = false;
	private boolean marked = false;
	
	private List<Field> nearSquares = new ArrayList<>();
	private List<FieldObserver> observers = new ArrayList<>();
	
	public void registObservers(FieldObserver obs) {
		observers.add(obs);
	}
	
	private void notifyObservers(FieldEvent event) {
		observers.stream().forEach(ob -> ob.EventOccured(this, event));
	}
	
	Field(int row, int col) {
		this.row = row;
		this.column = col;
	}
	
	boolean addSquare(Field nearSquare) {
		boolean difRow = row != nearSquare.row; 
		boolean difCol = column != nearSquare.column;
		boolean diagonal = difRow && difCol; 
		
		int deltaRow = Math.abs(row - nearSquare.row);
		int deltaCol = Math.abs(column - nearSquare.column);
		int deltaGeneral = deltaCol + deltaRow;
		
		if(deltaGeneral == 1 && !diagonal) {
			nearSquares.add(nearSquare);
			return true;
		} else if(deltaGeneral == 2 && diagonal) {
			nearSquares.add(nearSquare);
			return true;
		} else {
			return false;
		}
	}
	
	public void markedOrNot() {
		if(!open) {
			marked = !marked;
			
			if(marked) {
				notifyObservers(FieldEvent.MARK);
			} else {
				notifyObservers(FieldEvent.CLEAR);
			}
		}
	}
	
	public boolean openSqr() {
		if(!open && !marked) {
			if(mined) {
				notifyObservers(FieldEvent.EXPLODE);
				return true;
			}
			
			setOpen(true);	
			
			if(nearSqrSecure()) { // recursive call
				nearSquares.forEach(v -> v.openSqr());
			}
			return true;
			
		} else {
			return false;
		}		
	}
	
	public boolean nearSqrSecure() {
		return nearSquares.stream().noneMatch(v -> v.mined);
	}
	
	void mineField() {
		mined = true;
	}
	
	public boolean isMined() {
		return mined;
	}
	
	public boolean isMarked() {
		return marked;
	}
	
	public void setOpen(boolean open) {
		this.open = open;
		
		if(open) {
			notifyObservers(FieldEvent.OPEN);
		}
	}

	public boolean isOpen() {
		return open;
	}
	
	public boolean isClosed() {
		return !isOpen();
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}
	
	boolean goalAchieved() {
		boolean freeSqr = !mined && open;
		boolean protectedSqr = mined && marked;
		
		return protectedSqr || freeSqr;
	}
	
	public int nearMines() {
		return (int) nearSquares.stream().filter(v -> v.mined).count();
	}
	
	void reboot() {
		open = false;
		mined = false;
		marked = false;
		notifyObservers(FieldEvent.RESET);
	}
}
