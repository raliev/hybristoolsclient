package com.epam.hybristoolsclient;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.epam.hybristoolsclient.com.epam.hybristoolsclient.helpers.CSVPrint;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Rauf_Aliev on 8/16/2016.
 */

public class HybrisTypeSystem {
    static class JCommanderCmd {
        @Parameter(names = {"-type", "--type", "-t"}, description = "Type")
        public String type = "";

        @Parameter(names = {"-attribute", "--attribute", "-a"}, description = "Attribute")
        public String attribute = "";
    }

    public static void main(String[] args) throws UnsupportedEncodingException {

        JCommanderCmd jct = new JCommanderCmd();
        new JCommander(jct, args);

        //String query = EmptyIfNull(cmdLine.getOptionValue("q"));
        //String fields = EmptyIfNull(cmdLine.getOptionValue("f"));
        //String itemtype = EmptyIfNull(cmdLine.getOptionValue("i"));


        String request = jct.attribute.equals("") ? "/attributes" : "/attribute/"+jct.attribute;
        String result =
                HttpRequest.execute(Conf.getWebRoot() + "tools/typesystem/type/"+jct.type+request,
                        String.join("&",
                                Arrays.asList(
                                        )
                        ),
                        "",
                        HttpMethodsEnum.GET
                );

        List<String> lines = Arrays.asList(result.split("\n"));
        List<List<String>> csv = new ArrayList<>();
        for (String line : lines)
        {
            List<String> columns = new ArrayList<>();
            columns.addAll(Arrays.asList(line.split("\t")));
            csv.add(columns);
        }
        CSVPrint.writeCSV( csv);
        //System.out.println(result);

        //boolean withGui = !cmdLine.hasOption("no-gui");
        //int port = cmdLine.hasOption("port") ?
        //        ((Number)cmdLine.getParsedOptionValue("port")).intValue() : DEFAULT_PORT;


    }



}
