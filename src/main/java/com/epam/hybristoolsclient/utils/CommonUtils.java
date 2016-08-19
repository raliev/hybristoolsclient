package com.epam.hybristoolsclient.utils;

import com.beust.jcommander.JCommander;
import com.epam.hybristoolsclient.HybrisTypeSystem;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rauf_Aliev on 8/17/2016.
 */
public class CommonUtils {
    public static void getHelp(Object jct, String pn) {
        JCommander jc = new JCommander(jct);
        jc.setProgramName(pn);
        jc.usage();
    }
    public static List<String> getParam(String queryName, String query) throws UnsupportedEncodingException {
        List<String> params = new ArrayList<String>();
        if (query.equals(""))
        {   return params; }
        else {
            params.add(queryName);
            params.add(URLEncoder.encode(query, "UTF-8"));
            return params;
        }
    }
}
