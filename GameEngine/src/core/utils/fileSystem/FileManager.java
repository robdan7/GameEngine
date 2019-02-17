package core.utils.fileSystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
//import java.io.InputStreamReader;

/**
 * This class is used for fetching files inside the java source folders.
 * @author Robin
 *
 */
public class FileManager {
	
	/**
	 * Reads a file and returns it.
	 * @param f - the file source
	 * @return The requested file.
	 */
	public static File getFile( String f) {
		File file= new File(FileManager.class.getResource(f).getFile());
		return file;
	}
	
	/**
	 * Get an input stream for files.
	 * @param f - the file requested.
	 * @return An input stream
	 * @throws FileNotFoundException
	 */
	public static InputStream getStream( String f) throws FileNotFoundException {
		InputStream in = FileManager.class.getResourceAsStream(f);
		return in;
	}
	
	/**
	 * Get a buffered reader.
	 * @param f - the file requested.
	 * @return A buffered reader.
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static BufferedReader getReader( String f) throws FileNotFoundException, UnsupportedEncodingException {
		InputStreamReader in = new InputStreamReader(FileManager.class.getResourceAsStream(f), "UTF-8");
		//FileReader in = new FileReader(f);
		BufferedReader reader = new BufferedReader(in);
		return reader;
	}

}

