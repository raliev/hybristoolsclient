package com.epam.hybristoolsclient;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.epam.hybristoolsclient.utils.CSVPrint;
import com.epam.hybristoolsclient.utils.CommonUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Rauf_Aliev on 8/16/2016.
 */

public class HybrisBeans {

    static class JCommanderCmd extends CommonCommands{
        @Parameter(names = {"-b", "--bean", "-bean", "-bean-name", "-beanname"}, description = "Bean name")
        public String beanName = "";

        @Parameter(names = {"-e", "--extension", "-extension"}, description = "Extension")
        public String extension = "";

        @Parameter(names = {"-n", "--property-name", "-property-name", "--propertyname", "-property", "--property"}, description = "Bean property to change")
        public String propertyName = "";

        @Parameter(names = {"-v", "--property-value", "-property-value", "--propertyvalue", "-value", "--value"}, description = "New property value. just string value or <beanName> (enclosed in '<' and '>')")
        public String propertyValue = "";


    }

    public static void main(String[] args) throws UnsupportedEncodingException {

        String pn = "HybrisBeans";
        JCommanderCmd jct = new JCommanderCmd();
        try {
            new JCommander(jct, args);
        } catch (ParameterException e)
        {
            CommonUtils.getHelp(jct, pn);
            return;
        }
        if (jct.help) {
            CommonUtils.getHelp(jct, pn);
            return;
        }


        String request = jct.beanName.equals("") ? "/all" : "/bean/"+jct.beanName;
        String result =
                HttpRequest.execute(Conf.getWebRoot() + "tools/beans"+request,
                        String.join("&",
                                Arrays.asList(
                                        String.join("=", getParam("extension", jct.extension) ),
                                        String.join("=", getParam("propertyName", jct.propertyName) ),
                                        String.join("=", getParam("propertyValue", jct.propertyValue) )
                                        )
                        ),
                        "",
                        HttpMethodsEnum.GET
                );


        CSVPrint.printAsCSV( result, false );

    }

    private static List<String> getParam(String queryName, String query) throws UnsupportedEncodingException {
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
