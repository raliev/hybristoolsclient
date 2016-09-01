package com.epam.hybristoolsclient;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.epam.hybristoolsclient.utils.CSVPrint;
import com.epam.hybristoolsclient.utils.CommonUtils;
import com.github.davidmoten.rx.FileObservable;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import sun.misc.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import rx.Observable;
import rx.functions.Action1;
/**
 * Created by Rauf_Aliev on 8/16/2016.
 */

public class HybrisImpex {
    static class JCommanderCmd extends CommonCommands{
        @Parameter(names = {"-i"}, variableArity = true, description = "Impex to import (file name)")
        public List<String> filenames = null;

        @Parameter(names = {"-mode"}, description = "Validation mode (strict, relaxed)")
        public String mode = "strict";

        @Parameter(names = {"-e"}, description = "Script Encoding (UTF-8 by default)")
        public String encoding = "UTF-8";

        @Parameter(names = {"-ce", "-codeExec", "--code-exec", "-code-exec"}, description = "Enable Code Execution (by default it is disabled)")
        public boolean codeExecutionEnabled = false;

        @Parameter(names = {"-l", "-legacy", "--legacy"}, description = "Legacy mode (by default it is disabled)")
        public boolean legacyMode = false;

        @Parameter(names = {"-mt", "-max-threads", "--max-threads", "--maxthreads", "-maxthreads"}, description = "Maximum number of threads (default = 16)")
        public int maxThreads = 16;

        @Parameter(names = {"-mc", "-media-code", "--media-code", "--mediacode", "-mediacode"}, description = "Media code")
        public String mediaCode = "";

        @Parameter(names = {"-b", "-beautify", "--beautify"}, description = "Beautify impex file")
        public String beautify = "";

        @Parameter(names = {"-o", "-optimize", "--optimize"}, description = "Optimize impex file")
        public boolean optimize;


    }

    public static void main(String[] args) throws UnsupportedEncodingException, InterruptedException {





        String pn = "HybrisImpex";
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

        if (!jct.beautify.equals(""))
        {
            String impex = null;
            try {
                impex = Files.lines(Paths.get(jct.beautify)).collect(Collectors.joining("\n"));
                beautifyImpex (impex, jct.optimize);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        //String query = EmptyIfNull(cmdLine.getOptionValue("q"));
        //String fields = EmptyIfNull(cmdLine.getOptionValue("f"));
        //String itemtype = EmptyIfNull(cmdLine.getOptionValue("i"));


        if ((jct.filenames == null || jct.filenames.size() == 0 )&& (jct.mediaCode == null || jct.mediaCode.equals(""))) {
            System.out.println ("please specify a file name (-i <filename>)");
            return;
        }

        Thread tailLogThread = new Thread(new TailLogger());
        tailLogThread.start();
        String result = "";
        if (jct.mediaCode != null && !jct.mediaCode.equals("")) {
            result = ExecuteRequest(jct, "", "");
        } else {
            for (String filename : jct.filenames) {
                if (filename == null || filename.equals("")) {
                    continue;
                }
                String impex = loadImpexFromFile(filename);
                result = ExecuteRequest(jct, filename, impex);
            }
        }
        CSVPrint.printAsCSV(result, false);
        Thread.sleep(1000);
        tailLogThread.interrupt();

        //System.out.println(result);

        //boolean withGui = !cmdLine.hasOption("no-gui");
        //int port = cmdLine.hasOption("port") ?
        //        ((Number)cmdLine.getParsedOptionValue("port")).intValue() : DEFAULT_PORT;


    }

    private static void beautifyImpex(String beautify, boolean optimize) {

        List<String> lines = Arrays.asList(beautify.split("\n"));
        int line_number = -1;
        Map<Integer, String> excludedLines = new HashMap<Integer, String>();
        Map<Integer, String> impexLines = new HashMap<>();
        Map<Integer, Integer> impexIndexes = new HashMap<>();
        Map<String, String> variables = new HashMap<>();
        Map<String, Map<String,String>> impexHeaderDefaults = new HashMap<>();
        boolean csv_started = false;

        int impex_index = 0;
        for (String line : lines) {
            line_number ++;
            if ((line.toLowerCase().trim().startsWith("#"))
                    ||
                (line.toLowerCase().trim().startsWith("$")))

            {
                if ((line.toLowerCase().trim().startsWith("$"))) {
                            line = line.trim();
                            String name = line.substring(line.indexOf("$"));
                            name = line.substring(0, line.indexOf("=")).trim();
                            String value = line.substring(line.indexOf("=")+1, line.length());
                            value = value.trim();
                            for (String variable : variables.keySet())
                            {
                                value = value.replace(variable, variables.get(variable));
                            }
                            variables.put(name, value);
                            //System.out.println("# DEBUG: set "+name+" = "+value);
                    }

                excludedLines.put(line_number, line);
                continue;
            }

            if (line.toLowerCase().startsWith("insert_update ")
                    ||
                line.toLowerCase().startsWith("insert ")
                    ||
                    line.toLowerCase().startsWith("update ")
                    )
            {
                csv_started = true;
                impex_index++;

                List<String> headers = CSVPrint.parseLine(line, ';', '\"');
                for (int i=1; i<headers.size();i++)
                {
                    String header = headers.get(i);
                    String header_replaced = header;
                    for (String variable : variables.keySet()) {
                        header_replaced = header_replaced.replace(variable, variables.get(variable));
                    }
                    List<String> fieldList = new ArrayList<>();
                    parseHeader(header_replaced, "", fieldList);
                    Map<String,String> defaultValues = new HashMap<>();
                    for (String field : fieldList)
                    {
                        String defaultValue = extractDefaultValue (field);
                        if (defaultValue != null)
                            defaultValues.put(field, defaultValue);
                    }
                    if (defaultValues.size() != 0)
                        impexHeaderDefaults.put(impex_index + "_" + i, defaultValues);
                }


            }

            if (line.trim().equals(""))
            {
                csv_started = false;
            }

            if (csv_started)
            {
                impexLines.put(line_number, line);
                impexIndexes.put(line_number, impex_index);
            } else
            {
                excludedLines.put(line_number, line);
            }

        }

        int i = 0;
        while (i<=line_number)
        {
            if (impexIndexes.get(i) == null && excludedLines.get(i) != null)
            {
                // excluded
                System.out.println(excludedLines.get(i));
                i++;
                continue;
            }

            if (impexIndexes.get(i) != null)
            {
                int current= impexIndexes.get(i);
                String buffer = "";
                while (impexIndexes.get(i) != null && impexIndexes.get(i) == current)
                {
                    buffer = buffer + impexLines.get(i) + "\n";
                    i++;
                }
                CSVPrint.printAsCSV(buffer, true, ';');
            }
        }

    }

    private static String extractDefaultValue(String field) {
        if (field.indexOf("default=") != -1) {
                int i = field.indexOf("default=") + "default=".length();
                String ost = field.substring(i, field.length());
                int kvIndex = ost.indexOf("]");
                int zpIndex = ost.indexOf(",");
                if (kvIndex == -1 && zpIndex==-1) { return ""; }
                int min = kvIndex < zpIndex ? (kvIndex == -1 ? zpIndex : kvIndex) : (zpIndex == -1 ? kvIndex : zpIndex);
                ost = ost.substring(0, min);
                ost = ost.trim();
                if (!ost.equals("") && ost.charAt(0) == '"')
                {
                    ost = ost.substring(1,ost.length());
                    if (ost.lastIndexOf("\"")==-1) { return ""; }
                    ost = ost.substring(0,ost.lastIndexOf("\""));
                    return ost;
                } else
                {
                    return ost;
                }
        }
        return null;
    }

    private static List<String>extractFieldList(String header) {
        // asdasd default="asdasd"
// classAttributeAssignment(classificationClass(code,catalogVersion(catalog(id[default='ElectronicsClassification']),version[default='1.0'])),classificationAttribute(code,systemVersion(catalog(id[default='ElectronicsClassification']),version[default='1.0'])),systemVersion(catalog(id[default='ElectronicsClassification']),version[default='1.0']))

        List<String> fieldList = new ArrayList<>();
        parseHeader(header, "", fieldList);
        for (int i=0; i<fieldList.size();i++)
        {

        }

        return null;
    }

    //622:ElectronicsClassification:1.0:Aperture setting, 5775:ElectronicsClassification:1.0:ElectronicsClassification:1.0
    static void parseHeader(String header, String obj, List<String> fieldList)
    {
        header = header.trim();
        int skIndex = header.indexOf("(");
        int zpIndex = header.indexOf(",");

        if (skIndex == -1 && zpIndex == -1) {
            fieldList.add(obj+"."+header);
        } else
        if ((skIndex < zpIndex && skIndex != -1)  || (zpIndex == -1)) {
            int pravSk = findPravSk(header);
            String insideSk = header.substring(skIndex+1, pravSk);
            String newObj = header.substring(0,skIndex);
            String ost = header.substring(pravSk, header.length()).trim();
            if (!ost.equals("") && ost.charAt(0) == '[')
            {
                // default value for the group
                System.out.println("!");
            }
            parseHeader(insideSk, obj+"."+newObj,  fieldList);
            String afterSk = header.substring(pravSk+1, header.length()).trim();
            if (afterSk.length() > 0 && afterSk.substring(0,1).equals(",")) {
                afterSk = afterSk.substring(1, afterSk.length());
                //parseHeader(insideSk, obj, fieldList);
                parseHeader(afterSk, obj, fieldList);
            }

        } else
        if ((zpIndex < skIndex && zpIndex!=-1) || skIndex == -1)
        {
            String id = header.substring(0,zpIndex);
            if (!id.equals("")) {
                fieldList.add(obj+"."+id);
            }
            String ost = header.substring(zpIndex+1, header.length());
            parseHeader(ost, obj, fieldList);
        }
    }

    private static int findPravSk(String header) {
        int skIndex = 0;
        for (int i=0;i<header.length();i++)
        {
            if (header.charAt(i) == '(')
            {
                skIndex ++;
            }
            if (header.charAt(i) == ')')
            {
                skIndex --;
                if (skIndex == 0) { return i; }
            }
        }
        return 0; //impossible situation
    }

    private static String ExecuteRequest(JCommanderCmd jct, String filename, String impex) throws UnsupportedEncodingException {
        return HttpRequest.execute(Conf.getWebRoot() + "tools/impex/import",
                String.join("&",
                        Arrays.asList(
                                String.join("=", CommonUtils.getParam("filename", filename)),
                                String.join("=", CommonUtils.getParam("encoding", jct.encoding)),
                                String.join("=", CommonUtils.getParam("legacyMode", jct.legacyMode ? "true" : "false")),
                                String.join("=", CommonUtils.getParam("mode", jct.mode)),
                                String.join("=", CommonUtils.getParam("codeExecutionEnabled", jct.codeExecutionEnabled ? "true" : "false")),
                                String.join("=", CommonUtils.getParam("impex", impex)),
                                String.join("=", CommonUtils.getParam("maxThreads", jct.maxThreads + "")),
                                String.join("=", CommonUtils.getParam("mediaCode", jct.mediaCode))

                        )
                ),
                "",
                HttpMethodsEnum.POST
        );
    }

    private static String loadImpexFromFile(String fileStr) {
        String impex = null;
        try {
            System.err.println("[READING IMPEX] "+fileStr);
            impex = Files.lines(Paths.get(fileStr)).collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return impex;
    }




    static class TailLogger implements Runnable {
        public void run() {
            try {
                CommonUtils.tailLogFile();
                while (Thread.currentThread().isInterrupted()) {
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}


//         classAttributeAssignment(
//          classificationClass(
//                              code,
//                              catalogVersion(
//                                  catalog(
//                                                      id[default='ElectronicsClassification']),
//                                                      version[default='1.0']
//                                          )
//                            ),
//              classificationAttribute(
//                                      code,
//                                      systemVersion(
//                                                          catalog(
//                                                              id[default='ElectronicsClassification']
//                                                                  ),
//                                                          version[default='1.0']
//                                                   )
//                                     ),
//              systemVersion(
//                                  catalog(
//                                                    id[default='ElectronicsClassification']
//                                         ),
//                                  version[default='1.0']
//                          )
//          )