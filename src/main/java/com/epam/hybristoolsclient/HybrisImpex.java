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
import java.util.Arrays;
import java.util.List;
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

    public static void tailLogFile() throws InterruptedException {
        File file = new File(Conf.getLogLocation());
        Observable<String> tailer = FileObservable.tailer()
                .file(file)
                .startPosition(file.length())
                .sampleTimeMs(50)
                .chunkSize(10)
                .utf8()
                .tailText();

        tailer.subscribe(
                new Action1<String>() {
                    @Override
                    public void call(String line) {
                        System.err.println(line);
                        System.err.flush();
                        //if (line.contains("[DefaultImportService] Import was successful")) {
                            //Thread.currentThread().interrupt();
                        //}
                    }
                }
        );
    }


    static class TailLogger implements Runnable {
        public void run() {
            try {
                tailLogFile();
                while (Thread.currentThread().isInterrupted()) {
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
