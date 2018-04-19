import java.io.*;
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

public class Searcher{
    String indexDir = null ;
    IndexSearcher isearch;

    public static void main(String[] args) throws IOException, ParseException{
        Searcher s = new Searcher();
        TopDocs res = s.search( "ai");
        System.out.println("Total hits: " + res.totalHits);
        System.out.println("Top 10 Results: ");
        int count = 1;
        for(ScoreDoc sd: res.scoreDocs){
            Document d = s.getDocument(sd); 
            System.out.println(count);
            count +=1;
            System.out.println("Tweet: " + d.get("text")); 
            System.out.println("Hashtags: " + d.get("hashtags")); 
        }

    }//end main
    public TopDocs search(String searchQuery) throws IOException, ParseException{
        //get directory of lucene indices
        this.indexDir = new File(".").getCanonicalPath()+ "/indices/";
        Directory index_dir = FSDirectory.open( new File(this.indexDir).toPath() );
        //System.out.println("indexDir: " + this.indexDir);

        //reader to read the indexed documents
        IndexReader reader = DirectoryReader.open(index_dir); 
        //
        isearch = new IndexSearcher(reader); 

        QueryParser qparser = new QueryParser("text", new StandardAnalyzer() );
        Query query = qparser.parse(searchQuery);

        int max_results = 10;
        TopDocs results = isearch.search(query,max_results);
        return results;
    }//end search
    public Document getDocument(ScoreDoc sd) throws IOException{
        return isearch.doc(sd.doc);

    }//end getDocument

}//endclass Searcher
