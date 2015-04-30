/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsmannheim.ss15.alr.searchengine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 *
 * @author Herbe_000
 */
public class GUI {

    static String indexDir = System.getProperty("user.home") + "\\SearchEngine\\index";
    static String docsDir = System.getProperty("user.home") + "\\SearchEngine\\files\\";
    private static LuceneController lController = new LuceneController(indexDir, docsDir);
    private static Coordinator coordinator = new Coordinator(docsDir, indexDir);
    private static boolean showMenu = false;
    
    
    public static void main(String[] argu) throws InterruptedException, IOException, ParseException, Exception {
        setTrustAllCerts();
        indexDir=System.getProperty("user.home")+"\\SearchEngine\\index";
        docsDir=System.getProperty("user.home")+"\\SearchEngine\\files";
        coordinator.addToQueue("http://www.hs-mannheim.de/");
        coordinator.startCrawler(1);
        Thread.sleep(200000);
        coordinator.stopCrawler();
        lController.refreshIndex();
        while(showMenu){
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            System.out.println("Enter query String(or type exitquery to quit)");
            String line =  in.readLine();
            if(line.equals("exitquery")){
                showMenu = false;
            }
            List<Document> results = lController.doSearchQuery(line);
            for (Document d : results) {
                System.out.println(d.get("path"));
            }
            System.out.println("Found "+results.size()+" matching docs");
        }

    }
    
    private static void setTrustAllCerts() throws Exception
{
	TrustManager[] trustAllCerts = new TrustManager[]{
		new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			public void checkClientTrusted( java.security.cert.X509Certificate[] certs, String authType ) {	}
			public void checkServerTrusted( java.security.cert.X509Certificate[] certs, String authType ) {	}
		}
	};

	// Install the all-trusting trust manager
	try {
		SSLContext sc = SSLContext.getInstance( "SSL" );
		sc.init( null, trustAllCerts, new java.security.SecureRandom() );
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier( 
			new HostnameVerifier() {
				public boolean verify(String urlHostName, SSLSession session) {
					return true;
				}
			});
	}
	catch ( Exception e ) {
		//We can not recover from this exception.
		e.printStackTrace();
	}
}
}
