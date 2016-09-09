package incident.timo.com;

import org.apache.log4j.*;

/**
 * Created by tim on 8/12/2016.
 */
public class MainActivity {

    //https://data.lacounty.gov/resource/ivnt-3nrc.json?
    /* aruguments to be
    *      city name
    *      county name
     */
    private static final Logger LOGGER = Logger.getLogger("GetIncidentInfo");
    private ProcessIncidentData incidentCity = null;
    private String mCityName;

    public static void main(String[] args){
        if (args.length < 1){
            usageDisplay();
        }else{
            MainActivity mainAct = new MainActivity();
            mainAct.processRequest(args);
            mainAct.incidentCity.reportIncidentData(LOGGER,mainAct.mCityName);
        }
    }

    private static void usageDisplay(){
        LOGGER.error("This application works for LA County and it's cities");
        LOGGER.error("as well as San Francisco, Seattle, Chicago and Boston");
        LOGGER.error("");
        LOGGER.error("Example use: ");
        LOGGER.error("LA Burbank");
        LOGGER.error("Seattle");
        LOGGER.error("\"san fran\"");
        LOGGER.error("");
        LOGGER.error("Crime information is returned for the current year");
        LOGGER.error("Data is written to the results folder.");
    }

    private boolean processRequest(String[] params){
        String targetCity = params[0].toLowerCase();
        if (targetCity.contains("san fran")){
            incidentCity = new ProcessIncidentDataSanFran(params);
            mCityName = "San Francisco";
        }else if (targetCity.contains("seattle")) {
            incidentCity = new ProcessIncidentDataSeattle(params);
            mCityName = "Seattle";
        }else if (targetCity.contains("chicago")) {
            incidentCity = new ProcessIncidentDataChicago(params);
            mCityName = "Chicago";
        }else if (targetCity.contains("boston")){
            incidentCity = new ProcessIncidentDataBoston(params);
            mCityName = "Boston";
        } else{
            if (params[1].isEmpty()){
                LOGGER.error("The name of a city is LA County is required - exiting");
                return false;
            }
            incidentCity = new ProcessIncidentDataLA(params);
            mCityName = params[1];
        }
        incidentCity.getIncidentData(params);
        return true;
    }


}
