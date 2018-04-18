// some code/tutorials were used as references
// Source: tutorialspot.com

import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

public class IndexCreator{
    private IndexWriter writer = null;
    private TwitterParser tweet = new TwitterParser();
    private String indexPath = null;
    private String jsonFilePath = null;
    private ArrayList<JSONObject> jsonTweetArray = new ArrayList<JSONObject>();

    //delete main when done trouble shooting
    public static void main(String[] args){

        //ic = new IndexCreator();
        System.out.println("IndexCreator main...");
    }
            
    //indexPath is the directory where the indexes will be stored (to search over later) 
    public IndexCreator(String indexPath, String jsonFilePath)throws IOException {
        System.out.println(indexPath);
        this.indexPath = indexPath;
        this.jsonFilePath = jsonFilePath;

        //creating the indexer
        StandardAnalyzer analyzer = new StandardAnalyzer(); 
        IndexWriterConfig indexConfig = new IndexWriterConfig(analyzer); 
        File indexfile = new File(this.indexPath);
        Directory indexDir = (Directory)FSDirectory.open(indexfile.toPath());
        writer = new IndexWriter(indexDir, indexConfig);
    }
    public Document getDocument(JSONObject json_obj) throws IOException{
        Document doc = new Document();

        //using twitter parser to extract fields from tweets
        tweet.setTweet(json_obj);

        //creating several fields to index the tweets
        Field text = new TextField("text",tweet.getText(), Field.Store.YES);
        Field timestamp = new StringField("timestamp",tweet.getTimeStamp(), Field.Store.YES);
        Field location = new StringField("location",tweet.getUserLocation(), Field.Store.YES);
        Field username = new StringField("username",tweet.getUserName(), Field.Store.YES);
        Field userscreenname = new StringField("userscreenname",tweet.getUserScreenName(), Field.Store.YES);
        Field userimageurl = new StringField("userimageurl",tweet.getUserImageUrl(), Field.Store.YES);
        Field likedcount = new LongPoint("likedcount",tweet.getLikedCount());
        
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
        doc.add(text);        
        doc.add(timestamp);        
        doc.add(location);        
        doc.add(username);        
        doc.add(userscreenname);        
        doc.add(userimageurl);        
        doc.add(likedcount);        
        return doc;    
    }//end getDocument
        
    public ArrayList<JSONObject> parseJson(){
        

    }

    public void createIndex(){

    }//end createIndex


}//endLuceneSearch




