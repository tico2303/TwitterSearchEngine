import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;

public class file_read {
    public static void main(String [] args)throws IOException {
        //array of JSONObjects(tweets)
        ArrayList<JSONObject> json = new ArrayList<JSONObject>();
        JSONObject json_obj;
        String basePath = new File("").getAbsolutePath();
        basePath += "/data/";
        //String fileName = "/Users/roshi/cs172_info_retrieval/java_twitter_search_engine/data/data2.json";
        String fileName = basePath +"data2.json";

        System.out.println(fileName);
        System.in.read();

    try {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        String line = null;

        //read all tweets in file
        ArrayList<String> globalLinks = new ArrayList<String>();
        while((line = bufferedReader.readLine()) != null) {
            JSONObject obj = (JSONObject) new JSONParser().parse(line);
            json_obj = new JSONObject(obj);
            json.add(json_obj);
            TwitterParser twt = new TwitterParser();
            twt.setTweet(json_obj); 
            
            System.out.println("Text: " +twt.getText()); 
            System.out.println("TimeStamp: " +twt.getTimeStamp()); 
            System.out.println("UserName: " +twt.getUserName()); 
            System.out.println("UserScreenName: " +twt.getUserScreenName()); 
            System.out.println("UserImageUrl: " +twt.getUserImageUrl()); 
            System.out.println("UserLocation: " +twt.getUserLocation()); 
            System.out.println("Coordinates: " +twt.getBoundingCoordinates()); 
            System.out.println("FullCityName: " +twt.getFullCityName()); 
            System.out.println("User friends count: " +twt.getUserFriendCount()); 

            ArrayList<String> hashtags = twt.getHashTags(); 
            System.out.println("hashtags: ");
            for(String i : hashtags){
                System.out.println(i);
            }
            System.out.println("Links: ");
            ArrayList<String> links = twt.getLinks();
            for(String i : links){
                System.out.println(i);
                globalLinks.add(i);
            }
            Long like_count = twt.getLikedCount();
            System.out.println("Liked count: "+like_count);
            
        System.out.println("\n\n\n");
        }//endwhile
        System.out.println("*********** all global links for document *********");
        for(String i : globalLinks){
            System.out.println(i);
        }
        //close file
        bufferedReader.close();         
    }//endtry
    catch(FileNotFoundException ex) {
        System.out.println("Can't open file: " + fileName);                
    }
    catch(IOException ex) {
        ex.printStackTrace();

    } catch (ParseException e) {
        e.printStackTrace();
    }
}


}
