package util;

import com.googlecode.objectify.ObjectifyService;

import data.Record;

/**
 * 
 *
 */
public class ObjectifyUtil {

	/**
	 * 
	 */
	public static void setup() {
		
		ObjectifyService.begin();
	    ObjectifyService.register(Record.class);
	}
}
