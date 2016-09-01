package com.epam.hybristoolsclient;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.epam.hybristoolsclient.utils.CommonUtils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by Rauf_Aliev on 8/25/2016.
 */
public class HybrisLog {

    static class JCommanderCmd extends CommonCommands{

        @Parameter(names = {"-a", "--all", "-all"}, required =  false, description = "All LogManager's classes")
        public boolean all = false;

        @Parameter(names = {"-l", "--list", "-list"}, required =  false, description = "List of configured classes and their log levels")
        public boolean list = false;

        @Parameter(names = {"-c", "--class", "-class"}, required =  false, description = "Full class name")
        public String className = "";

        @Parameter(names = {"-ll", "--log-level", "-log-level", "-loglevel", "--loglevel", "-logLevel" , "--logLevel"}, required =  false, description =
                "Log level (ALL/TRACE/DEBUG/INFO/WARN/ERROR/FATAL/OFF")
        public String logLevel = "";

    }

    public static void main(String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        String pn = "HybrisLog";
        JCommanderCmd jct = new JCommanderCmd();
        try {
            JCommander jc = new JCommander(jct, args);
        } catch (ParameterException e) {
            CommonUtils.getHelp(jct, pn);
            return;
        }
        if (jct.help) {
            CommonUtils.getHelp(jct, pn);
            return;
        }

        if (jct.all) {
            showAllClasses();
            return;
        } else if (jct.list) {
            showListOfClasses();
            return;
        } else if (!jct.className.equals("") && !jct.logLevel.equals("")) {
            String response = HttpRequest.execute(Conf.getWebRoot() + "tools/logging/add?" +
                            String.join("&",
                                    Arrays.asList(
                                            String.join("=", CommonUtils.getParam("class", jct.className)),
                                            String.join("=", CommonUtils.getParam("logLevel", jct.logLevel))
                                    )
                            )
                    , "", "", HttpMethodsEnum.GET,
                    ""
            );
            System.out.println(response);
            return;
        } else
        {
            // no params
            CommonUtils.tailLogFile();
            while (true) { Thread.sleep(50); }
        }

    }

    private static void showAllClasses() {
        String URL = Conf.getWebRoot() + "tools/logging/all";
        System.out.println(HttpRequest.execute(URL, "", "", HttpMethodsEnum.GET));
    }

    private static void showListOfClasses() {
        String URL = Conf.getWebRoot() + "tools/logging/loggers";
        System.out.println(HttpRequest.execute(URL, "", "", HttpMethodsEnum.GET));
    }
}
