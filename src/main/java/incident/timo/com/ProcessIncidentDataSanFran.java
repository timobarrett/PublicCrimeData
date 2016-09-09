package incident.timo.com;

import org.apache.http.client.utils.URIBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by tim on 9/8/2016.
 */
public class ProcessIncidentDataSanFran extends ProcessIncidentData {

    protected String INCIDENT_BASE_URL = "data.sfgov.org/resource/tmnf-yvry.json?";
    protected String INCIDENT_DATE_PORTION = "$where=date_trunc_y(date) > ";
    protected String INCIDENT_DAY_MONTH = "-12-31\"";

    public ProcessIncidentDataSanFran(String...params){
        super(params);
        super.setBaseUrl(INCIDENT_BASE_URL);
        super.setIncidentPath(INCIDENT_DATE_PORTION);
        super.setIncidentPath(INCIDENT_DATE_PORTION + ("\""+Integer.toString(mCurrentYear - 1)+INCIDENT_DAY_MONTH));
    }

    /**
     * process the json data from Get call
     * @param jsonStr
     * @throws JSONException
     */
    protected void processIncidentData(String jsonStr) throws JSONException{
        IncidentData incident = new IncidentData();
        String streetAddr = new String();
        incident.setLatitude(" ");
        incident.setLongitude(" ");

        JSONObject crimeJson = new JSONObject(jsonStr);
        incident.setDescription(crimeJson.getString("descript"));
        String dateStr = crimeJson.getString("date");
        incident.setDateTime(dateStr.replace("00:00",crimeJson.getString("time")));
        incident.setGangRelated("N/A");
        incident.setShortDesc(crimeJson.getString("category"));
        streetAddr = (crimeJson.getString("address"));
        incident.setAddress(streetAddr);
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
//        Log.d(LOG_TAG,"DONE PARSING - SHort Desc" + crimeJson.getString("category"));

        if(incidentMap.containsKey(streetAddr)){
            incidentMap.put(streetAddr+incident.getDateTime(),incident);
        }else {
            incidentMap.put(streetAddr, incident);
        }
    }
}

