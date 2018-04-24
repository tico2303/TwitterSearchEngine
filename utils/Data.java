package utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;
import java.io.*;

public class Data{

    //Data gets list of twitter data files in the data folder
    public ArrayList<String> getData()throws IOException{
        ArrayList<String> fileNameList = new ArrayList<String>();

        String data_dir = new File(".").getCanonicalPath();
        data_dir += File.separator + "data" + File.separator;
        //System.out.println("Searching Directory of " +data_dir + " ");

        File f = new File(data_dir);

        File[] flist = f.listFiles();
        //System.out.println("flist.length: "+ flist.length);
        for(File fl : flist){
            //System.out.println("Adding: " + fl.toString() + " to fileNameList");
            fileNameList.add(fl.toString());
        }
        return fileNameList;
    }//end getData



}//end Data
