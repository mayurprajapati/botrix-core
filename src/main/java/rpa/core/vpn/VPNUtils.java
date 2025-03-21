package rpa.core.vpn;

import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.exceptions.BishopRuleViolationException;
import rpa.core.file.OSValidator;

public class VPNUtils {
	
	private static Logger LOGGER = LoggerFactory.getLogger(VPNUtils.class);
	
	
	public static String getServerShaFingerprint(String host) throws Exception {
		String[] command;
		host = StringUtils.split(host,"/")[0];
		if (OSValidator.isUnix()) {
			String[] cmd = { "/bin/bash", "-c", "gnutls-cli " + host };
			command = cmd;
		} else if (OSValidator.isMac()) {
			String[] cmd = { "/bin/bash", "-c", "/usr/local/bin/gnutls-cli " + host };
			command = cmd;
		} else {
			throw new BishopRuleViolationException(OSValidator.getOS() + " is not supported");
		}
		try {
			return getServerShaFingerprint(host, command);
		} catch (BishopRuleViolationException e) {
			if (e.getMessage().contains("/usr/bin/gnutls-cli: No such file or directory")) {
				if (OSValidator.isUnix()) {
					String[] cmd = { "/bin/bash", "-c", "/usr/bin/gnutls-cli " + host };
					command = cmd;
					return getServerShaFingerprint(host, command);
				} else {
					throw e;
				}
			}
			throw e;
		}
	}
	public static String getServerShaFingerprint(String host, String[] command) throws BishopRuleViolationException {
	    try {
	    	LOGGER.info(Arrays.deepToString(command));
	    	
	    	ProcessBuilder processBuilder = new ProcessBuilder();
    		processBuilder.command(command);
    		System.out.println("Process builder starting");
	        Process process = processBuilder.start();
	        System.out.println("Process builder started");
	        String s;
			StringBuilder insBuilder = new StringBuilder();
			StringBuilder errsBuilder = new StringBuilder();
			System.out.println("Getting stream");
			
			 Scanner in = new Scanner(process.getInputStream());
			 Scanner err = new Scanner(process.getInputStream());
			 while(in.hasNextLine()) {
				 s = in.nextLine();
				 System.out.println(s);
				 insBuilder.append(s).append(System.lineSeparator());
			 }
			 while(err.hasNextLine()) {
				 s = err.nextLine();
				 System.out.println(s);
				 insBuilder.append(s).append(System.lineSeparator());
			 }
			    
			if (StringUtils.isNotBlank(errsBuilder)) {
				LOGGER.error("Error for the command : " + errsBuilder);
			}
			String streamOutput = insBuilder.toString() + System.lineSeparator() + errsBuilder.toString();
            System.out.println(streamOutput);
            String shaKey = StringUtils.trim(StringUtils.substringAfter(streamOutput, "Public Key PIN:")).split(System.lineSeparator())[0];
            if(StringUtils.startsWith(shaKey, "pin-sha256"))
            	return String.format(" --servercert %s ", shaKey);
            else if(StringUtils.containsIgnoreCase(streamOutput, "The operation timed out"))
            	return " ";
            else 
            	throw new BishopRuleViolationException(streamOutput);
	    } catch (BishopRuleViolationException e) {
	    	throw e;
	    } catch (Exception e) {
	        LOGGER.error("",e);
	    }
	    throw new BishopRuleViolationException("Unable to determine output");
	}

}

