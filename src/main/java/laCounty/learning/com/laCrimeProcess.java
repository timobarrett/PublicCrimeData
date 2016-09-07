package laCounty.learning.com;

    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.net.URISyntaxException;
    import java.net.URI;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.List;

    import org.apache.commons.httpclient.HttpClient;
    import org.apache.commons.httpclient.HttpException;
    import org.apache.commons.httpclient.methods.GetMethod;
    import org.apache.http.HttpStatus;
    import org.apache.http.client.utils.URIBuilder;
    import org.json.simple.JSONArray;
    import org.json.simple.JSONObject;
    import org.json.simple.parser.JSONParser;
    import org.json.simple.parser.ParseException;
/**
 * Created by tim on 8/12/2016.
 */
public class laCrimeProcess {

    //https://data.lacounty.gov/resource/ivnt-3nrc.json?
    protected static String LA_CRIME_URL = "data.lacounty.gov";
    protected static String LA_CRIME_PATH = "/resource/ivnt-3nrc.json";
    protected static String CRIME_YEAR = "crime_year";
    protected static String CITY = "city";
    protected static String GANG = "gang_related";
    protected int mGangCount = 0;

    public static void main(String[] args){

        laCrimeProcess laCrime = new laCrimeProcess();
        ArrayList<String> foo = laCrime.getCrimeData(args[0],args[1]);

//        if (foo.size() > 0){
//            laCrime.processCrimes(foo);
//        }
    }

    protected ArrayList<String> getCrimeData(String... params){
        ArrayList<String> results;
        URI crimUri = buildCrimeUrl(params[0],params[1]);
        results = restGet(crimUri.toString());
        System.out.println("Number of crimes = "+results.size());
        System.out.println("Gang related crimes = "+mGangCount);
        return results;
    }
    protected URI buildCrimeUrl(String... param){
        URIBuilder builtUri;
        URI uri = null;
        try{
            builtUri = new URIBuilder()
                    .setScheme("https")
                    .setHost(LA_CRIME_URL)
                    .setPath(LA_CRIME_PATH)
                    .addParameter(CRIME_YEAR, param[0])
                    .addParameter(CITY, param[1]);
            uri = builtUri.build();
        } catch (URISyntaxException u) {
            System.out.println("ERROR - URI EXCEPTION");
        }
        return uri;
    }
    protected ArrayList<String> restGet (String getString){
        System.out.println("restGet Method " + getString);
        GetMethod getMethod = new GetMethod(getString);
        String readLine;
        HttpClient httpClient = new HttpClient();
        List<String> getResult = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        String delim = "[:]";

        try {
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                System.out.println("ERROR with Rest Get Command - " + getMethod.getResponseBodyAsString());
            } else {
                System.out.println("Get status code = " + statusCode);
                BufferedReader br = new BufferedReader(new InputStreamReader(getMethod.getResponseBodyAsStream()));

                while ((readLine = br.readLine()) != null) {
        //            System.out.println("READLINE = " + readLine);
                    if (readLine.toString().contains("gang_related") && readLine.toString().contains("Y")){
                        mGangCount++;
                    }
                    if(readLine.toString().contains("street")){
                        sb.append(readLine.toString().substring(readLine.indexOf(":")+1,readLine.length()));
                    }
                    if (readLine.toString().contains("statistical_code_description")) {
                        sb.append(readLine.toString().substring(readLine.indexOf(":")+1,readLine.length()));
                    }
                    if (readLine.toString().contains("crime_date")){
                        sb.append(readLine.toString().substring(readLine.indexOf(":")+1,readLine.length()));
                        System.out.println(sb.toString());
                        getResult.add(sb.toString());
                        sb.delete(0,sb.length());
                    }
                }

            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            getMethod.releaseConnection();
        }
        return (ArrayList<String>) getResult;
    }
}
