package com.epam.hybristoolsclient;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.epam.hybristoolsclient.utils.CSVPrint;
import com.epam.hybristoolsclient.utils.CommonUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Rauf_Aliev on 8/16/2016.
 */

public class HybrisTypeSystem {
    static class JCommanderCmd extends CommonCommands{
        @Parameter(names = {"-type", "--type", "-t"}, description = "Type")
        public String type = "";

        @Parameter(names = {"-attribute", "--attribute", "-a"}, description = "Attribute")
        public String attribute = "";

    }

    public static void main(String[] args) throws UnsupportedEncodingException {


        String pn = "HybrisTypeSystem";
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


        //String query = EmptyIfNull(cmdLine.getOptionValue("q"));
        //String fields = EmptyIfNull(cmdLine.getOptionValue("f"));
        //String itemtype = EmptyIfNull(cmdLine.getOptionValue("i"));


        String request = jct.attribute.equals("") ? "/attributes" : "/attribute/"+jct.attribute;
        if (jct.type.equals("") ) { request = "/types"; }  else { request = "/type/"+jct.type + request; }
        String result =
                HttpRequest.execute(Conf.getWebRoot() + "tools/typesystem"+request,
                        String.join("&",
                                Arrays.asList(
                                        )
                        ),
                        "",
                        HttpMethodsEnum.GET
                );

        CSVPrint.printAsCSV(result, false);
        //System.out.println(result);

        //boolean withGui = !cmdLine.hasOption("no-gui");
        //int port = cmdLine.hasOption("port") ?
        //        ((Number)cmdLine.getParsedOptionValue("port")).intValue() : DEFAULT_PORT;


    }




}
