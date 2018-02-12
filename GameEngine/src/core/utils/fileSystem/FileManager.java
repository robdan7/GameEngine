package core.utils.fileSystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
//import java.io.InputStreamReader;

public class FileManager {
	public static File getFile( String f) {
		File file= new File(FileManager.class.getResource(f).getFile());
		return file;
	}
	public static InputStream getStream( String f) throws FileNotFoundException {
		InputStream in = FileManager.class.getResourceAsStream(f);
		return in;
	}
	
	public static BufferedReader getReader( String f) throws FileNotFoundException, UnsupportedEncodingException {
		InputStreamReader in = new InputStreamReader(FileManager.class.getResourceAsStream(f), "UTF-8");
		//FileReader in = new FileReader(f);
		BufferedReader reader = new BufferedReader(in);
		return reader;
	}

}

