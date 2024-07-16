package telran.view;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
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
		LocalDate dateLastLogin = getDateLastLogin();
		int numberOfLogins = getNumberOfLogins();
		io.writeLine(new User(userName, password, dateLastLogin, phoneNumber, numberOfLogins));
	}

	private LocalDate getDateLastLogin() {
		LocalDate dateLastLogin = io.readIsoDateRange("Enter last login date in format yyyy-mm-dd", "Wrong date", LocalDate.MIN, LocalDate.now().plusDays(1));
		return dateLastLogin;
	}

	private int getNumberOfLogins() {
		Function<String, Integer> mapper = str -> {
			Integer inputValue = null;
			try {
				inputValue = Integer.parseInt(str);
				if ( inputValue < 1  ) {
					throw new RuntimeException("Number should be greater then or equal to 1");
				}
			} catch (RuntimeException e) {
				throw new RuntimeException( e );
			}
			return inputValue;
		};
		return io.readObject("Enter number of logins", "Wrong value", mapper);
	}

	private String getPhoneNumber() {
		Predicate<String> isIsraelMobilePhoneNumber = 
				trueOrThrow(str -> str.matches("(\\+972-?|0)5\\d-?(\\d{3}-\\d{2}-|\\d{2}-?\\d{3}-?)\\d{2}"), 
						"Should be Israel mobile phone number, e.g.: 051-1234567");
		return io.readStringPredicate("Enter phone number", "Wrong phone number:", isIsraelMobilePhoneNumber);
	}

	private String getPassword() {
		Predicate<String> atLeastOneCapital = 
				trueOrThrow(str -> str.codePoints().anyMatch( i -> Character.isUpperCase(i)), 
						"Should be at least one capital letter");
		Predicate<String> atLeastOneLowerCase = 
				trueOrThrow(str -> str.codePoints().anyMatch( i -> Character.isLowerCase(i)), 
						"Should be at least one capital letter");
		Predicate<String> atLeastOneDigit = 
				trueOrThrow(str -> str.codePoints().anyMatch( i -> Character.isDigit(i)), 
						"Should be at least one digit");
		Predicate<String> atLeastOneSpecialChar = 
				trueOrThrow(str -> Arrays.stream(str.split("")).anyMatch(s-> "#$*&%".contains(s)), 
						"Should be at least one symbol from [#$*&%]");
		return io.readStringPredicate("Enter password", "Wrong password:", 
				lengthAtLeast(8).and(atLeastOneCapital).and(atLeastOneLowerCase).and(atLeastOneDigit).and(atLeastOneSpecialChar));
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
