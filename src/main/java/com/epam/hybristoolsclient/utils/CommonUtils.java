package com.epam.hybristoolsclient.utils;

import com.beust.jcommander.JCommander;
import com.epam.hybristoolsclient.Conf;
import com.epam.hybristoolsclient.HybrisTypeSystem;
import com.github.davidmoten.rx.FileObservable;
import rx.Observable;
import rx.functions.Action1;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rauf_Aliev on 8/17/2016.
 */
public class CommonUtils {
    public static void getHelp(Object jct, String pn) {
        JCommander jc = new JCommander(jct);
        jc.setProgramName(pn);
        jc.usage();
    }
    public static List<String> getParam(String queryName, String query) throws UnsupportedEncodingException {
        List<String> params = new ArrayList<String>();
        if (query.equals(""))
        {   return params; }
        else {
            params.add(queryName);
            params.add(URLEncoder.encode(query, "UTF-8"));
            return params;
        }
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

}
