package rpa.core.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import botrix.internal.logging.LoggerFactory;
import rpa.core.exceptions.BishopLoginException;

public class FTPUtils {
	private static Logger LOGGER = LoggerFactory.getLogger(FTPUtils.class);

	/**
	 * Login to SFTP Client
	 * 
	 * @param username
	 * @param password
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public ChannelSftp loginSFTP(String username, String password, String url) throws Exception {
		try {
			LOGGER.info("Logging into SFTP");
			JSch jsch = new JSch();
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			Session jschSession = jsch.getSession(username, url, 2222);
			jschSession.setConfig(config);
			jschSession.setPassword(password);
			jschSession.connect();
			if (jschSession.isConnected()) {
				LOGGER.info("Successful login to SFTP");
				return (ChannelSftp) jschSession.openChannel("sftp");
			} else {
				LOGGER.error("Unsuccessful login to SFTP");
				throw new BishopLoginException("Unsuccessful login to SFTP");
			}
		} catch (Exception e) {
			LOGGER.error("SFTP connection timeout. Login unsuccessful for user: " + username, e);
			throw new BishopLoginException("SFTP connection timeout. Login unsuccessful for user: " + username, e);
		}
	}

	/**
	 * Login to FTP Client
	 * 
	 * @param username
	 * @param password
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public FTPClient login(String username, String password, String url) throws Exception {
		try {
			LOGGER.info("Logging into FTP");
			FTPClient client = new FTPClient();
			client.setDefaultPort(21);
			client.connect(url);
			client.enterLocalPassiveMode();
			client.login(username, password);
			if (client.isConnected()) {
				LOGGER.error("successful login to FTP");
				return client;
			} else {
				LOGGER.error("Unsuccessful login to FTP");
				throw new BishopLoginException("Unsuccessful login to FTP");
			}
		} catch (BishopLoginException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("FTP connection timeout. Login unsuccessful for user " + username, e);
			throw new BishopLoginException("FTP connection timeout. Login unsuccessful for user " + username, e);
		}
	}

}
