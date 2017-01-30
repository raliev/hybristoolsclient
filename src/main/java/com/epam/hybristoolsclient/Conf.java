package com.epam.hybristoolsclient;

import sun.misc.URLClassPath;

import java.io.*;
import java.util.Calendar;
import java.util.Properties;

/**
 * Created by Rauf_Aliev on 8/16/2016.
 */
public class Conf {
    private static final Properties props;

    static {
        props = new Properties();
        String propFile = "config.properties";

        System.out.println("------");
        try {
        File f = new File(propFile);
        FileInputStream is = new FileInputStream(f);
            if(is == null) throw new IOException();
            props.load(is);
        } catch (Exception e) {
            System.out.println("property file config.properties not found");
            System.exit(1);
        }

    }

    public static String getWebRoot() {

        return props.getProperty("target.url");
    }

    public static String getLogLocation() {
        //todo: generate
        Calendar now = Calendar.getInstance();
        String year = now.get(Calendar.YEAR) + "";
        String month = (now.get(Calendar.MONTH) + 1) + "";
        String date = now.get(Calendar.DATE) + "";

        if (month.length() == 1) {
            month = "0" + month;
        }
        if (date.length() == 1) {
            date = "0" + month;
        }

        return props.getProperty("target.logdir") + "/console-" + year + month + date + ".log";
    }
}
