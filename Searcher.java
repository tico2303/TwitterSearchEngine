import java.io.*;
import java.util.ArrayList;

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

public class Searcher{
    String indexDir = null ;
    IndexSearcher isearch;

    public static void main(String[] args) throws IOException, ParseException{
        //Parse Commandline arguments
        ParseSearch ps = new ParseSearch();
        ps.parse(args);
        String indexField = ps.indexField;
        String queryTerm = ps.query;
        System.out.println("Searching for: " + queryTerm);
        System.out.println("IndexField: " + indexField);
        
        //Constants
        Constants constants = new Constants();
        Searcher s = new Searcher();
    
        ArrayList<String> indexFieldList = constants.indexFieldList;
        System.out.println("indexFieldList: " +String.join(",",indexFieldList));

        //search
        TopDocs res = s.search( queryTerm, indexField);
        System.out.println("Total hits: " + res.totalHits);
        System.out.println("Top 10 Results: ");
        int count = 1;
        //iterate through the results and print the desired information
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
        TopDocs results = isearch.search(query, max_results);
        return results;
    }//end search
    public Document getDocument(ScoreDoc sd) throws IOException{
        return isearch.doc(sd.doc);

    }//end getDocument

}//endclass Searcher
