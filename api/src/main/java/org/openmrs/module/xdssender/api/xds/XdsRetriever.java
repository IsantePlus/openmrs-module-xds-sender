package org.openmrs.module.xdssender.api.xds;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.dcm4chee.xds2.common.exception.XDSException;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Component("xdssender.XdsRetriever")
public class XdsRetriever {
	
	@Autowired
	private XdsSenderConfig config;
	
	public HttpResponse sendRetrieveCCD(String patientEcid) throws XDSException, IOException,
	        NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		
		HttpClient httpclient;
		if (config.getExportCcdIgnoreCerts()) {
			Scheme httpsScheme = new Scheme("https", 443, createSSLFactoryIgnoringCert());
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(httpsScheme);

			ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);
			httpclient = new DefaultHttpClient(cm);
		} else {
			httpclient = new DefaultHttpClient();
		}
		HttpGet httpGet = new HttpGet(config.getExportCcdEndpoint() + "/" + patientEcid);
		interceptAuthorization(httpGet);
		return httpclient.execute(httpGet);
	}
	
	private SSLSocketFactory createSSLFactoryIgnoringCert() throws NoSuchAlgorithmException, KeyStoreException,
	        KeyManagementException {

		SSLContext sslContext = SSLContext.getInstance("SSL");

		sslContext.init(null, new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs,
										   String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs,
										   String authType) {
			}
		} }, new SecureRandom());

		return new SSLSocketFactory(sslContext);
	}
	
	private void interceptAuthorization(HttpGet httpGet) {
		Charset charset = Charset.forName("US-ASCII");
		String auth = config.getOshrUsername() + ":" + config.getOshrPassword();
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(charset));
		String authHeader = "Basic " + new String(encodedAuth, charset);
		
		httpGet.setHeader("Authorization", authHeader);
	}
}
