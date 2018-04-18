// some code/tutorials were used as references
// Source: tutorialspot.com

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


public class IndexCreator{
    private IndexWriter writer = null;
    private TwitterParser tweet = new TwitterParser();
    private String indexPath = null;
    private ArrayList<JSONObject> jsonTweetArray = new ArrayList<JSONObject>();

    //delete main when done trouble shooting
    public static void main(String[] args) throws IOException{

        //ic = new IndexCreator();
        System.out.println("IndexCreator main...");
        IndexCreator ic = new IndexCreator("/Users/roshi/cs172_info_retrieval/TwitterSearchEngine/indices");
        ic.createIndex();
    }
            
    //indexPath is the directory where the indexes will be stored (to search over later) 
    public IndexCreator(String indexPath)throws IOException {
        System.out.println("IndexPath is: " + indexPath);
        this.indexPath = indexPath;

        //creating the indexer
        File indexfile = new File(this.indexPath);
        Directory indexDir = (Directory)FSDirectory.open(indexfile.toPath());
        /*
        IndexWriterConfig indexConfig = new IndexWriterConfig(new StandardAnalyzer()); 
        writer = new IndexWriter(indexDir, indexConfig);
        */
    }
    //creates a document per tweet
    public Document getDocument(JSONObject json_obj) throws IOException{
        Document doc = new Document();
        //using twitter parser to extract fields from tweets
        tweet.setTweet(json_obj);

        //creating several fields to index the tweets
        if(tweet.getText() != null){
            Field text = new TextField("text",tweet.getText(), Field.Store.YES);
            doc.add(text);        
            }

        if(tweet.getTimeStamp() != null){
            Field timestamp = new StringField("timestamp",tweet.getTimeStamp(), Field.Store.YES);
            doc.add(timestamp);        
            }

        if(tweet.getUserLocation() != null){
            Field location = new StringField("location",tweet.getUserLocation(), Field.Store.YES);
            doc.add(location);        
            }
        if(tweet.getUserName() != null){
            Field username = new StringField("username",tweet.getUserName(), Field.Store.YES);
            doc.add(username);        
            }

        if(tweet.getUserScreenName() != null){
            Field userscreenname = new StringField("userscreenname",tweet.getUserScreenName(), Field.Store.YES);
            doc.add(userscreenname);        
            }


        if(tweet.getUserImageUrl() != null){
            Field userimageurl = new StringField("userimageurl",tweet.getUserImageUrl(), Field.Store.YES);
            doc.add(userimageurl);        
            }

        if(tweet.getText() != null){
            Field likedcount = new LongPoint("likedcount",tweet.getLikedCount());
            doc.add(likedcount);        
            }

        ArrayList<String> hash_tag_list = tweet.getHashTags();
        if(hash_tag_list.size() > 0){
            Iterator<String> hash_iter = hash_tag_list.iterator();
            while(hash_iter.hasNext()){
                String hash = hash_iter.next();
                System.out.println("adding Hashtag: " + hash + " to lucene doc");
                Field hashtags = new StringField("hashtags", hash, Field.Store.YES);
                doc.add(hashtags);
            }
        }
        // Field links = new Field("links",tweet.getLinks());

        //adding fields to document
        return doc;    
    }//end getDocument
    /*     
    public ArrayList<JSONObject> parseJson(){
        

    }
    */
    public void createIndex() throws IOException{
        //get list of all data files
        Data data = new Data();
        try{
            ArrayList<String> data_dirs = data.getData();
            for(String file : data_dirs){
                System.out.println(file);
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String line = null;

                //read all tweets in file
                while((line = bufferedReader.readLine()) != null) {
                    JSONObject obj = (JSONObject) new JSONParser().parse(line);
                    //for every tweet in file
                    JSONObject tweet_json_obj = new JSONObject(obj);
                    Document doc = this.getDocument(tweet_json_obj);
                    //this.writer.addDocument(doc);

                    }//end while
                }//end for
        }//end try
        catch(ParseException pe){
            System.out.println(pe);
        }


    }//end createIndex


}//endLuceneSearch




