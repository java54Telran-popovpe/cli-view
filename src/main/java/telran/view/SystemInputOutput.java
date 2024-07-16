package telran.view;

import java.io.*;

public class SystemInputOutput implements InputOutput{
	
	BufferedReader reader = new BufferedReader( new InputStreamReader(System.in));
	PrintStream writer = System.out;
	
	@Override
	public String readString(String promt) {
		writeLine(promt);
		try {
			return reader.readLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void writeString(String str) {
		writer.print(str);
	}

}
