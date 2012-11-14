package com.ccc.mail.ssl.truststores;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;

import org.apache.commons.lang.ArrayUtils;
import org.jsslutils.sslcontext.DefaultSSLContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ccc.mail.core.servers.storage.MailConstants;

public class MultiKeyStoreManager extends DefaultSSLContextFactory implements X509KeyManager {
	 private static final Logger logger = LoggerFactory.getLogger(MultiKeyStoreManager.class); 
	 private final X509KeyManager jvmKeyManager;
	 private final X509KeyManager customKeyManager;

	 public MultiKeyStoreManager(X509KeyManager jvmKeyManager, X509KeyManager customKeyManager ) {
	  this.jvmKeyManager = jvmKeyManager;
	  this.customKeyManager = customKeyManager;  
	 }

	 @Override
	 public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
	  // try the first key manager
	  String alias = customKeyManager.chooseClientAlias(keyType, issuers, socket);
	  if( alias == null ) {
	   alias = jvmKeyManager.chooseClientAlias(keyType, issuers, socket);
	   logger.warn("Reverting to JVM CLIENT alias : " + alias);
	  }

	  return alias;

	 }

	 @Override
	 public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
	  // try the first key manager
	  String alias = customKeyManager.chooseServerAlias(keyType, issuers, socket);
	  if( alias == null ) {
	   alias =  jvmKeyManager.chooseServerAlias(keyType, issuers, socket);
	   logger.warn("Reverting to JVM Server alias : " + alias);
	  } 
	  return alias;
	 }

	 @Override
	 public X509Certificate[] getCertificateChain(String alias) {
	  X509Certificate[] chain = customKeyManager.getCertificateChain(alias);
	  if( chain == null || chain.length == 0) {
	   logger.warn("Reverting to JVM Chain : " + alias);
	   return jvmKeyManager.getCertificateChain(alias);
	  } else {
	   return chain;
	  }  
	 }

	 @Override
	 public String[] getClientAliases(String keyType, Principal[] issuers) {
	  String[] cAliases = customKeyManager.getClientAliases(keyType, issuers);
	  String[] jAliases = jvmKeyManager.getClientAliases(keyType, issuers);
	  logger.warn("Supported Client Aliases Custom: " + cAliases.length + " JVM : " + jAliases.length);
	  return (String[]) ArrayUtils.addAll(cAliases,jAliases);
	 }

	 @Override
	 public PrivateKey getPrivateKey(String alias) {
	  PrivateKey key = customKeyManager.getPrivateKey(alias);
	  if( key == null ) {
	   logger.warn("Reverting to JVM Key : " + alias);
	   return jvmKeyManager.getPrivateKey(alias);
	  } else {
	   return key;
	  }
	 }

	 @Override
	 public String[] getServerAliases(String keyType, Principal[] issuers) {
	  String[] cAliases = customKeyManager.getServerAliases(keyType, issuers);
	  String[] jAliases = jvmKeyManager.getServerAliases(keyType, issuers);
	  logger.warn("Supported Server Aliases Custom: " + cAliases.length + " JVM : " + jAliases.length);
	  return (String[]) ArrayUtils.addAll(cAliases,jAliases);
	 }
	 
	 /**
	  * Returns an array of KeyManagers, set up to use the required keyStore.
	  * This method does the bulk of the work of setting up the custom trust managers.
	  * 
	  * @param props 
	  * 
	  * @return an array of KeyManagers set up accordingly.
	  */
	 private static KeyManager[] getKeyManagers(Properties props) throws IOException, GeneralSecurityException {
	  // First, get the default KeyManagerFactory.
	  String alg = KeyManagerFactory.getDefaultAlgorithm();
	  KeyManagerFactory kmFact = KeyManagerFactory.getInstance(alg);   
	  // Next, set up the KeyStore to use. We need to load the file into
	  // a KeyStore instance.
	  FileInputStream fis = new FileInputStream(props.getProperty(MailConstants.SSL_FACTORY));
	  logger.info("Loaded keystore");
	  KeyStore ks = KeyStore.getInstance("jks");
	  String keyStorePassword = props.getProperty(MailConstants.KEYSTORE_PASSWORD);
	  ks.load(fis, keyStorePassword.toCharArray());
	  fis.close();
	  // Now we initialise the KeyManagerFactory with this KeyStore
	  kmFact.init(ks, keyStorePassword.toCharArray());

	  // default
	  KeyManagerFactory dkmFact = KeyManagerFactory.getInstance(alg); 
	  dkmFact.init(null,null);  

	  // Get the first X509KeyManager in the list
	  X509KeyManager customX509KeyManager = getX509KeyManager(alg, kmFact);
	  X509KeyManager jvmX509KeyManager = getX509KeyManager(alg, dkmFact);

	  KeyManager[] km = { new MultiKeyStoreManager(jvmX509KeyManager, customX509KeyManager) };   
	  logger.debug("Number of key managers registered:" + km.length);  
	  return km;
	 }


	 /**
	  * Find a X509 Key Manager compatible with a particular algorithm
	  * @param algorithm
	  * @param kmFact
	  * @return
	  * @throws NoSuchAlgorithmException
	  */
	 private static X509KeyManager getX509KeyManager(String algorithm, KeyManagerFactory kmFact)
	   throws NoSuchAlgorithmException {
	  KeyManager[] keyManagers = kmFact.getKeyManagers();

	  if (keyManagers == null || keyManagers.length == 0) {
	   throw new NoSuchAlgorithmException("The default algorithm :" + algorithm + " produced no key managers");
	  }

	  X509KeyManager x509KeyManager = null;

	  for (int i = 0; i < keyManagers.length; i++) {
	   if (keyManagers[i] instanceof X509KeyManager) {
	    x509KeyManager = (X509KeyManager) keyManagers[i];
	    break;
	   }
	  }

	  if (x509KeyManager == null) {
	   throw new NoSuchAlgorithmException("The default algorithm :"+ algorithm + " did not produce a X509 Key manager");
	  }
	  return x509KeyManager;
	 }




	 private static void initialiseManager(Properties props) throws IOException, GeneralSecurityException { 
	  // Next construct and initialise a SSLContext with the KeyStore and
	  // the TrustStore. We use the default SecureRandom.
	  SSLContext context = SSLContext.getInstance("SSL");
	  context.init(getKeyManagers(props), getTrustManagers(props), null);
	  SSLContext.setDefault(context);

	 }

	private static TrustManager[] getTrustManagers(Properties props) throws NoSuchAlgorithmException {
		String alg=TrustManagerFactory.getDefaultAlgorithm();
		
		TrustManagerFactory t=TrustManagerFactory.getInstance(alg);
		return t.getTrustManagers();
		
	
	}


	}
