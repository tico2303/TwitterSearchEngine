import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class TwitterParser{
    
    private JSONObject tweet;
    private JSONObject user;
    private boolean found_user = false;

    //setTweet sets the tweet that will then be parsed by the other getFunctions
    public void setTweet(JSONObject new_tweet){
        tweet = new_tweet;
        if(tweet.containsKey("user")){
            user = (JSONObject)tweet.get("user"); 
            found_user = true;
        }
    }
    //gets the Text of the tweet
    public String getText(){
        //possibly parse the hashtags out
        if(tweet.get("text") != null){
            return (String) tweet.get("text");
        }
        return "";
    }
    //gets the time tweet was created
    public String getTimeStamp(){
        return (String) tweet.get("created_at");
    }
    //get the 4 BoundingCooridinates of the tweet
    public JSONArray getBoundingCoordinates(){
        JSONArray coords = null;
       if(tweet.get("place") != null){
            //System.out.println("Found place.......");
            JSONObject place = (JSONObject)tweet.get("place");
            JSONObject bound_box =(JSONObject)place.get("bounding_box");
        
            coords = (JSONArray)bound_box.get("coordinates");
            coords = (JSONArray)coords.get(0);
            // there are four coordinates 
            /*
            JSONArray cord1 = (JSONArray)coords.get(0);
            JSONArray cord2 = (JSONArray)coords.get(1);
            JSONArray cord3 = (JSONArray)coords.get(2);
            JSONArray cord4 = (JSONArray)coords.get(3);
            //[lat,long]
            System.out.println(cord1.get(0));
            System.out.println(cord2);
            */
            return coords;
        } 
        return coords;
    }
    public String getFullCityName(){
        String cityName = null;
        if(tweet.get("place") != null){
            JSONObject place = (JSONObject)tweet.get("place");
            cityName = (String) place.get("full_name");
        }

        return cityName;
    }

    public String getUserName(){
        if(found_user){
            return (String) user.get("name");
        }
        else{
            return null;
        }
    
    }

    public String getUserScreenName(){
        if(found_user){
            return (String) user.get("screen_name");
        }
        else{
            return null;
        }
    }
    //gets the Url for the user's profile image
    public String getUserImageUrl(){
        if(found_user){
            return (String) user.get("profile_image_url_https");
        }
        else{
            return null;
        }
    }
    public Long getUserFriendCount(){
        if(found_user){
            return (Long) user.get("friends_count");
        }
        else{
            return null;
        }
    }
    public Long getUserFollwersCount(){
        if(found_user){
            return (Long) user.get("followers_count");
        }
        else{
            return null;
        }
    }
    //gets the links associated with the tweet
    public ArrayList<String> getLinks(){
        Set<String> linksList = new HashSet<String>();
        
        //check entities.urls 
        if(tweet.containsKey("retweeted_status")){
            JSONObject retweet = (JSONObject)tweet.get("retweeted_status");
            //check retweeted_status.entities.urls array
            if(retweet.containsKey("extended_tweet")){
                JSONObject extended = (JSONObject)retweet.get("extended_tweet");
                JSONObject entities = (JSONObject) extended.get("entities");
                JSONArray urls = (JSONArray)entities.get("urls");
                Iterator<?> url_iter = urls.iterator();
                while(url_iter.hasNext()){
                    JSONObject obj = (JSONObject)url_iter.next();
                    String expanded_url = (String) obj.get("expanded_url");
                    //System.out.println("Adding "+expanded_url+" to links list");
                    linksList.add(expanded_url);
                }//endwhile

                //iterate over urls get the strings
                if(entities.containsKey("media")){
                    JSONArray media = (JSONArray) entities.get("media");
                        Iterator<?> media_iter = media.iterator();
                        while(media_iter.hasNext()){
                            JSONObject obj = (JSONObject)media_iter.next();
                            String medurl = (String)obj.get("media_url");
                            String expurl = (String)obj.get("expanded_url");
                            String url = (String)obj.get("url");
                            //System.out.println("adding "+expurl+ " to links list\n");
                            //System.out.println("adding "+medurl+ " to links list\n");
                            //System.out.println("adding "+url+ " to links list\n");
                            linksList.add(expurl);
                            linksList.add(medurl);
                            linksList.add(url);
                        }//endwhile
                }//endifentities
            }//end extended_tweet
        }//endiftweet

        //}//endiftweet 
        
        return new ArrayList<String>(linksList); 
    }

    public String getUserLocation(){
        if(found_user){
            return (String)user.get("location");
        }
        else{
            return null;
        }
    }
    //gets list of Hashtags
    public ArrayList<String> getHashTags(){
        Set<String> hash_tags_list = new HashSet<String>();
        JSONArray hash_tags = null;

        String[] text = getText().split(" ");
        for(String t :text){
            if(t.length() > 0){
                if(t.charAt(0) == '#'){
                    hash_tags_list.add(t.replace("#",""));
                }
            }
        }

        if(tweet.containsKey("entities")){
            JSONObject entit = (JSONObject)tweet.get("entities");
            hash_tags = (JSONArray) entit.get("hashtags"); 
            Iterator<?> iter = hash_tags.iterator();
            if(!hash_tags.isEmpty()){
                while(iter.hasNext()){
                    //System.out.println(iter.next());
                    JSONObject ht = (JSONObject)iter.next();
                    String htag =(String) ht.get("text");
                    hash_tags_list.add(htag);
                }
            }

        }//endif entities
        if(tweet.containsKey("extended_tweet")){
            JSONObject entit = (JSONObject)tweet.get("entities");
            hash_tags = (JSONArray) entit.get("hashtags"); 
            Iterator<?> iter = hash_tags.iterator();
            if(!hash_tags.isEmpty()){
                while(iter.hasNext()){
                    //System.out.println(iter.next());
                    JSONObject ht = (JSONObject)iter.next();
                    String htag =(String) ht.get("text");
                    hash_tags_list.add(htag);
                }
            }
        }//endif extended_tweet
        //convert Set to ArrayList
        return new ArrayList<String>(hash_tags_list);        
    }

    public Long getLikedCount(){
        return (Long)tweet.get("favorite_count");
    }
    private ArrayList<String> parseForHashtags(){
        ArrayList<String> hashtags = new ArrayList<String>();
        //parse tweets for hashtags

        return hashtags;
    }
}//endTwitterParser
