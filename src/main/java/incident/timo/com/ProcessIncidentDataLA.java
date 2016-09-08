package incident.timo.com;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
//import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tim on 9/7/2016.
 */
public class ProcessIncidentDataLA extends ProcessIncidentData{

    public ProcessIncidentDataLA(String...params) {
        super(params);
    }

    protected void getIncidentData(String...params){

        BufferedReader reader = null;
        HttpURLConnection urlConnection = null;
        String incidentJsonStr = null;
        final String INCIDENT_BASE_URL = "data.lacounty.gov";
        final String INCIDENT_PATH =     "/resource/ivnt-3nrc.json";
        final String QUERY_PARAM_YEAR = "crime_year";
        final String QUERY_PARAM_CITY = "city";
        final String NEW_RECORD = "{";

        String cityName = params[1];
        List<NameValuePair> getParams = new ArrayList<>();
        getParams.add(new BasicNameValuePair(QUERY_PARAM_YEAR,Integer.toString(mCurrentYear)));
        getParams.add(new BasicNameValuePair(QUERY_PARAM_CITY, cityName));
        try {
            URIBuilder ubuilder = new URIBuilder()
                    .setScheme("https")
                    .setHost(INCIDENT_BASE_URL)
                    .setPath(INCIDENT_PATH)
                    .addParameters(getParams);

            URI incidentUri = ubuilder.build();     // Construct the URL for the OpenWeatherMap query
            URL incidentUrl = new URL(incidentUri.toString());
            System.out.println("URL generated = "+incidentUrl);
         //   Log.i(LOG_TAG, "URL Built -" + incidentUrl.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) incidentUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                //return "No Data";
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            //look for a terminator for entry and save class why a class - handle multiple instance lon and lat
            while ((line = reader.readLine()) != null) {
                if (line.toString().contains("[ {")) {
                    buffer.append(NEW_RECORD);
                } else if (line.toString().contains(", {")) {
                    try {
                        processIncidentData(buffer.toString());
                        buffer.delete(0, buffer.length());
                        buffer.append(NEW_RECORD);
                    } catch (JSONException e) {
           //             Log.d(LOG_TAG, "EXCEPTION - JSON processing " + e.getLocalizedMessage());
                    }
                } else {
                    buffer.append(line);
                }
            }
            if (buffer.length() > 0) {
                try {
                    processIncidentData(buffer.toString());
                } catch (JSONException e) {
             //       Log.d(LOG_TAG, "BUFFER LENGTH = " + buffer.length() + "BUFFER = " + buffer.toString());
             //       Log.d(LOG_TAG, "EXCEPTION2 - JSON processing " + e.getLocalizedMessage());
                }
            }
        } catch (IOException | URISyntaxException e) {
            //Log.e(LOG_TAG, "Error ", e.getCause());
            // If the code didn't successfully get the incident data, there's no point in attempting
            // to parse it.
            return ;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
              //      Log.e(LOG_TAG, "Error closing stream", e.getCause());
                }
            }
           // Log.d(LOG_TAG,"MAP COUNT = "+ incidentMap.size());
        }
    }

    protected void processIncidentData(String jsonStr) throws JSONException{
        IncidentData incident = new IncidentData();
        String streetAddr = new String();
        incident.setLatitude(" ");
        incident.setLongitude(" ");

        JSONObject crimeJson = new JSONObject(jsonStr);

        incident.setDescription(crimeJson.getString("statistical_code_description"));
        incident.setDateTime(crimeJson.getString("crime_date"));
        incident.setGangRelated(crimeJson.getString("gang_related"));
        incident.setShortDesc(crimeJson.getString("crime_category_description"));
        streetAddr = (crimeJson.getString("street"));
        incident.setAddress(streetAddr);
        incident.setVictimCnt(crimeJson.getString("victim_count"));
        //  JSONArray locInfo = (JSONArray)crimeJson.get("geo_crime_location");
        if (jsonStr.contains("latitude")) {
            JSONObject locObj = (JSONObject) crimeJson.get("geo_crime_location");
            incident.setLatitude(locObj.getString("latitude"));
        }
        if (jsonStr.contains("longitude")) {
            JSONObject locObj = (JSONObject) crimeJson.get("geo_crime_location");
            incident.setLongitude(locObj.getString("longitude"));
        }
   //     Log.d(LOG_TAG,"DONE PARSING - SHort Desc" + crimeJson.getString("crime_category_description"));

        if(incidentMap.containsKey(streetAddr)){
            incidentMap.put(streetAddr+incident.getDateTime(),incident);
        }else {
            incidentMap.put(streetAddr, incident);
        }
    }
}
