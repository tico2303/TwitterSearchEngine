// some code/tutorials were used as references // Source: tutorialspot.com

import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.util.Iterator;

//json manipulation libraries
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//lucene packages
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field;
// import org.apache.lucene.document.FieldType;
//import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import utils.*;

public class IndexCreator{
    private IndexWriter writer = null;
    private TwitterParser tweet = new TwitterParser();
    private String indexPath = null;
    private ArrayList<JSONObject> jsonTweetArray = new ArrayList<JSONObject>();
    public ArrayList<String> indexFieldList = new ArrayList<String>();

    //delete main when done trouble shooting
    public static void main(String[] args) throws IOException{

        //ic = new IndexCreator();
        System.out.println("IndexCreator main...");
        IndexCreator ic = new IndexCreator();
        ic.createIndex();
    }
            
    //indexPath is the directory where the indexes will be stored (to search over later) 
    public IndexCreator()throws IOException {
        //Find path where indices are stored 
        this.indexPath = new File(".").getCanonicalPath()+ "/indices/";
        Directory indexDir = FSDirectory.open( new File(this.indexPath).toPath() );
        System.out.println("IndexPath is: " + indexPath);

        //creating the indexWriter
        IndexWriterConfig indexConfig = new IndexWriterConfig( new StandardAnalyzer()); 
        writer = new IndexWriter(indexDir, indexConfig);
    }
    //creates a document per tweet
    public Document getDocument(JSONObject json_obj) throws IOException{
        Document doc = new Document();
        //using twitter parser to extract fields from tweets
        tweet.setTweet(json_obj);

        //creating several fields to index the tweets
        if(tweet.getText() != null){
            String index = "text";
            Field text = new TextField(index,tweet.getText(), Field.Store.YES);
            doc.add(text);        
            this.addToIndexFieldList(index);
            }

        if(tweet.getTimeStamp() != null){
            // System.out.println("adding timestamp to document");
            String index = "timestamp";
            Field timestamp = new StringField(index,tweet.getTimeStamp(), Field.Store.YES);
            doc.add(timestamp);        
            this.addToIndexFieldList(index);
            }

        if(tweet.getUserLocation() != null){
            System.out.println("adding location to document");
            String index = "location";
            Field location = new StringField(index,tweet.getUserLocation(), Field.Store.YES);
            doc.add(location);        
            this.addToIndexFieldList(index);
            }
        if(tweet.getBoundingCoordinates() != null){
            System.out.println("adding coords to document");
            String index = "coords";
            Field location = new StringField(index,tweet.getBoundingCoordinates(), Field.Store.YES);
            doc.add(location);        
            this.addToIndexFieldList(index);
        }
        if(tweet.getUserName() != null){
            // System.out.println("adding username to document");
            String index = "username";
            Field username = new StringField(index,tweet.getUserName(), Field.Store.YES);
            doc.add(username);        
            this.addToIndexFieldList(index);
            }

        if(tweet.getUserScreenName() != null){
            // System.out.println("adding userscreenname to document");
            String index = "userscreenname";
            Field userscreenname = new StringField(index,tweet.getUserScreenName(), Field.Store.YES);
            doc.add(userscreenname);        
            this.addToIndexFieldList(index);
            }

        if(tweet.getUserImageUrl() != null){
            // System.out.println("adding userimageurl to document");
            String index = "userimageurl";
            Field userimageurl = new StringField(index,tweet.getUserImageUrl(), Field.Store.YES);
            doc.add(userimageurl);        
            this.addToIndexFieldList(index);
            }

        if(tweet.getLikedCount() != null){
            // System.out.println("adding LikedCount to document");
            String index = "likedcount";
            Field likedcount = new LongPoint(index,tweet.getLikedCount());
            doc.add(likedcount);        
            this.addToIndexFieldList(index);
            }
        //Adding hastags to document
        ArrayList<String> hash_tag_list = tweet.getHashTags();
        if(hash_tag_list.size() > 0){
            Iterator<String> hash_iter = hash_tag_list.iterator();
            while(hash_iter.hasNext()){
                String hash = hash_iter.next();
                // System.out.println("Hashtag: " + hash);
                String index = "hashtags";
                Field hashtags = new StringField(index, hash, Field.Store.YES);
                doc.add(hashtags);
                this.addToIndexFieldList(index);
            }//end while
        }
        //Adding links to document
        ArrayList<String> links_list = tweet.getLinks();
        if(links_list.size() > 0){
            Iterator<String> link_iter = links_list.iterator();
            while(link_iter.hasNext()){
                String link = link_iter.next();
                // System.out.println("Link: " +link);
                String index = "links";
                doc.add(new StringField(index,link,Field.Store.YES));
                this.addToIndexFieldList(index);
            }//endwhile


        }//endif links_list.size()

        //adding fields to document
        System.out.println("Done Indexing Tweet..");
        // System.out.println("indexFieldList: " + String.join(",", this.indexFieldList));
        return doc;    
    }//end getDocument

    public void createIndex() throws IOException{
        //get list of all data files
        Data data = new Data();
        try{
            ArrayList<String> data_dirs = data.getData();
            //for every file in data directory read the file
            for(String file : data_dirs){
                System.out.println(file);
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String line = null;

                //for every tweet in file get a Document for that tweet
                while((line = bufferedReader.readLine()) != null) {
                    JSONObject obj = (JSONObject) new JSONParser().parse(line);
                    JSONObject tweet_json_obj = new JSONObject(obj);
                    Document doc = this.getDocument(tweet_json_obj);
                    this.writer.addDocument(doc);
                    }//end while

                }//end for
            //close the IndexWriter or will not write index correctly (making it unsearchable)
            this.writer.close();
        }//end try
        catch(ParseException pe){
            System.out.println(pe);
        }
    }//end createIndex

    private void addToIndexFieldList(String index){
        if(!this.indexFieldList.contains(index)){
            this.indexFieldList.add(index);
            }
        }//end addToIndexFieldList

}//end IndexCreator





