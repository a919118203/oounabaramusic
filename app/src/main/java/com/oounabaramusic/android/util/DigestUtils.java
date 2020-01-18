package com.oounabaramusic.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class DigestUtils {
    private static MessageDigest md5;

    public static String md5HexOfFile(File file){
        MessageDigest md5=getMd5();
        FileInputStream in;
        StringBuilder sb=new StringBuilder();
        try {
            in=new FileInputStream(file);
            byte[] buff=new byte[1024],out;
            int len;
            while((len=in.read(buff))!=-1){
                md5.update(buff,0,len);
            }
            in.close();

            out=md5.digest();
            for(byte b:out){
                int a=b & 0xff;
                String str=Integer.toHexString(a);
                if(str.length()==1){
                    sb.append("0");
                    sb.append(str);
                }else{
                    sb.append(str);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static MessageDigest getMd5() {
        if(md5==null){
            try {
                md5=MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return md5;
    }
}
