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
import java.util.Arrays;
import java.util.List;

/**
 * Created by Rauf_Aliev on 8/18/2016.
 */
public class HybrisMedia {

    static class JCommanderCmd extends CommonCommands{
        @Parameter(names = {"-i"}, required =  false, description = "File to send to hybris")
        public String filename = "";

        @Parameter(names = {"-c", "--code", "-code"}, required =  false, description = "Media code")
        public String mediaCode = "";

        @Parameter(names = {"-mf", "--media-format", "-media-format"}, required =  false, description = "Media format")
        public String mediaFormat = "";

        @Parameter(names = {"-amf", "-all-mf", "--all-media-formats", "-all-media-formats"}, required =  false, description = "All media formats")
        public boolean allMediaFormats = false;

        @Parameter(names = {"-a", "-all-m", "--all-medias", "-all-medias"}, required =  false, description = "All medias")
        public boolean allmedias = false;

        @Parameter(names = {"-mt", "-media-type", "--media-type", "-mediatype"}, required =  false, description = "MediaType (Media, BarcodeMedia. CatalogUnawareMedia, CatalogVersionSyncScheduleMedia, ConfigurationMedia, Document, EmailAttachment,Formatter, ImpExMedia, JasperMedia, LDIFMedia, LogFile, ScriptMedia)")
        public String mediaType = "Media";

        @Parameter(names = {"-d", "-download", "--download"}, required =  false, description = "Download the media(s)")
        public boolean download;

        @Parameter(names = {"-f", "-fields", "--fields"}, required =  false, description = "fields to display")
        public String fields = "code,mediaContainer,mediaFormat";

        @Parameter(names = {"-of", "-output-format", "--output-format", "-outputFormat", "--outputFormat"}, required =  false, description = "Output Format (CON, CSV, TSV, BRD)")
        public String outputFormat = "CON";

        @Parameter(names = {"-to-file", "--to-file", "--tofile", "-t"}, required =  false, description = "Output file (use with -d)")
        public String outputFile = "";

        @Parameter(names = {"-catalog", "--catalog"}, required =  false, description = "Catalog")
        public String catalog = "";

        @Parameter(names = {"-catalogVersion", "--catalogVerson", "-catalog-version", "--catalog-version"}, required =  false, description = "Catalog Version")
        public String catalogVersion = "";

    }

    public static void main(String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {


        String pn = "HybrisMedia";
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

        if (jct.allMediaFormats) {
            System.out.println(HttpRequest.execute(Conf.getWebRoot()+"tools/media/mediaformats", "", "", HttpMethodsEnum.GET));
            return;
        }

        if (jct.allmedias) {
            //filename is empty: show all medias
            System.out.println(HttpRequest.execute(Conf.getWebRoot()+"tools/media/medias", "", "", HttpMethodsEnum.GET));
            return;
        }

        if (!jct.filename.equals("")) {
            String URL = Conf.getWebRoot() + "tools/media/create?" +
                    String.join("&",
                            Arrays.asList(
                                    String.join("=", CommonUtils.getParam("code", jct.mediaCode)),
                                    String.join("=", CommonUtils.getParam("filename", jct.filename)),
                                    String.join("=", CommonUtils.getParam("mediaFormat", jct.mediaFormat)),
                                    String.join("=", CommonUtils.getParam("catalog", jct.catalog)),
                                    String.join("=", CommonUtils.getParam("catalogVersion", jct.catalogVersion)),
                                    String.join("=", CommonUtils.getParam("mediaCode", jct.mediaCode)),
                                    String.join("=", CommonUtils.getParam("mediaType", jct.mediaType))
                            )
                    );
            System.out.println(HttpRequest.uploadFileToHybris(jct.filename, URL));
        }
        else
        {

            String response = HttpRequest.execute(Conf.getWebRoot()+"tools/media/medias?" +
                            String.join("&",
                                    Arrays.asList(
                                            String.join("=", CommonUtils.getParam("code", jct.mediaCode)),
                                            String.join("=", CommonUtils.getParam("outputFormat", jct.outputFormat)),
                                            String.join("=", CommonUtils.getParam("catalog", jct.catalog)),
                                            String.join("=", CommonUtils.getParam("catalogVersion", jct.catalogVersion)),
                                            String.join("=", CommonUtils.getParam("download", jct.download ? "true" : "false")),
                                            String.join("=", CommonUtils.getParam("fields", jct.fields))
                                    )
                            )
                    , "", "", HttpMethodsEnum.GET,
                    jct.outputFile
                    );
            if (jct.download) {

                if (jct.outputFile.equals("")) {
                    System.out.println(response);
                } else
                {
                    System.out.println(jct.outputFile+" is created.");
                }
                return;
            }
            List<String> lines = Arrays.asList(response.split("\n"));
            for (String line : lines)
            {
                List<String> columns = Arrays.asList(response.split("\t"));

            }
            System.out.println(response);
        }

    }


}
