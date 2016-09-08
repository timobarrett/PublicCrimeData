package incident.timo.com;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;

/**
 * Created by tim on 9/8/2016.
 */
public class ProcessIncidentDataSeattle extends ProcessIncidentData {
    public ProcessIncidentDataSeattle(String...params){
        super(params);
    }

    protected void getIncidentData(String...params){
        // protected void lookupIncidentData() {
        String cityName;
        BufferedReader reader = null;
        HttpURLConnection urlConnection = null;
        String incidentJsonStr = null;
        final String INCIDENT_BASE_URL = "data.seattle.gov";
        final String INCIDENT_PATH = "/resource/7ais-f98f.json";
        final String QUERY_PARAM_YEAR = "year";
        final String NEW_RECORD = "{";

        try {
            URIBuilder ubuilder = new URIBuilder()
                    .setScheme("https")
                    .setHost(INCIDENT_BASE_URL)
                    .setPath(INCIDENT_PATH)
                    .addParameter(QUERY_PARAM_YEAR,Integer.toString(mCurrentYear));
            URI incidentUri = ubuilder.build();
            // Construct the URL for the OpenWeatherMap query
            URL incidentUrl = new URL(incidentUri.toString());
            System.out.println("URL generated = "+incidentUrl);
           // Log.i(LOG_TAG, "URL Built -" + incidentUrl.toString());

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
               //         Log.d(LOG_TAG, "EXCEPTION - JSON processing " + e.getLocalizedMessage());
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
           // Log.e(LOG_TAG, "Error ", e.getCause());
            // If the code didn't successfully get the incident data, there's no point in attempting
            // to parse it.
            return;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
               //     Log.e(LOG_TAG, "Error closing stream", e.getCause());
                }
            }//Log.d(LOG_TAG,"MAP COUNT = "+ incidentMap.size());
        }
        return;
    }

    /**
     * process the incident data returned
     * @param jsonStr
     * @throws JSONException
     */
    protected void processIncidentData(String jsonStr) throws JSONException{
        IncidentData incident = new IncidentData();
        String streetAddr = new String();
        incident.setLatitude(" ");
        incident.setLongitude(" ");

        JSONObject crimeJson = new JSONObject(jsonStr);
        incident.setDescription(crimeJson.getString("offense_type"));
        incident.setDateTime(crimeJson.getString("date_reported"));
        incident.setGangRelated("N/A");
        incident.setShortDesc(crimeJson.getString("summarized_offense_description"));
        streetAddr = (crimeJson.getString("hundred_block_location"));
        incident.setAddress(streetAddr.replace("XX","00"));
        incident.setVictimCnt("N/A");
        //  JSONArray locInfo = (JSONArray)crimeJson.get("geo_crime_location");
        if (jsonStr.contains("latitude")) {
            JSONObject locObj = (JSONObject) crimeJson.get("location");
            incident.setLatitude(locObj.getString("latitude"));
        }
        if (jsonStr.contains("longitude")) {
            JSONObject locObj = (JSONObject) crimeJson.get("location");
            incident.setLongitude(locObj.getString("longitude"));
        }
       // Log.d(LOG_TAG,"DONE PARSING - SHort Desc" + crimeJson.getString("summarized_offense_description"));

        if(incidentMap.containsKey(streetAddr)){
            incidentMap.put(streetAddr+incident.getDateTime(),incident);
        }else {
            incidentMap.put(streetAddr, incident);
        }
    }

}
