package com.epam.hybristoolsclient;

import java.util.Calendar;

/**
 * Created by Rauf_Aliev on 8/16/2016.
 */
public class Conf {
    public static String getWebRoot() { return "https://electronics.local:9002/"; }

    public static String getLogLocation() {
        //todo: generate
        Calendar now = Calendar.getInstance();
        String year = now.get(Calendar.YEAR) +"";
        String month = (now.get(Calendar.MONTH)+1)+ "";
        String date = now.get(Calendar.DATE)+"";

        if (month.length()==1) { month = "0"+month; }
        if (date.length()==1) { date = "0"+month; }

        return "C:\\hybris\\h61\\hybris\\log\\tomcat\\console-"+year+month+date+".log";
    }
}
