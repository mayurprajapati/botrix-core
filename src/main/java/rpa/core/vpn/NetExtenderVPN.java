package rpa.core.vpn;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.driver.G;
import rpa.core.exceptions.BishopDataConstraintException;
import rpa.core.file.OSValidator;
import rpa.core.windowsprocess.WindowsProcess;

public class NetExtenderVPN {
	private static Logger LOGGER = LoggerFactory.getLogger(NetExtenderVPN.class);
	public static final String NETEXTENDER_CLI_WINDOWS = "\"C:\\Program Files (x86)\\SonicWall\\SSL-VPN\\NetExtender\\NECLI.exe\"";
	public static final String NETEXTENDER_CLI_MAC = "mobileconnect://connect?";
	public static final String NETEXTENDER_CLI_LINUX = "netExtender";

	public static void connect(String host, String domain, String user, String password) throws Exception {
		try {
			String streamFromTermial = connectToVPN(host, domain, user, password);
			if (StringUtils.containsIgnoreCase(streamFromTermial, "connected successfully") || StringUtils
					.containsIgnoreCase(streamFromTermial, "NetExtender has connected with following information")
					||  StringUtils
					.containsIgnoreCase(streamFromTermial, "NetExtender has connected with following information")) {
				LOGGER.info("Connection successful to NetExtender");
				G.executionMetrics.setVpn(true);
			} else {
				streamFromTermial = connectToVPN(host, domain, user, password);
				if (StringUtils.containsIgnoreCase(streamFromTermial, "connected successfully") || StringUtils
						.containsIgnoreCase(streamFromTermial, "NetExtender has connected with following information")) {
					LOGGER.info("Connection successful to NetExtender");
					G.executionMetrics.setVpn(true);
				} else {
					LOGGER.error("Unable to login to NetExtender");
					throw new BishopDataConstraintException("Unable to connect to NetExtender. " + streamFromTermial);
				}
			}

		} catch (BishopDataConstraintException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("NetExtender VPN was not connected successfully", e);
			throw new Exception("NetExtender VPN was not connected successfully");
		}
	}

	public static String connectToVPN(String host, String domain, String user, String password)
			throws Exception, IOException, InterruptedException {
		String s = null;
		Process proc = null;
		if (OSValidator.isWindows()) {
			String cmdcommand = String.format("%s connect -s %s -u %s -p %s -d %s --always-trust %s", NETEXTENDER_CLI_WINDOWS, host, user,
					password, domain, host);
			String[] cmdCommands = new String[] { "cmd", "/C", cmdcommand };
			String[] a = {"taskkill /F /IM NEIdle.exe"};
			String[] b = {"taskkill /F /IM NEGui.exe"};
			proc = WindowsProcess.runCommandAndWait(a);
			proc = WindowsProcess.runCommandAndWait(b);
			LOGGER.info("Command used in CMD:\n{}", cmdcommand.replace(password, "**********"));
			proc = WindowsProcess.runCommandAndWait(cmdCommands);
			s = WindowsProcess.streamOutput(proc);
		} else if (OSValidator.isMac()) {
			String terminalCommand = String.format("%sserver=%s&user=%s&password=%s&domain=%s", NETEXTENDER_CLI_MAC,
					host, user, password, domain);
			String[] terminalCommands = new String[] { "/bin/bash", "-c", terminalCommand };
			LOGGER.info("Command used in Terminal :\n{}", terminalCommand.replace(password, "**********"));
			String[] kill = {"pkill netExtender"};
			proc = WindowsProcess.runCommandAndWait(kill);
			s = WindowsProcess.streamOutput(WindowsProcess.runCommandAndWait(terminalCommands));
		} else if (OSValidator.isUnix()) {
			String terminalCommand = String.format("%s -s %s -u %s -p %s -d %s --always-trust %s", NETEXTENDER_CLI_LINUX,
					host, user, password, domain, host);
			String[] terminalCommands = new String[] { "/bin/bash", "-c", terminalCommand };
			LOGGER.info("Command used in Terminal :\n{}", terminalCommand.replace(password, "**********"));
			s = WindowsProcess.streamOutput(WindowsProcess.runCommandAndWait(terminalCommands));
		}

		return s;
	}

	public static void disconnect() {
		try {
			if (OSValidator.isWindows()) {
				String cmdcommand = NETEXTENDER_CLI_WINDOWS + " disconnect";
				String[] cmdCommands = new String[] { "cmd", "/C", cmdcommand };
				Process p = WindowsProcess.runCommandAndWait(cmdCommands);
				WindowsProcess.streamOutput(p);
			} else if (OSValidator.isMac()) {
				String terminalCommand = NETEXTENDER_CLI_MAC + " disconnect";
				String[] terminalCommands = new String[] { "/bin/bash", "-c", terminalCommand };
				WindowsProcess.streamOutput(WindowsProcess.runCommandAndWait(terminalCommands));
			} else if (OSValidator.isUnix()) {
				String terminalCommand = NETEXTENDER_CLI_LINUX + " disconnect";
				String[] terminalCommands = new String[] { "/bin/bash", "-c", terminalCommand };
				WindowsProcess.streamOutput(WindowsProcess.runCommandAndWait(terminalCommands));
			}
			LOGGER.info("Disabled NetExtender VPN.");
		} catch (Exception e) {
			LOGGER.error("NetExtender VPN was not disconnected.", e);
		}
	}

}