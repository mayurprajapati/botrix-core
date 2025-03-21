package rpa.core.vpn;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.driver.G;
import rpa.core.exceptions.BishopDataConstraintException;
import rpa.core.exceptions.BishopRuleViolationException;
import rpa.core.file.OSValidator;
import rpa.core.windowsprocess.WindowsProcess;

public class PulseSecureClientVPN {
	private static Logger LOGGER = LoggerFactory.getLogger(PulseSecureClientVPN.class);

	public static void connect(String host, String user, String password) throws Exception {
		try {
			String streamFromTermial = connectToVPN(host, user, password);
			if (StringUtils.containsIgnoreCase(streamFromTermial, "Session authentication will expire") || StringUtils
					.containsIgnoreCase(streamFromTermial, "Connected to") || StringUtils.isEmpty(streamFromTermial)) {
				LOGGER.info("Connection successful to Pulse Secure");
				G.executionMetrics.setVpn(true);
			} else {
				LOGGER.error("Unable to login to Pulse Secure");
				throw new BishopDataConstraintException("Unable to connect to Pulse Secure. " + streamFromTermial);
			}

		} catch (BishopDataConstraintException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("Pulse Secure VPN was not connected successfully", e);
			throw new Exception("Pulse Secure VPN was not connected successfully");
		}
	}

	public static String connectToVPN(String host, String user, String password)
			throws Exception, IOException, InterruptedException {
		String s;
		Process proc = null;
		if (OSValidator.isWindows()) {
			throw new BishopRuleViolationException("Pulse Secure for Windows is not supported");
		} else if (OSValidator.isMac()) {
			throw new BishopRuleViolationException("Pulse Secure for MacOS is not supported");
		} else if (OSValidator.isUnix()) {
			String servercert = VPNUtils.getServerShaFingerprint(host);
			String terminalCommand = String.format("echo -n \"%s\" | openconnect --protocol=pulse %s --background --user=%s%s--passwd-on-stdin",
					password, host, user, servercert);
			String[] terminalCommands = new String[] { "/bin/bash", "-c", terminalCommand };
			LOGGER.info("Command used in Terminal :\n{}", terminalCommand.replace(password, "**********"));
			proc = WindowsProcess.runCommandAndWait(terminalCommands);
		}
		String streamOutput = WindowsProcess.streamOutput(proc);
		return streamOutput;
	}

	public static void disconnect() {
		try {
			if (OSValidator.isUnix()) {
				String terminalCommand = "pkill openconnect";
				String[] terminalCommands = new String[] { "/bin/bash", "-c", terminalCommand };
				WindowsProcess.streamOutput(WindowsProcess.runCommandAndWait(terminalCommands));
			}
			LOGGER.info("Disabled Pulse Secure VPN.");
		} catch (Exception e) {
			LOGGER.error("Pulse Secure VPN was not disconnected.", e);
		}
	}

}