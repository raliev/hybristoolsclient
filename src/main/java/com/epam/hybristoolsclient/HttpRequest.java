package com.epam.hybristoolsclient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by Rauf_Aliev on 8/16/2016.
 */


public class HttpRequest {

    private static void trustAllHosts()
    {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
        {
            public java.security.cert.X509Certificate[] getAcceptedIssuers()
            {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
            {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
            {
            }
        } };

        // Install the all-trusting trust manager
        try
        {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) { return true; }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String execute(String targetURL, String urlParameters, String postData, HttpMethodsEnum method) {
        return execute(targetURL, urlParameters, postData, method, null);
    }

        public static String execute(String targetURL, String urlParameters, String postData, HttpMethodsEnum method, String writetofile) {
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url;
            if (method.equals(HttpMethodsEnum.GET)) {
                url = new URL(targetURL + (urlParameters.equals("") ? "" : "?") + urlParameters);
            } else
            if (method.equals(HttpMethodsEnum.POST))
            {
                url = new URL(targetURL);
                if (!urlParameters.equals("") && postData.equals("")) {
                    postData = urlParameters;
                }
            }
            else
            {
                throw new Exception("Method "+method.toString()+" is not supported");
            }

            trustAllHosts();
            HttpsURLConnection.setDefaultHostnameVerifier ((hostname, session) -> true);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(method == HttpMethodsEnum.POST ? "POST" : "GET");
            if (method.equals(HttpMethodsEnum.POST)) {
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                connection.setRequestProperty("Content-Length",
                        Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches(false);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.writeBytes(postData);
                wr.close();

                //Get Response
            }
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            if (writetofile != null && !writetofile.equals("")) {
                File  f = new File (writetofile);
                FileOutputStream fos = new FileOutputStream(f);
                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                fos.flush();
                is.close();
            } else {
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\n');
                }
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String uploadFileToHybris(String fileToUpload, String URL) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        trustAllHosts();
        HttpsURLConnection.setDefaultHostnameVerifier ((hostname, session) -> true);
        //CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpClient httpClient = createHttpClient_AcceptsUntrustedCerts();
        HttpPost uploadFile = new HttpPost(URL);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("name", "Name", ContentType.TEXT_PLAIN);

        // This attaches the file to the POST:
        File f = new File(fileToUpload);
        builder.addBinaryBody(
                "file",
                new FileInputStream(f),
                ContentType.APPLICATION_OCTET_STREAM,
                f.getName()
        );

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);
        HttpResponse response = httpClient.execute(uploadFile);
        HttpEntity responseEntity = response.getEntity();
        return EntityUtils.toString(responseEntity);
    }

    public static HttpClient createHttpClient_AcceptsUntrustedCerts() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        HttpClientBuilder b = HttpClientBuilder.create();

        // setup a Trust Strategy that allows all certificates.
        //
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                return true;
            }
        }).build();
        b.setSslcontext( sslContext);

        // don't check Hostnames, either.
        //      -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if you don't want to weaken
        HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

        // here's the special part:
        //      -- need to create an SSL Socket Factory, to use our weakened "trust strategy";
        //      -- and create a Registry, to register it.
        //
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();

        // now, we create connection-manager using our Registry.
        //      -- allows multi-threaded use
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager( socketFactoryRegistry);
        b.setConnectionManager( connMgr);

        // finally, build the HttpClient;
        //      -- done!
        HttpClient client = b.build();
        return client;
    }

}
