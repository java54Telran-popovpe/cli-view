package telran.view;

import java.util.stream.IntStream;

public class Menu implements Item {
	private static final String CHARACTER = "*";
	private static final int N_CHARACTER = 30;
	private static final int OFFSET = 7;
	String name;
	Item[] items;

	public Menu(String name, Item[] items) {
		this.name = name;
		this.items = items;
	}

	@Override
	public String displayName() {
		return name;
	}

	@Override
	public void perform(InputOutput io) {
		boolean running = true;
		displayTitle( io);
		do {
			displayItems( io );
			int itemNumber = io.readNumberRange("Enter item number", "Not existing item", 1, items.length + 1).intValue();
			try {
				Item item = items[itemNumber - 1];
				item.perform(io);
				if (item.isExit()) {
					running = false;
				}
				} catch (RuntimeException e) {
					io.writeLine(e.getMessage());
				}
			} while( running );

	}

	private void displayItems(InputOutput io) {
		IntStream.range(0, items.length).forEach( i -> io.writeLine(String.format("%d. %s", i + 1, items[i].displayName())));
		
	}

	private void displayTitle( InputOutput io) {
		io.writeLine(CHARACTER.repeat(N_CHARACTER));
		io.writeLine(String.format("%s%s", " ".repeat(OFFSET), name));
		io.writeLine(CHARACTER.repeat(N_CHARACTER));
		
	}

	@Override
	public boolean isExit() {
		return false;
	}
	
	

}
