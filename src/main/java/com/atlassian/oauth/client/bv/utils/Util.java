package com.atlassian.oauth.client.bv.utils;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Srikanth BG on 9/12/14.
 */
public class Util {

    public static Date formatToSQLDate(String strDate)
    {
        String startDate=strDate;
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = null;
        try {
            date = sdf1.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        java.sql.Date sqlStartDate = new java.sql.Date(date.getTime());
        return sqlStartDate;
    }

    public static int getBatchCount(int totalCount, int batchSize)
    {
        if(totalCount != 0) {

            if(totalCount % batchSize == 0)
                return totalCount / batchSize;

            else
                return totalCount / batchSize + 1;
        }
        else return 0;
    }

   public static int getSqlDateDiff(Date d1, Date d2) {

       /*
            D1 past time , D2 present time
        */
       int days = Days.daysBetween(new DateTime(d1), new DateTime(d2)).getDays();
       return days;

   }

    public static Date getTodaysDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = new java.util.Date();
        return Date.valueOf(dateFormat.format(date));

    }

    public static PrivateKey getPrivateKey(String filename) {
        try {
            //URL path = ClassLoader.getSystemResource(filename);
           // InputStream inputStream = ClassLoader.getSystemResourceAsStream(filename);
           /* if (path == null) {
                //The file was not found, insert error handling here
            }
            File f = new File(path.toURI());*/

            File f = new File(filename);

            FileInputStream fis = new FileInputStream(f);
            DataInputStream dis = new DataInputStream(fis);
            byte[] keyBytes = new byte[(int) f.length()];
            dis.readFully(keyBytes);
            dis.close();

            PKCS8EncodedKeySpec spec =
                    new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);

        } catch (Exception e) {
                e.printStackTrace();
                return null;
        }
    }
}
