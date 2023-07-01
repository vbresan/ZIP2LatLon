import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.googlecode.objectify.ObjectifyService;

import data.Record;
import util.ObjectifyUtil;
import util.StringUtil;

@SuppressWarnings("serial")
@WebServlet(
    name = "JSONAPI",
    urlPatterns = {"/json"}
)
public class JSONServlet extends HttpServlet {
	
	private static final String API_KEY = 
		"";
			
	private static final String URI = 
		"https://maps.google.com/maps/api/geocode/json?";	
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	private boolean areParametersDefined(HttpServletRequest request) {
		
		String zip = request.getParameter("address");
    	return StringUtil.isDefined(zip);
	}
	
	/**
	 * 
	 * @param query 
	 * @return
	 */
	private String getRequestURL(String query) {
		return URI + query + "&key=" + API_KEY;
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	private String getJSONResponse(URL url) {
		
		String response = "{}";
		
		try {
			InputStreamReader input  = new InputStreamReader(url.openStream());
			BufferedReader    reader = new BufferedReader(input);
			StringBuffer 	  buffer = new StringBuffer();
			String line;

			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			
			reader.close();
			response = buffer.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return response;
	}
	
	/**
	 * 
	 * @param record
	 */
	private void saveRecord(Record record) {
		ObjectifyService.ofy().save().entity(record);
	}
	
	/**
	 * 
	 * @param query 
	 * @return
	 */
	private Record getRecord(String query) {
		return ObjectifyService.ofy().load().type(Record.class).id(query).now();
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 */
	private Record getNewRecord(String query) {
		
		Record record = null;
		
    	try {
			URL url = new URL(getRequestURL(query));
	    	String jsonString = getJSONResponse(url);
	    	
			JSONObject jsonObject = new JSONObject(jsonString);
			String     status     = jsonObject.getString("status");
			
			if (StringUtil.isDefined(status) && status.equalsIgnoreCase("OK")) {
				record = new Record(query, jsonString);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return record;
	}

	/**
	 * 
	 * @param query
	 * @return
	 */
	private String getJSONResponse(String query) {
		
		String response = "{}";
		
		Record record = getRecord(query);
		if (record == null) {
			
			record = getNewRecord(query);
			if (record != null) {
				saveRecord(record);
				response = record.getJSON();
			}
		} else {
			record.updateAccessDate();
			saveRecord(record);
			response = record.getJSON();
		}
		
		return response;
	}

	/**
	 * 
	 */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
    	
    	response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
    	
    	if (!areParametersDefined(request)) {
    		response.getWriter().print("{}");
			return;
		}
    	
    	ObjectifyUtil.setup();
    	
    	String jsonString = getJSONResponse(request.getQueryString());
    	response.getWriter().print(jsonString);
	}

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    	throws ServletException, IOException {
    	doPost(request, response);
    }
}