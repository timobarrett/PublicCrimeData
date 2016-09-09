package incident.timo.com;

import org.json.JSONException;

import java.io.Serializable;
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

    public ProcessIncidentData(String...param) {
        incidentMap = new HashMap<String,IncidentData>();
        mCurrentYear = Calendar.getInstance().get(Calendar.YEAR);
        mClosetsLoc = "";
        params = param;
    }


    protected abstract void getIncidentData(String...params);

    protected abstract  void processIncidentData(String jsonStr) throws JSONException;// throws JsonException;

    protected int getIncidentCount(){return incidentMap.size();}

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
