package fixednewfeatures_package;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class RetrieveFixedNewFeatures {
	
   public static ArrayList<LocalDateTime> features_fixed;
   public static HashMap<LocalDateTime, String> issue_key;

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
	         BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	         String jsonText = readAll(rd);
	         JSONArray json = new JSONArray(jsonText);
	         return json;
	       } finally {
	         is.close();
	       }
	   }

   public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
      InputStream is = new URL(url).openStream();
      try {
         BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
         String jsonText = readAll(rd);
         JSONObject json = new JSONObject(jsonText);
         return json;
       } finally {
         is.close();
       }
   }


  
  	   public static void main(String[] args) throws IOException, JSONException {
		   
		   String projName ="TAJO";
		   Integer j = 0, i = 0, total = 1;
		   features_fixed = new ArrayList<LocalDateTime>();
	do {
		 //Get JSON API for fixed new features w/ AV in the project
		   j = i + 1000;
		   String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
	                 + projName + "%22AND%22issueType%22=%22"+"New+Feature"+"%22AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt="
	                 + i.toString() + "&maxResults=" + j.toString();
      JSONObject json = readJsonFromUrl(url);
      JSONArray issues = json.getJSONArray("issues");
      issue_key = new HashMap<LocalDateTime, String> ();
      
      total = json.getInt("total");
      for (; i < total && i<j; i++ ) {
         	 JSONObject obj = issues.getJSONObject(i%1000);
         	 String key = obj.get("key").toString();
         	 JSONObject fields = obj.getJSONObject("fields");
         	 String resolutiondate = (String)fields.getString("resolutiondate");
         	 String date = resolutiondate.split("T")[0];
         	 addRelease(date,key);
 	   }
	} while (i < total);
     
    

	
   // sort fixdates
      Collections.sort(features_fixed, new Comparator<LocalDateTime>(){
         //@Override
         public int compare(LocalDateTime o1, LocalDateTime o2) {
             return o1.compareTo(o2);
         }
      });
      if (features_fixed.size() < 6)
         return;
      FileWriter fileWriter = null;
	 try {
         fileWriter = null;
         String outname = projName + "FixedDatesTAJO.csv";
				    //Name of CSV for output
				    fileWriter = new FileWriter(outname);
         fileWriter.append("Index,Key,Date");
         fileWriter.append("\n");
         for ( i = 0; i < features_fixed.size(); i++) {
            Integer index = i + 1;
            fileWriter.append(index.toString());
            fileWriter.append(",");
            fileWriter.append(issue_key.get(features_fixed.get(i)));
            fileWriter.append(",");
            fileWriter.append(features_fixed.get(i).toString());
            fileWriter.append("\n");
         }

      } catch (Exception e) {
         System.out.println("Error in csv writer");
         e.printStackTrace();
      } finally {
         try {
            fileWriter.flush();
            fileWriter.close();
         } catch (IOException e) {
            System.out.println("Error while flushing/closing fileWriter !!!");
            e.printStackTrace(); 
         }
      }
      
      
      return;
   }
  	   
  	

  	 public static void addRelease(String strDate, String id) {
	      LocalDate date = LocalDate.parse(strDate);
	      LocalDateTime dateTime = date.atStartOfDay();
	      //if (!features_fixed.contains(dateTime))
	         features_fixed.add(dateTime);
	      issue_key.put(dateTime, id);
	      return;
	   }
 
}
