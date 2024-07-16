package telran.view;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;


public interface InputOutput {
	String readString( String promt );
	void writeString( String str );
	default void writeLine( Object obj ) {
		writeString( obj.toString() + "\n");
	}
	default <T> T readObject(String promt, String errorPromt, Function<String, T> mapper) {
		T result = null;
		boolean running = false;
		do {
			String str = readString(promt);
			running = false;
			try {
				result = mapper.apply(str);
			} catch (RuntimeException e ) {
				writeLine(errorPromt + " " + e.getMessage());
				running = true;
			}
			
		} while( running );
		return result;
	}
	
	
	default Integer readInt(String promt, String errorPromt) {
		//Entered string must be a number otherwise errorPromt with cycle
		return readObject(promt, errorPromt, Integer::parseInt);
	}
	default Long readLong(String promt, String errorPromt) {
		return readObject(promt, errorPromt, Long::parseLong);
	}
	default Double readDouble(String promt, String errorPromt) {
		//Entered string must be a number otherwise errorPromt with cycle
		return readObject(promt, errorPromt, Double::parseDouble);
	}
	//[min,max)
	default Double readNumberRange(String promt, String errorPromt, double min, double max) {
		Function<String, Double> mapper = str -> {
			Double inputValue = null;
			try {
				inputValue = Double.parseDouble(str);
				if ( inputValue >= min && inputValue < max ) {
					throw new RuntimeException("Number should be greater or equal " + min + " and less then " + max);
				}
			} catch (RuntimeException e) {
				throw new RuntimeException();
			}
			return inputValue;
		};
		return readObject( promt,  errorPromt, mapper );
	}
	default Integer readNumberRange(String promt, String errorPromt, int min, int max) {
		Function<String, Integer> mapper = str -> {
			Integer inputValue = null;
			try {
				inputValue = Integer.parseInt(str);
				if ( inputValue >= min && inputValue < max ) {
					throw new RuntimeException("Number should be greater or equal " + min + " and less then " + max);
				}
			} catch (RuntimeException e) {
				throw new RuntimeException();
			}
			return inputValue;
		};
		return readObject( promt,  errorPromt, mapper );
	}
	

				
	//return string if predicate succeeds
	default String readStringPredicate(String promt, String errorPromt, Predicate<String> predicate) {
		Function<String, String> mapper = str -> {
				if ( predicate.negate().test(str) ) {
					throw new RuntimeException("Input value failed to pass conditions check.");
				}
				return str;
		};
		return readObject( promt,  errorPromt, mapper );
	}
	default String readStringOptions(String promt, String errorPromt, HashSet<String> options) {
		//string one of options
		return readStringPredicate(promt,errorPromt, str -> options.contains(str));
	}
	default LocalDate readIsoDate(String promt, String errorPromt) {
		//Entered string must be a local date in ISO format: yyy-mm-dd
		return readObject(promt, errorPromt, LocalDate::parse);
	}
	default LocalDate readIsoDateRange(String promt, String errorPromt, LocalDate from, LocalDate to) {
		//Entered string must be a local date in ISO format: yyy-mm-dd in range (from,to)
		Function<String, LocalDate> mapper = str -> {
			LocalDate inputValue = null;
			try {
				inputValue = LocalDate.parse(str);
				if ( !inputValue.isAfter(from) || !inputValue.isBefore(to)) {
					throw new RuntimeException((!from.equals(LocalDate.MIN) ? "Date should be after " + from : "" ) + 
												(!to.equals(LocalDate.MAX) ? " Date should be before " + to : "" ));
				}
			} catch (RuntimeException e) {
				throw new RuntimeException(e);
			}
			return inputValue;
		};
		return readObject(promt,errorPromt, mapper);
	}
	
}
