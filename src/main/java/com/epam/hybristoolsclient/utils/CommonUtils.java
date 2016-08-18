package com.epam.hybristoolsclient.utils;

import com.beust.jcommander.JCommander;
import com.epam.hybristoolsclient.HybrisTypeSystem;

/**
 * Created by Rauf_Aliev on 8/17/2016.
 */
public class CommonUtils {
    public static void getHelp(Object jct, String pn) {
        JCommander jc = new JCommander(jct);
        jc.setProgramName(pn);
        jc.usage();
    }

}
