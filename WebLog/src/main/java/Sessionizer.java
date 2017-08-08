/**
 * @author ankit on 8/7/17.
 * @project WeblogChallengeAnkitArya
 */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

public class Sessionizer {

    public static void main(String args[]) {

        // Temporary path for testing purposes
        String logPath = "WebLog/src/main/java/sample.log";

        // Sessionizer Object instantiation for calling different methods in the class
        Sessionizer sn = new Sessionizer();

        // This map contains the URLs hit or are aggregated from a particular IP address/session
        Map<String, Set<String>> urlAggregator = new HashMap<String, Set<String>>();

        // This map contains the distinct URL count hit from a particular IP address/session
        Map<String, Integer> uniqueURLCounter = new HashMap<String, Integer>();

        // This map contains the sum of all the request/response/processing times for respective session/IP address
        Map<String, List<Double>> sessionTime = new HashMap<String, List<Double>>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Random start minute to analyze data between arbitrary time range
        int startMinute = 2;

        // Random start hour to analyze data between arbitrary time range
        int startHour = 18;

        try{

            FileInputStream fstream = new FileInputStream(logPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;

            /* read log line by line */
            while ((strLine = br.readLine()) != null) {
                /* parse strLine to obtain what you want */
                String[] arr = strLine.split("\\s+");
                String[] dateTimer = arr[0].split("T|:|\\.");

                Date fixedDate = sdf.parse("2015-07-22");

                Date currentDate = sdf.parse(dateTimer[0]);
                //System.out.println(Integer.parseInt(dateTimer[2]));
                int lowerTimeRange = Integer.parseInt(dateTimer[2]);
                int hour = Integer.parseInt(dateTimer[1]);

                /* Current random time range is taken as 5 minutes for testing.
                   Comparing the dates and the hours as well to get a feasible time range.
                   And analyze the logs that fall in that range only.
                */
                if ((currentDate.compareTo(fixedDate) == 0) && (hour == startHour) && (lowerTimeRange > startMinute) && (lowerTimeRange - startMinute <= 5)) {
                    urlAggregator = sn.extractOptions(strLine, sessionTime, arr);
                }
            }
            br.close();

            uniqueURLCounter = sn.getUniqueUrlCounter(urlAggregator);
            Double avgSessionTime = sn.getAverageSessionTime(sessionTime);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

    }

    // The method below is responsible for giving us the URLs aggregated per IP address/session and returned as a Map.
    // Also, this computes the total session time for a URL related to a particular IP address/session and store it in another Map called 'sessionTime'.
    public Map<String, Set<String>> extractOptions(String strLine, Map<String, List<Double>> sessionTime, String[] arr) {

        Map<String,Set<String>> map = new HashMap<String, Set<String>>();

        if(!(map.containsKey(arr[2]))) {
            map.put(arr[2], new HashSet<String>());
        }

        map.get(arr[2]).add(arr[12]);

        if(!(sessionTime.containsKey(arr[2]))) {
            sessionTime.put(arr[2], new ArrayList<Double>());
        }

        sessionTime.get(arr[2]).add(Double.parseDouble(arr[4])+Double.parseDouble(arr[5])+Double.parseDouble(arr[6]));

        return map;
    }

    // This method is responsible for returning the unique URL counter for each session/IP address in a Map.
    public Map<String, Integer> getUniqueUrlCounter(Map<String, Set<String>> urlAggregator) {

        Map<String, Integer> map = new HashMap<String, Integer>();

        for(String ip : urlAggregator.keySet()) {
            map.put(ip, urlAggregator.get(ip).size());
        }

        return map;
    }

    // This method is returning the Average session Time.
    // Also, it calculates the IP address of the user with longest session in a variable called 'ipLongestSession'
    public double getAverageSessionTime(Map<String, List<Double>> sessionTime) {
        double totalSessionTime = 0, avergeSessionTime = 0;
        Map<Double, String> longestSession = new HashMap<Double, String>();

        for(String ip : sessionTime.keySet()) {
            double ipSessionTime = 0;
            for(Double st : sessionTime.get(ip)) {
                totalSessionTime += st;
                ipSessionTime += st;
            }

            // setting up the Map for retrieving the IP address with longest session.
            longestSession.put(ipSessionTime, ip);
        }

        // Calculating the Average session time by dividing the total session time with number of IP addresses/sessions.
        avergeSessionTime = totalSessionTime / sessionTime.size();

        double temp = 0;
        Map<Double, String> longest = new HashMap<Double, String>();

        for(Double ipSession : longestSession.keySet()) {
            if(ipSession > temp) {
                temp = ipSession;
            }

            String ipLongestSession = longestSession.get(temp);
        }

        return avergeSessionTime;
    }

}

