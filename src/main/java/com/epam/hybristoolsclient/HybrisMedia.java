package com.epam.hybristoolsclient;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.epam.hybristoolsclient.utils.CommonUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by Rauf_Aliev on 8/18/2016.
 */
public class HybrisMedia {

    static class JCommanderCmd extends CommonCommands{
        @Parameter(names = {"-i"}, required =  true, description = "File to send to hybris")
        public String filename = "";
    }

    public static void main(String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        String pn = "HybrisMedia";
        HybrisImpex.JCommanderCmd jct = new HybrisImpex.JCommanderCmd();
        try {
            new JCommander(jct, args);

        } catch (ParameterException e) {
            CommonUtils.getHelp(jct, pn);
            return;
        }
        if (jct.help) {
            CommonUtils.getHelp(jct, pn);
            return;
        }

        String fileToUpload = "C:\\hybris\\h61\\hybris\\bin\\custom\\hybristoolsclient\\console\\data\\sample.impex";
        String URL = Conf.getWebRoot()+"tools/media/create?type=asdasd";
        System.out.println(HttpRequest.uploadFileToHybris(fileToUpload, URL));
    }


}
