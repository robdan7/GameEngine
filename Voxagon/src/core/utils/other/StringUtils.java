package core.utils.other;

public class StringUtils {
	
	/**
	 * remove all characters after a specified string.
	 * @param s
	 * @param regex - Inclusive substring.
	 * @return The full input string will be returned if there is nothing to remove or if no substring could be found.
	 */
	public static String removeAfter(String s, String regex) {
		int index = s.indexOf(regex);
		if (index != -1) {
			s = s.substring(0,index);
		}
		return s;
	}
	
	public static String readBetween(String s, String start, String stop) {
		int startIndex = s.indexOf(start);
		int stopIndex = s.indexOf(stop);
		return s.substring(startIndex+start.length(), stopIndex).replaceFirst("\\s*", "");
	}
	

}
