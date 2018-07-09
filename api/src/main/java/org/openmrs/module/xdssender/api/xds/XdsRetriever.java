package org.openmrs.module.xdssender.api.xds;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.api.service.impl.CcdHttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Component("xdssender.XdsRetriever")
public class XdsRetriever {

	private static final Logger LOGGER = LoggerFactory.getLogger(XdsRetriever.class);

	@Autowired
	private XdsSenderConfig config;
	
	public CcdHttpResult sendRetrieveCCD(String patientEcid)  {
		try {
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
			return new CcdHttpResult(httpclient.execute(httpGet));
		} catch (Exception ex) {
			LOGGER.error("Error when fetching ccd", ex);
			return new CcdHttpResult(ex);
		}
	}
	
	private SSLSocketFactory createSSLFactoryIgnoringCert() throws NoSuchAlgorithmException, KeyStoreException,
			KeyManagementException, UnrecoverableKeyException {
		return new SSLSocketFactory(new TrustStrategy() {
			@Override
			public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
				return true;
			}
		}, new AllowAllHostnameVerifier());
	}
	
	private void interceptAuthorization(HttpGet httpGet) {
		Charset charset = Charset.forName("US-ASCII");
		String auth = config.getOshrUsername() + ":" + config.getOshrPassword();
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(charset));
		String authHeader = "Basic " + new String(encodedAuth, charset);
		
		httpGet.setHeader("Authorization", authHeader);
	}
}
