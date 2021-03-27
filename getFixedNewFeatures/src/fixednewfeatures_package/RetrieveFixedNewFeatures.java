package fixednewfeatures_package;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONArray;
import java.util.logging.Logger; 

public class RetrieveFixedNewFeatures {
	
   public static final List<LocalDateTime> featuresFixed = new ArrayList<LocalDateTime>();
   protected static final Map<String,LocalDateTime> issueKey = new HashMap<> ();

   private static String readAll(Reader rd) throws IOException {
	      StringBuilder sb = new StringBuilder();
	      int cp;
	      while ((cp = rd.read()) != -1) {
	         sb.append((char) cp);
	      }
	      return sb.toString();
	   }

   public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
	      InputStream is = new URL(url).openStream();
	      try {
	         BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
	         String jsonText = readAll(rd);
	         return new JSONArray(jsonText);
	       } finally {
	         is.close();
	       }
	   }

   public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
      InputStream is = new URL(url).openStream();
      try (BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
          return new JSONObject(readAll(rd));
      }
   }
      
   
  
   public static void main(String[] args) throws IOException, JSONException {
	   
	   String projName ="TAJO";
	   Integer j = 0;
	   Integer i = 0;
	   Integer total = 1;
	   //Get JSON API for fixed new features w/ AV in the project
	   j = i + 1000;
	   String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                 + projName + "%22AND%22issueType%22=%22"+"New+Feature"+"%22AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt="
                 + i.toString() + "&maxResults=" + j.toString();
	   JSONObject json = readJsonFromUrl(url);
	   JSONArray issues = json.getJSONArray("issues");
  
	   for (i=0; i < issues.length(); i++ ) {
     	 JSONObject obj = issues.getJSONObject(i);
     	 String key = obj.get("key").toString();
     	 JSONObject fields = obj.getJSONObject("fields");
     	 String resolutiondate = (String)fields.getString("resolutiondate");
     	 String date = resolutiondate.split("T")[0];
     	 addNewFeature(date,key);
     	
	   }
	   
	   
	   Collections.sort(featuresFixed,(LocalDateTime o1, LocalDateTime o2) -> o1.compareTo(o2));  
		
		
		try(FileWriter fileWriter = new FileWriter(projName + "FixedDatesTAJO.csv")) {
			fileWriter.append("Index,Key,Date");
	        fileWriter.append("\n");
	        for ( i = 0; i < issueKey.size(); i++) {
	           Integer index = i + 1;
	           fileWriter.append(index.toString());
	           fileWriter.append(",");
	           fileWriter.append((String) issueKey.keySet().toArray()[i]);
	           fileWriter.append(",");
	           LocalDateTime date = issueKey.get(issueKey.keySet().toArray()[i]);
	           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	           String formatDateTime = date.format(formatter);
	           fileWriter.append(formatDateTime);
	           fileWriter.append("\n");
	           fileWriter.flush();
	        }
		} 
		catch (Exception e) {
			final Logger log = Logger.getLogger(RetrieveFixedNewFeatures.class.getName());
	    	log.info("Error in csv writer");
	        e.printStackTrace();
	     }
		
		
		
		 public static void addNewFeature(String strDate, String id) {
	  		 LocalDate date = LocalDate.parse(strDate);
		     LocalDateTime dateTime = date.atStartOfDay();
		     issueKey.put(id, dateTime);
		     if (!featuresFixed.contains(dateTime))
		         featuresFixed.add(dateTime);
		     
		      return;
		  }
	  	 
	  }


