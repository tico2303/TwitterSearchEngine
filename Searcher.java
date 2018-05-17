import java.io.*;
import java.util.ArrayList;
import java.io.FileWriter;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;

import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;

import org.apache.lucene.store.Directory;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import utils.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class Searcher{
    private String indexDir = null ;
    IndexSearcher isearch;
    private TopDocs results = null;

    public static void main(String[] args) throws IOException, ParseException{
        //Parse Commandline arguments
        ParseSearch ps = new ParseSearch();
        ps.parse(args);
        String indexField = ps.indexField;
        String queryTerm = ps.query;
        //System.out.println("Searching for: " + queryTerm);
        //System.out.println("IndexField: " + indexField);
        
        //Constants
        Constants constants = new Constants();
        Searcher s = new Searcher();
        ArrayList<String> indexFieldList = constants.indexFieldList;
        //System.out.println("indexFieldList: " +String.join(",",indexFieldList));

        //search
        TopDocs res = s.search( queryTerm, indexField);
        //System.out.println("Total hits: " + res.totalHits);
        //System.out.println("Top 10 Results: ");
        int count = 1;
        //iterate through the results and print the desired information
        /*
        for(ScoreDoc sd: res.scoreDocs){
            Document d = s.getDocument(sd); 
            System.out.println(count);
            count +=1;
             System.out.println("Tweet: " + d.get("text")); 
             System.out.println("Hashtags: " + d.get("hashtags")); 
             System.out.println("LikedCount: " + d.get("likedcount")); 
             System.out.println("userImageUrl: " + d.get("userimageurl")); 
             System.out.println("username: " + d.get("username")); 
             System.out.println("timestamp: " + d.get("timestamp")); 
             System.out.println("\n\n");
        }
        */
            System.out.println(s.resultsToJson());
    }//end main
    public TopDocs search(String searchQuery, String indexField) throws IOException, ParseException{
        //get directory of lucene indices to search over
        this.indexDir = new File(".").getCanonicalPath()+ "/indices/";
        Directory index_dir = FSDirectory.open( new File(this.indexDir).toPath() );
        IndexReader reader = DirectoryReader.open(index_dir); 


        isearch = new IndexSearcher(reader); 
        //change Query Parser parameter based on field criteria (user selected)
        QueryParser qparser = new QueryParser(indexField, new StandardAnalyzer() );
        Query query = qparser.parse(searchQuery);
        
        //return all results and display them on sepearate pages
        int max_results = 99999999;
        this.results = isearch.search(query, max_results);
        //System.out.println("Done processing search.. posting Results to flask server");
        //postResults();
        return this.results;
    }//end search

    public Document getDocument(ScoreDoc sd) throws IOException{
        return isearch.doc(sd.doc);

    }//end getDocument


    public String resultsToJson()throws IOException{

        JSONArray resultsArray = new JSONArray();
        if (this.results != null) {
            //System.out.println("Preparing Results for post");
            ScoreDoc[] hits = this.results.scoreDocs;
            for(int rank = 0; rank < hits.length; rank++){
                JSONObject obj = new JSONObject();
                Document hitdoc = getDocument(hits[rank]);
                obj.put("rank", (rank +1));
                obj.put("score", hits[rank].score);
                obj.put("text", hitdoc.get("text"));
                obj.put("username", hitdoc.get("username"));
                obj.put("timestamp", hitdoc.get("timestamp"));
                obj.put("hashtags", hitdoc.get("hashtags"));
                obj.put("links", hitdoc.get("links"));
                obj.put("userimageurl", hitdoc.get("userimageurl"));
                obj.put("likedcount", hitdoc.get("likedcount"));
                obj.put("location", hitdoc.get("location"));
                obj.put("coords", hitdoc.get("coords"));
                resultsArray.add(obj);
            }
        }
        JSONObject res = new JSONObject();
        res.put("results",resultsArray);
        return res.toString();
    }

    public void postResults() throws IOException{
        JSONArray resultsArray = new JSONArray();
        if (this.results != null) {
            //System.out.println("Preparing Results for post");
            ScoreDoc[] hits = this.results.scoreDocs;
            for(int rank = 0; rank < hits.length; rank++){
                JSONObject obj = new JSONObject();
                Document hitdoc = getDocument(hits[rank]);
                obj.put("rank", (rank +1));
                obj.put("score", hits[rank].score);
                obj.put("text", hitdoc.get("text"));
                obj.put("username", hitdoc.get("username"));
                obj.put("timestamp", hitdoc.get("timestamp"));
                obj.put("hashtags", hitdoc.get("hashtags"));
                obj.put("links", hitdoc.get("links"));
                resultsArray.add(obj);
            }
            //System.out.println("Posting Results");
            try{
                String dir = new File(".").getCanonicalPath()+ "/results";
                //System.out.println(dir);
                FileWriter file = new FileWriter(dir);
                JSONObject res = new JSONObject();
                res.put("results",resultsArray);
                file.write(res.toString());
            }
            catch(IOException e){
                System.out.println("oops couldn't write results ");
            }

            //WebPoster.sendResults(resultsArray);
            //System.out.println("Done Posting Results");
        }
    }
}//endclass Searcher
