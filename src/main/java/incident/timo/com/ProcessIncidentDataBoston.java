package incident.timo.com;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by tim on 9/9/2016.
 */
public class ProcessIncidentDataBoston extends ProcessIncidentData {

    final String INCIDENT_BASE_URL = "data.cityofboston.gov";
    final String INCIDENT_PATH = "/resource/fqn4-4qap.json";
    final String QUERY_PARAM_YEAR = "year";

    public ProcessIncidentDataBoston(String...params){
        super (params);
        super.setBaseUrl(INCIDENT_BASE_URL);
        super.setIncidentPath(INCIDENT_PATH);
        super.setQueryString(QUERY_PARAM_YEAR + "=" + mCurrentYear);
    }


    protected void processIncidentData(String jsonStr) throws JSONException{
        IncidentData incident = new IncidentData();
        String streetAddr = new String();
        incident.setLatitude(" ");
        incident.setLongitude(" ");

        JSONObject crimeJson = new JSONObject(jsonStr);
        incident.setDescription(crimeJson.getString("offense_description"));
        incident.setDateTime(crimeJson.getString("occurred_on_date"));
        incident.setGangRelated("N/A");
        incident.setShortDesc(crimeJson.getString("offense_code_group"));
        streetAddr = crimeJson.getString("street");
        incident.setAddress(streetAddr);
        incident.setVictimCnt("N/A");
        //  JSONArray locInfo = (JSONArray)crimeJson.get("geo_crime_location");
        incident.setLatitude(crimeJson.getString("lat"));
        incident.setLongitude(crimeJson.getString("long"));
        // Log.d(LOG_TAG,"DONE PARSING - SHort Desc" + crimeJson.getString("summarized_offense_description"));
        if(incidentMap.containsKey(streetAddr)){
            incidentMap.put(streetAddr+incident.getDateTime(),incident);
        }else {
            incidentMap.put(streetAddr, incident);
        }
    }
}
