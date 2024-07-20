package telran.view;

import java.util.function.Consumer;

public interface Item {
	String displayName();
	void perform(InputOutput io);
	boolean isExit();
	static Item of(String displayName, Consumer<InputOutput> action, boolean isExit ) {
		return new Item() {

			@Override
			public String displayName() {
				return displayName;
			}

			@Override
			public void perform(InputOutput io) {
				action.accept(io);
				
			}

			@Override
			public boolean isExit() {
				return isExit;
			}
			
		};
	}
	
	static Item of(String displayName, Consumer<InputOutput> action) {
		return  of(displayName, action, false );
	}
	
	static Item ofExit() {
		return  of("Exit", io -> {}, true );
	}
}
