package com.epam.hybristoolsclient;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.epam.hybristoolsclient.utils.CommonUtils;


import javax.security.auth.login.Configuration;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.net.URLEncoder;

/**
 * Created by Rauf_Aliev on 8/16/2016.
 */



public class HybrisFlexibleSearch  {
    static class JCommanderCmd extends CommonCommands {
        @Parameter(names = {"-q", "-query", "--query"}, description = "Flexible Query")
        public String query = "";
        @Parameter(names = {"-f", "-fields", "--fields"}, description = "Attributes")
        public String fields = "";
        @Parameter(names = {"-i", "-it", "-itemtype", "--itemtype"}, description = "The shortcut X for 'select {pk} from {X}'")
        public String itemtype = "";
        @Parameter(names = {"-ref", "--ref"}, description = "Reference rules. Example: \"Category:code,name Product:code\" ")
        public String ref = "";
        @Parameter(names = {"-l", "-language", "--language", "--lang", "-lang"}, description = "current session language" )
        public String language = "";
        @Parameter(names = {"-cn", "-catalog", "--catalog", "--catalogName", "--catalog-name","-catalog-name"}, description = "catalog name" )
        public String catalogName = "";
        @Parameter(names = {"-cv", "-catalogversion", "--catalogversion", "--catalog-version", "-catalog-version"}, description = "session catalog version" )
        public String catalogVersion = "";
        @Parameter(names = {"-of", "-output-format", "--output-format", "--outputformat", "-format"}, description = "Output format: CSV, CON, TSV, BRD, IMPEX")
        public String outputFormat = "";
        @Parameter(names = {"-u", "-user", "--user"}, description = "Session User")
        public String user = "";
        @Parameter(names = {"-debug", "--debug"}, description = "Debug mode")
        public boolean debug = false;
        @Parameter(names = {"-mr", "-maxResults", "-maxresults", "--max-results", "-max-results"}, description = "max number of results")
        public int maxResults = 1000000;
        @Parameter(names = {"-b", "-beatify", "--beautify" })
        public boolean beautify = false;
        @Parameter(names = {"-pk"})
        public String pk;

    }
    public static void main(String[] args) throws UnsupportedEncodingException {

        String pn = "HybrisFlexibleSearch";
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

            if (jct.query.equals("") && jct.itemtype.equals("") && jct.pk.equals("")) {
                System.out.println("You need to specify either -query or -itemtype or -pk");
                return;
            }

            String result =
            HttpRequest.execute(Conf.getWebRoot() + "/tools/flexiblesearch/execute",
                                String.join("&",
                                                Arrays.asList(
                                                                String.join("=", CommonUtils.getParam("query", jct.query) ),
                                                                String.join("=", CommonUtils.getParam("fields", jct.fields)),
                                                                String.join("=", CommonUtils.getParam("itemtype", jct.itemtype)),
                                                                String.join("=", CommonUtils.getParam("debug", jct.debug ? "true" : "false")),
                                                                String.join("=", CommonUtils.getParam("language", jct.language)),
                                                                String.join("=", CommonUtils.getParam("catalogName", jct.catalogName)),
                                                                String.join("=", CommonUtils.getParam("catalogVersion", jct.catalogVersion)),
                                                                String.join("=", CommonUtils.getParam("outputFormat", jct.outputFormat)),
                                                                String.join("=", CommonUtils.getParam("maxResults", jct.maxResults+"")),
                                                                String.join("=", CommonUtils.getParam("ref", jct.ref)),
                                                                String.join("=", CommonUtils.getParam("beautify", jct.beautify ? "true" : "false")),
                                                                String.join("=", CommonUtils.getParam("pk", jct.pk)
                                                                )
                                                )
                                        ),
                                "",
                                HttpMethodsEnum.POST
                    );


            System.out.println(result);

            //boolean withGui = !cmdLine.hasOption("no-gui");
            //int port = cmdLine.hasOption("port") ?
            //        ((Number)cmdLine.getParsedOptionValue("port")).intValue() : DEFAULT_PORT;


    }



    private static String EmptyIfNull(String s) {
        return s == null ? "" : s;
    }


}
