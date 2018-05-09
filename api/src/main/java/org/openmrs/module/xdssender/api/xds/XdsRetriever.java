package org.openmrs.module.xdssender.api.xds;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.dcm4chee.xds2.common.exception.XDSException;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Component("xdssender.XdsRetriever")
public class XdsRetriever {
	
	@Autowired
	private XdsSenderConfig config;
	
	public CloseableHttpResponse sendRetrieveCCD(String patientEcid) throws XDSException, IOException,
	        NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(createSSLFactoryIgnoringCert()).build();
		HttpGet httpGet = new HttpGet(config.getExportCcdEndpoint() + "/" + patientEcid);
		interceptAuthorization(httpGet);
		return httpclient.execute(httpGet);
	}
	
	private SSLConnectionSocketFactory createSSLFactoryIgnoringCert() throws NoSuchAlgorithmException, KeyStoreException,
	        KeyManagementException {
		SSLContextBuilder builder = new SSLContextBuilder();
		builder.loadTrustMaterial(null, new TrustStrategy() {
			
			public boolean isTrusted(final X509Certificate[] chain, String authType) throws CertificateException {
				return true;
			}
		});
		return new SSLConnectionSocketFactory(builder.build());
	}
	
	private void interceptAuthorization(HttpGet httpGet) {
		Charset charset = Charset.forName("US-ASCII");
		String auth = config.getOshrUsername() + ":" + config.getOshrPassword();
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(charset));
		String authHeader = "Basic " + new String(encodedAuth, charset);
		
		httpGet.setHeader("Authorization", authHeader);
	}
}
