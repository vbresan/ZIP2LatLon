package data;

import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * 
 *
 */
@Entity
public class Record {
	
	@Id
	private String query;
	private String json;
	
	@Index
	private Date created;
	@Index
	private Date accessed;

	/**
	 * 
	 */
	public Record() {
		// necessary for Objectify
	}

	/**
	 * 
	 * @param query
	 * @param json
	 */
	public Record(String query, String json) {

		this.query = query;
		this.json  = json;
		
		Date date = new Date();
		created  = date;
		accessed = date;
	}

	/**
	 * 
	 */
	public void updateAccessDate() {
		accessed = new Date();
	}

	/**
	 * 
	 * @return
	 */
	public String getJSON() {
		return json;
	}	
}
