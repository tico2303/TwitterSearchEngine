import java.io.File;
import java.io.FilenameFilter;
import java.util.*;
import java.io.*;

public class Data{
    public ArrayList<String> getData()throws IOException{
        String data_dir = new File(".").getCanonicalPath();
        data_dir += File.separator + "data" + File.separator;
        ArrayList<String> fileNameList = new ArrayList<String>();
        // System.out.println("Searching Directory of " +data_dir + " for .json files");

    File f = new File(data_dir);
    File[] flist = f.listFiles();
    for(File fl : flist){
        // System.out.println("Adding: " + fl.toString() + " to fileNameList");
        fileNameList.add(fl.toString());
    }
    return fileNameList;
    }

}//end DataFinder
