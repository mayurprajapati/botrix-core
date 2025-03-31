package rpa.core.vpn;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.driver.G;
import rpa.core.exceptions.BishopException;
import rpa.core.exceptions.BishopRuleViolationException;
import rpa.core.file.OSValidator;
import rpa.core.file.ParseUtils;
import rpa.core.windowsprocess.WindowsProcess;

public class FortiClientVPN {
	private static Logger LOGGER = LoggerFactory.getLogger(FortiClientVPN.class);

	public static void connect(String host, String user, String password) throws Exception {
		try {
			String streamFromTermial = connectToVPN(host, user, password);
			if (StringUtils.containsIgnoreCase(streamFromTermial, "Session authentication will expire")
					|| StringUtils.containsIgnoreCase(streamFromTermial, "Established DTLS connection")
					|| StringUtils.isEmpty(streamFromTermial)) {
				LOGGER.info("Connection successful to FortiClient");
				G.executionMetrics.setVpn(true);
			} else {
				LOGGER.error("Unable to login to FortiClient");
				throw new BishopException("Unable to connect to FortiClient. " + streamFromTermial);
			}

		} catch (BishopException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("FortiClient VPN was not connected successfully", e);
			throw new Exception("FortiClient VPN was not connected successfully");
		}
	}

	public static String connectToVPN(String host, String user, String password)
			throws Exception, IOException, InterruptedException {
		String s;
		Process proc = null;
		if (OSValidator.isWindows()) {
			try {
				String fortiClientConnectionString = String.format(
						"\"C:\\Program Files\\Fortinet\\FortiClient\\FortiSSLVPNclient.exe\" connect -s %s -h %s -u %s:%s -i -m",
						G.executionMetrics.getBishopAccount(), host, user, password);
				String[] cmdCommands = new String[] { "cmd", "/C", fortiClientConnectionString };
				LOGGER.info(
						"VPN Connection string: " + Arrays.deepToString(cmdCommands).replace(password, "**********"));
				proc = Runtime.getRuntime().exec(cmdCommands);
				String pid = String.valueOf(proc.pid());
				if (StringUtils.isBlank(ParseUtils.trimToEmpty(pid))) {
					LOGGER.error("FortiClient VPN not connected");
					throw new BishopRuleViolationException("FortiClient VPN not connected");
				}
				LOGGER.info("FortiClient VPN Connected. Running in pid " + pid);
				G.wait.sleep(10);
				return "";
			} catch (Exception e) {
				LOGGER.error("Forticlient VPN not connected", e);
				throw e;
			}
		} else if (OSValidator.isMac()) {
			throw new BishopRuleViolationException("FortiClient for MacOS is not supported");
		} else if (OSValidator.isUnix()) {
			String servercert = VPNUtils.getServerShaFingerprint(host);
			String terminalCommand = String.format(
					"echo -n \"%s\" | openconnect --protocol=fortinet %s --background --user=%s%s--passwd-on-stdin",
					password, host, user, servercert);
			String[] terminalCommands = new String[] { "/bin/bash", "-c", terminalCommand };
			LOGGER.info("Command used in Terminal :\n{}", terminalCommand.replace(password, "**********"));
			proc = runCommandAndWait(terminalCommands);
		}

		String streamOutput = WindowsProcess.streamOutput(proc);
		return streamOutput;
	}

	public static Process runCommandAndWait(String[] commands) throws IOException, InterruptedException {
		Process proc;
		proc = Runtime.getRuntime().exec(commands);
		proc.waitFor(60, TimeUnit.SECONDS);
		return proc;
	}

	public static Process runCommandAndWait(String command) throws IOException, InterruptedException {
		Process proc;
		proc = Runtime.getRuntime().exec(command);
		proc.waitFor();
		return proc;
	}

	public static void disconnect() {
		try {
			if (OSValidator.isWindows()) {
				String[] cmdCommands = new String[] { "cmd", "/C",
						"C:\\Program Files\\Fortinet\\FortiClient\\FortiSSLVPNclient.exe\" disconnect" };
				Process proc = WindowsProcess.runCommandAndWait(cmdCommands);
				int exitValue = proc.exitValue();
				if (exitValue != 0) {
					LOGGER.error("FortiClient VPN not disconnected");
					throw new BishopRuleViolationException("FortiClient VPN not disconnected");
				}
				WindowsProcess.streamOutput(proc);
			} else if (OSValidator.isMac()) {

			} else if (OSValidator.isUnix()) {
				String terminalCommand = "pkill openconnect";
				String[] terminalCommands = new String[] { "/bin/bash", "-c", terminalCommand };
				WindowsProcess.streamOutput(runCommandAndWait(terminalCommands));
			}
			LOGGER.info("Disabled FortiClient VPN.");
		} catch (Exception e) {
			LOGGER.error("FortiClient VPN was not disconnected.", e);
		}
	}

}