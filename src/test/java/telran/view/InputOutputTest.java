package telran.view;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

record User(String userName, String password, LocalDate dateLastLogin,String phoneNumber, int numberOfLogins) {}



class InputOutputTest {
	
	InputOutput io = new SystemInputOutput();

	@Test
	@Disabled
	void readObjectTest() {
		User user = io.readObject("Enter user in format <username>#<>password>#<dateLastLogin>#<phoneNumber>#<numberOgLogins>", 
				"Wrong user input format", str -> {
					String[] tokens = str.split("#");
					return new User(tokens[0], tokens[1], LocalDate.parse(tokens[2]), tokens[3], Integer.parseInt(tokens[4]));
				});
		io.writeLine(user);
	}
	
	@Test
	void readUserByFields() {
		//TODO create User object from separate fields and display out
		//username at least 6 ASCII letters - first Capital, others Lower case
		//password at least 8 symbols, at least one capital letter,
		//at least one lower case letter, at least one digit, at least one symbol from "#$*&%."
		//phone number - Israel mobile phone
		//dateLastLogin not after current date
		//number of logins any positive number
		String userName = getUserName();
		String password = getPassword();
		String phoneNumber = getPhoneNumber();
		LocalDate dateLastLogin = io.readIsoDateRange("Enter last login date in format yyyy-mm-dd", "Wrong date", 
														LocalDate.MIN, LocalDate.now().plusDays(1));
		int numberOfLogins = io.readNumberRange("Enter number of logins", "Wrong value", 
														1.0, Integer.MAX_VALUE).intValue();
		io.writeLine(new User(userName, password, dateLastLogin, phoneNumber, numberOfLogins));
	}

	private String getPhoneNumber() {
		Predicate<String> isIsraelMobilePhoneNumber = 
				trueOrThrow(str -> str.matches("(\\+972-?|0)5\\d-?(\\d{3}-\\d{2}-|\\d{2}-?\\d{3}-?)\\d{2}"), 
						"Should be Israel mobile phone number, e.g.: 051-1234567");
		return io.readStringPredicate("Enter phone number", "Wrong phone number:", isIsraelMobilePhoneNumber);
	}


	private String getPassword() {
		Set<Integer> specialChars = new HashSet<>(Arrays.asList("#$*&%".codePoints().boxed().toArray(Integer[]::new)));
		Predicate<Integer> atLeastOneCapital = i -> Character.isUpperCase(i);
		Predicate<Integer> atLeastOneLowerCase = i -> Character.isLowerCase(i);
		Predicate<Integer> atLeastOneDigit = i -> Character.isDigit(i);
		Predicate<Integer> atLeastOneSpecialChar =  specialChars::contains;
		return io.readStringPredicate("Enter password", "Password rules: at least 8 symbols, contains one capital letter, one lower case letter, one digit,one symbol from \"#$*&%.\"", 
				str -> str.length() > 7 && str.codePoints().boxed().anyMatch(	atLeastOneCapital
																				.and(atLeastOneDigit)
																				.and(atLeastOneLowerCase)
																				.and(atLeastOneDigit)
																				.and(atLeastOneSpecialChar)
																			));
	}
	
	private String getPassword1() {
		Predicate<String> predicate = str -> {
			boolean capital = false;
			boolean lowerCase = false;
			boolean digit = false;
			boolean specialChar = false;
			Set<Character> specialChars = new HashSet<>(Arrays.asList( new Character[]{'#', '$', '*',  '&',  '%' }));
			if ( str.length() < 8) {
				throw new RuntimeException("Password length should be equal to or greater then 8") ;			
			}
			for(Character chr: str.toCharArray()) {
				capital = !capital && Character.isUpperCase(chr);
				lowerCase = !lowerCase && Character.isLowerCase(chr);
				digit = !digit && Character.isDigit(chr);
				specialChar = !specialChar && specialChars.contains(chr);
			}
			return capital && lowerCase && specialChar && digit;
		};
		
		return io.readStringPredicate("Enter password", "Wrong password:", predicate);
	}

	private String getUserName() {
		
		Predicate<String> allASCIILetters = 
				trueOrThrow(str -> str.codePoints().allMatch(i -> i < 127 && Character.isLetter(i)),
								"only ASCII letters allowed");
		Predicate<String> firstCapitalOtherLowerCase = 
				trueOrThrow( str -> Character.isUpperCase(str.charAt(0)) && str.substring(1).codePoints().allMatch( i -> Character.isLowerCase(i)),
						"first letter should be capital, others - in lower case");
		return io.readStringPredicate("Enter userName", "Wrong username:", 
				lengthAtLeast(6).and(allASCIILetters).and(firstCapitalOtherLowerCase));
	}
	
	private String getUserName1() {
		Predicate<String> predicate = str -> {
			if ( str.length() < 6 ) {
				throw new RuntimeException("Username length should be equal to or greater then 6");
			}
			char[] chrs = str.toCharArray();
			if ( chrs[0] > 127 && !Character.isUpperCase(chrs[0]) ) {
				throw new RuntimeException("First letter should be ASCII uppercase letter");
			}
			int i = 1;
			int len = str.length();
			while( i < len && chrs[i] < 127 && Character.isLowerCase(chrs[i])) {
				i++;
			}
			if ( i < len ) {
				throw new RuntimeException("All letters, beside the first, should be in lowercase");
			}
			return true;
		};
		return io.readStringPredicate("Enter userName", "Wrong username:", predicate);
	}
	
	private Predicate<String> trueOrThrow( Predicate<String> predicate, String message) {
		return str -> { 
			if ( predicate.negate().test(str) )
				throw new RuntimeException( message );
			return true;
		};
	}
	
	private Predicate<String> lengthAtLeast( int length) {
		return trueOrThrow( str -> str.length() >= length,
				"length should be " + length + " or greater");
	}
	
	

}
