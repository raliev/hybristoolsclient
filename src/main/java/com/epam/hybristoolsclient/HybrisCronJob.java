package com.epam.hybristoolsclient;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.epam.hybristoolsclient.utils.CommonUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rauf_Aliev on 8/31/2016.
 */
public class HybrisCronJob {
    static class JCommanderCmd extends CommonCommands {
        @Parameter(names = {"-e", "--e", "-execute", "--execute"}, required =  false,  description = "Execute the cronJob")
        public String cronJobNameForExecuting = "";

        @Parameter(names = {"-c", "-change", "--c", "--change"}, required =  false, description = "name of CronJob to change its attributes (see status)")
        public String cronJobNameForChanging = "";

        @Parameter(names = {"-s", "--status", "-status", "--s"}, required =  false, description = "change the status of CronJob (should be used with -c)")
        public String status = "";

        @Parameter(names = {"-a", "--all", "-all", "--a"}, required =  false, description = "show the list of all cronJobs")
        public boolean all = false;

    }

    public static void main(String[] args) throws UnsupportedEncodingException, InterruptedException {

        String pn = "HybrisCronJob";
        JCommanderCmd jct = new JCommanderCmd();
        try {
            new JCommander(jct, args);

        } catch (ParameterException e) {
            System.out.println(e.getMessage());
            CommonUtils.getHelp(jct, pn);
            return;
        }
        if (jct.help) {
            CommonUtils.getHelp(jct, pn);
            return;
        }
        if (!jct.cronJobNameForExecuting.equals("")) {
            String result = executeCronJob(jct.cronJobNameForExecuting);
            System.out.println(result);

        } else
        if (!jct.cronJobNameForChanging.equals("")) {
            String status = jct.status;
            if (status == null || status.equals("")) { System.out.println("please specify an attribute to change (look at the list of attributes supported, -?)"); return; }
            Map<String, String> attributes = new HashMap<>();
            attributes.put("status", jct.status);
            String result = changeCronJob(jct.cronJobNameForChanging, attributes);
            System.out.println(result);
        } else
        if (jct.all != false)
        {
            String result = showCronJobs();
            System.out.println(result);
        }


    }

    private static String executeCronJob(String cronJobNameForExecuting) throws UnsupportedEncodingException {
        return HttpRequest.execute(Conf.getWebRoot() + "tools/cronjobs/execute",
                String.join("&",
                        Arrays.asList(
                                String.join("=", CommonUtils.getParam("cronJobName", cronJobNameForExecuting))
                                )
                ),
                "",
                HttpMethodsEnum.GET
        );
    }

    private static String changeCronJob(String cronJobNameForExecuting, Map<String, String> newAttributes) throws UnsupportedEncodingException {

        return HttpRequest.execute(Conf.getWebRoot() + "tools/cronjobs/change",
                String.join("&",
                        Arrays.asList(
                                String.join("=", CommonUtils.getParam("cronJobName", cronJobNameForExecuting)),
                                String.join("=", CommonUtils.getParam("active", newAttributes.get("status")))
                                )
                ),
                "",
                HttpMethodsEnum.GET
        );
    }
    private static String showCronJobs() throws UnsupportedEncodingException {
        return HttpRequest.execute(Conf.getWebRoot() + "tools/cronjobs/cronjobs",
                "",
                "",
                HttpMethodsEnum.GET
        );
    }

}
