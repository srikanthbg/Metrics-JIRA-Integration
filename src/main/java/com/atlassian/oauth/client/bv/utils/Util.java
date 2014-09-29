package com.atlassian.oauth.client.bv.utils;

import java.sql.Date;
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

}
