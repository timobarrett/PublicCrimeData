package incident.timo.com;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 * Created by tim on 9/7/2016.
 */
public abstract class ProcessIncidentData implements Serializable{

    protected int mCurrentYear = 0;
    protected String mClosetsLoc;
    protected double mCurLat = 0d;
    protected double mCurLon = 0d;
    protected HashMap<String,IncidentData>incidentMap;
    protected String[] params;
    private Logger mLogger;
    private String mQueryString;

    protected String INCIDENT_BASE_URL;
    protected String INCIDENT_PATH;
    protected String QUERY_PARAM_YEAR = "year";


    public ProcessIncidentData(String...param) {
        incidentMap = new HashMap<String,IncidentData>();
        mCurrentYear = Calendar.getInstance().get(Calendar.YEAR);
        mClosetsLoc = "";
        params = param;
    }


    //protected abstract void getIncidentData(String...params);
    protected abstract void processIncidentData(String jsonStr) throws JSONException;

    protected void getIncidentData(String...params){
        // protected void lookupIncidentData() {
        String cityName;
        BufferedReader reader = null;
        HttpURLConnection urlConnection = null;
        String incidentJsonStr = null;

        final String NEW_RECORD = "{";

        try {
            URIBuilder ubuilder = new URIBuilder()
                    .setScheme("https")
                    .setHost(INCIDENT_BASE_URL)
                    .setPath(INCIDENT_PATH)
                    .setCustomQuery(mQueryString);
            //                .addParameter(QUERY_PARAM_YEAR,Integer.toString(mCurrentYear));
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

    protected int getIncidentCount(){return incidentMap.size();}

    protected void setQueryString(String queryString){ mQueryString = queryString;}

    protected void setBaseUrl(String baseUrl){INCIDENT_BASE_URL = baseUrl;}

    protected void setIncidentPath(String incidentPath){INCIDENT_PATH = incidentPath;}

    protected void reportIncidentData(Logger logger, String cityName){
        mLogger = logger;
        IncidentData id;

        Iterator it = incidentMap.entrySet().iterator();

        mLogger.info("   Total Incidents reported = " + getIncidentCount()+ " For " + cityName);
        mLogger.info("-------------------------------------");

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            id = (IncidentData)pair.getValue();
            dumpTheEntry(id);
        }
    }

    protected void dumpTheEntry(IncidentData id) {
        StringBuilder sb = new StringBuilder();
        sb.append("Crime: " + id.getDescription());
        sb.append(" - " + id.getShortDesc());
        sb.append(" Address: " + id.getAddress());
        sb.append(" Date & Time = "+ id.getDateTime().replace("T","@"));
        if (id.getVictimCnt()!="N/A"){sb.append(" Victims = " + id.getVictimCnt());}
        if (!id.getGangRelated().contains("N/A")){sb.append(" Gang related = " + id.getGangRelated());}
        mLogger.info(sb.toString());
    }
}
