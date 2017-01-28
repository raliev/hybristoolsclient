package com.epam.hybristoolsclient;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.epam.hybristoolsclient.utils.CSVPrint;
import com.epam.hybristoolsclient.utils.CommonUtils;
import com.github.davidmoten.rx.FileObservable;
import rx.Observable;
import rx.functions.Action1;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Rauf_Aliev on 8/16/2016.
 */

public class HybrisConfiguration {
    static class JCommanderCmd extends CommonCommands{

        @Parameter(names = {"-e", "--extension", "-extension" }, required = false, description = "Extension")
        public String extension = "";

        @Parameter(names = {"-l", "--list", "-list" }, required = false, description = "List of the configuration variables (from memory)")
        public boolean list = false;

        @Parameter(names = {"-c", "-check", "--check"}, required = false, description = "Check the difference between memory and *.property files (requires -e)")
        public boolean check = false;

        @Parameter(names = {"-s", "-sync", "--sync"}, required = false, description = "Set all configuration properties in memory that created/updated in *.property files (requires -e)")
        public boolean sync = false;

        @Parameter(names = {"-n", "-name", "--name"}, required = false, description = "Name of the property to change (requires -v)")
        public String name = "";

        @Parameter(names = {"-v", "-value", "--value"}, required = false, description = "New property value (requires -n)")
        public String value = "";

    }

    public static void main(String[] args) throws UnsupportedEncodingException, InterruptedException {


        String pn = "HybrisConfiguration";
        JCommanderCmd jct = new JCommanderCmd();
        try {
            new JCommander(jct, args);

        } catch (ParameterException e)
        {
                        e.printStackTrace();
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

        String result = "";
        result = ExecuteRequest(jct);
        System.out.print(result);
    }

    private static String ExecuteRequest(JCommanderCmd jct) throws UnsupportedEncodingException {
        if (jct.check) {
            return HttpRequest.execute(Conf.getWebRoot() + "tools/configuration/check",
                    String.join("&",
                            Arrays.asList(
                                    String.join("=", CommonUtils.getParam("extension", jct.extension))
                                    )
                    ),
                    "",
                    HttpMethodsEnum.GET
            );
        }
        if (jct.sync) {
            return HttpRequest.execute(Conf.getWebRoot() + "tools/configuration/sync",
                    String.join("&",
                            Arrays.asList(
                                    String.join("=", CommonUtils.getParam("extension", jct.extension))
                            )
                    ),
                    "",
                    HttpMethodsEnum.GET
            );
        }
        if (!jct.name.equals("")) {
            return HttpRequest.execute(Conf.getWebRoot() + "tools/configuration/set",
                    String.join("&",
                            Arrays.asList(
                                    String.join("=", CommonUtils.getParam("name", jct.name)),
                                    String.join("=", CommonUtils.getParam("value", jct.value))
                            )
                    ),
                    "",
                    HttpMethodsEnum.GET
            );
        }
        if (jct.list) {
            return HttpRequest.execute(Conf.getWebRoot() + "tools/configuration/list",
                    String.join("&",
                            Arrays.asList(
                                    String.join("=", CommonUtils.getParam("extension", jct.extension))
                            )
                    ),
                    "",
                    HttpMethodsEnum.GET
            );
        }

        return "";
    }
}
