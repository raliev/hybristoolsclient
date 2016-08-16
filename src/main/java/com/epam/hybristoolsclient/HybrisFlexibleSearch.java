package com.epam.hybristoolsclient;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;


import javax.security.auth.login.Configuration;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.net.URLEncoder;

/**
 * Created by Rauf_Aliev on 8/16/2016.
 */

class JCommanderCmd {
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
    @Parameter(names = {"-of", "-output-format", "--output-format", "--outputformat", "-format"}, description = "Output format: CSV, TSV, BRD, IMPEX")
    public String outputFormat = "";
    @Parameter(names = {"-u", "-user", "--user"}, description = "Session User")
    public String user = "";
    @Parameter(names = {"-debug", "--debug"}, description = "Debug mode")
    public boolean debug = false;
}

public class HybrisFlexibleSearch {

    public static void main(String[] args) throws UnsupportedEncodingException {

        JCommanderCmd jct = new JCommanderCmd();
        new JCommander(jct, args);

        System.out.println("query="+jct.query.toString());
        System.out.println("fields="+jct.fields.toString());

            //String query = EmptyIfNull(cmdLine.getOptionValue("q"));
            //String fields = EmptyIfNull(cmdLine.getOptionValue("f"));
            //String itemtype = EmptyIfNull(cmdLine.getOptionValue("i"));

            if (jct.query.equals("") && jct.itemtype.equals("")) {
                System.out.println("You need to specify either -query or -itemtype");
                return;
            }

            String result =
            HttpRequest.execute(Conf.getWebRoot() + "/tools/flexiblesearch/execute",
                                String.join("&",
                                                Arrays.asList(
                                                                String.join("=", getParam("query", jct.query) ),
                                                                String.join("=", getParam("fields", jct.fields)),
                                                                String.join("=", getParam("itemtype", jct.itemtype)),
                                                                String.join("=", getParam("debug", jct.debug ? "true" : "false")),
                                                                String.join("=", getParam("language", jct.language)),
                                                                String.join("=", getParam("catalogName", jct.catalogName)),
                                                                String.join("=", getParam("catalogVersion", jct.catalogVersion)),
                                                                String.join("=", getParam("outputFormat", jct.outputFormat)),
                                                                String.join("=", getParam("ref", jct.ref)
                                                                )
                                                )
                                        ),
                                "",
                                HttpMethodsEnum.GET
                    );

            System.out.println(result);

            //boolean withGui = !cmdLine.hasOption("no-gui");
            //int port = cmdLine.hasOption("port") ?
            //        ((Number)cmdLine.getParsedOptionValue("port")).intValue() : DEFAULT_PORT;


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

    private static String EmptyIfNull(String s) {
        return s == null ? "" : s;
    }


}
