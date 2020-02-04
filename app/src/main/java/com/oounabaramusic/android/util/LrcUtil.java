package com.oounabaramusic.android.util;

import com.oounabaramusic.android.bean.Lrc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LrcUtil {

    private static String timeMatcher3="\\[\\d{1,2}:\\d{1,2}\\.\\d{1,2}]";
    private static String timeMatcher2="\\[\\d{1,2}:\\d{1,2}]";

    public static Lrc parseFileToLrc(String filePath) throws IOException {
        return parseFileToLrc(new File(filePath));
    }

    public static Lrc parseFileToLrc(File file) throws IOException {
        Lrc result=new Lrc();
        List<Long> time=new ArrayList<>();
        List<String> content=new ArrayList<>();

        time.add(0L);
        content.add("header");

        BufferedReader br=new BufferedReader(new FileReader(file));
        String str;
        while((str=br.readLine())!=null){

            String t=str.substring(0,str.indexOf("]")+1);
            long lt=0;
            if(t.matches(timeMatcher3)){
                //去括号
                t=t.replace("[","").replace("]","");

                String[] lins=t.split("[:.]");

                lt+=Long.valueOf(lins[0])*60L*1000L;
                lt+=Long.valueOf(lins[1])*1000L;
                lt+=Long.valueOf(lins[2]);
            }else if(t.matches(timeMatcher2)){
                //去括号
                t=t.replace("[","").replace("]","");

                String[] lins=t.split("[:.]");

                lt+=Long.valueOf(lins[0])*60L*1000L;
                lt+=Long.valueOf(lins[1])*1000L;
            }else{
                continue;
            }

            content.add(str.substring(str.indexOf("]")+1));
            time.add(lt);
        }
        time.add((long) Integer.MAX_VALUE);
        content.add("foot");

        result.setTimes(time);
        result.setContent(content);
        return result;
    }
}
