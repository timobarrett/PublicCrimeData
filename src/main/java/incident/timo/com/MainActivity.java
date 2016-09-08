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

    public static void main(String[] args){
        if (args.length < 1){
            usageDisplay();
        }else{
            MainActivity mainAct = new MainActivity();
            mainAct.processRequest(args);
            mainAct.incidentCity.reportIncidentData(LOGGER);
        }
    }

    private static void usageDisplay(){
        LOGGER.debug("FUCK YOU ASS WIPE");
        LOGGER.info("This application works for LA County and it's cities");
        LOGGER.info("as well as San Francisco and Seattle");
        LOGGER.info("");
        LOGGER.info("Example use: ");
        LOGGER.info("LA Burbank");
        LOGGER.info("Seattle");
        LOGGER.info("");
        LOGGER.info("Crime information is returned for the current year");
    }

    private boolean processRequest(String[] params){
        String targetCity = params[0].toLowerCase();
        if (targetCity.contains("san fran")){
            incidentCity = new ProcessIncidentDataSanFran(params);
        }else if (targetCity.contains("seattle")){
            incidentCity = new ProcessIncidentDataSeattle(params);
        } else{
            if (params[1].isEmpty()){
                LOGGER.error("The name of a city is LA County is required - exiting");
                return false;
            }
            incidentCity = new ProcessIncidentDataLA(params);
        }
        incidentCity.getIncidentData(params);
        return true;
    }


}
