package ms.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Table implements FieldObserver {
	
	private final int rows;
	private final int columns;
	private final int mines;
	
	private final List<Field> fields = new ArrayList<Field>();
	private final List<Consumer<ResultEvent>> tableObs = new ArrayList<>();
	
	public Table(int rows, int columns, int mines) {
		this.rows = rows;
		this.columns = columns;
		this.mines = mines;
		
		generateFields();
		associateNearSqrs();
		randomizeMines();
	}
	
	public void forEachField(Consumer<Field> function) {
		fields.forEach(function);
	}
	
	public void registTableObs(Consumer<ResultEvent> obs) {
		tableObs.add(obs);
	}
	
	private void notifyTableObs(boolean result) {
		tableObs.stream().forEach(ob -> ob.accept(new ResultEvent(result)));
	}
	
	public void openField(int row, int column) {
		fields.parallelStream()
			.filter(c -> c.getRow() == row && c.getColumn() == column)
			.findFirst()
			.ifPresent(c -> c.openSqr());
	}
	
	public void markField(int row, int column) {
		fields.parallelStream()
			.filter(c -> c.getRow() == row && c.getColumn() == column)
			.findFirst()
			.ifPresent(c -> c.markedOrNot());
	}
	
	private void generateFields() {
		for(int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				Field f1 = new Field(r, c);
				f1.registObservers(this);
				fields.add(f1);
			}
		}
	}
	
	private void associateNearSqrs() {
		for(Field f1: fields) {
			for(Field f2: fields) {
				f1.addSquare(f2);
			}
		}
	}
	
	private void randomizeMines() {
		long armedMines = 0;
		Predicate<Field> mined = c -> c.isMined();
		
		do {
			int random = (int) (Math.random() * fields.size());
			fields.get(random).mineField();
			armedMines = fields.stream().filter(mined).count();
			
		} while(armedMines < mines);
	}
	
	public boolean wonGame() {
		return fields.stream().allMatch(c -> c.goalAchieved());
	}
	
	public void resetGame() {
		fields.stream().forEach(c -> c.reboot());
		randomizeMines();
	}
	
	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}

	@Override
	public void EventOccured(Field field, FieldEvent event) {
		if(event == FieldEvent.EXPLODE) {
			showMines();
			notifyTableObs(false);
		} else if(wonGame()){
			notifyTableObs(true);
		}
	}
	
	private void showMines() {
		fields.stream()
			.filter(c -> c.isMined())
			.filter(c -> !c.isMarked())
			.forEach(c -> c.setOpen(true));
	}
}
