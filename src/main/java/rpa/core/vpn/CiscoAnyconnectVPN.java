package rpa.core.vpn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.driver.G;
import rpa.core.driver.SystemProperties;
import rpa.core.exceptions.BishopVPNException;
import rpa.core.file.OSValidator;
import rpa.core.windowsprocess.WindowsProcess;

public class CiscoAnyconnectVPN {
	private static Logger LOGGER = LoggerFactory.getLogger(CiscoAnyconnectVPN.class);
	public static final String ANYCONNECT_CLI_WINDOWS = "\"C:\\Program Files (x86)\\Cisco\\Cisco AnyConnect Secure Mobility Client\\vpncli.exe\"";
	public static final String ANYCONNECT_CLI_MAC = "/opt/cisco/anyconnect/bin/vpn";

	public static boolean isAnyConnectEnabled() throws Exception {
		String s = null;
		Process proc = null;
		boolean state = true;
		try {
			if (OSValidator.isWindows()) {
				String cmdcommand = ANYCONNECT_CLI_WINDOWS + " -s state";
				String[] cmdCommands = new String[] { "cmd", "/C", cmdcommand };
				proc = runCommandAndWait(cmdCommands);
			} else if (OSValidator.isMac()) {
				String terminalCommand = ANYCONNECT_CLI_MAC + " -s state";
				String[] terminalCommands = new String[] { "/bin/bash", "-c", terminalCommand };
				proc = runCommandAndWait(terminalCommands);
			} else if (OSValidator.isUnix()) {
				return false;
			}
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			StringBuilder sb = new StringBuilder();
			LOGGER.info("Command Output :");
			while ((s = stdInput.readLine()) != null) {
				if (StringUtils.isNotBlank(s))
					sb.append(s).append(System.lineSeparator());
				if (StringUtils.containsIgnoreCase(s, "state: Disconnected")) {
					state = false;
				} else if (StringUtils.containsIgnoreCase(s, "state: Connected")) {
					state = true;
				}
			}
			LOGGER.info(sb.toString());
		} catch (Exception e) {
			throw new BishopVPNException("AnyConnect timed out. AnyConnect VPN not available.", e);
		}
		return state;
	}

	public static void enableAnyConnect(String host, String group, String user, String password) throws Exception {
		String vpnProfile = SystemProperties.DEFAULT_DOWNLOAD_LOCATION + File.separator
				+ String.format("anyconnect-%s-profile.txt", G.executionMetrics.getBishopAccount());

		if (!OSValidator.isUnix()) {
			try {
				if (!isAnyConnectEnabled()) {
					String streamFromTermial = writeProfileAndConnect(host, group, user, password, vpnProfile);
					if (!isAnyConnectEnabled()) {
						Pattern groupNumberPattern = Pattern
								.compile("(\\d)\\)\\s+?${GROUP_NAME}".replace("${GROUP_NAME}", group));
						Matcher m = groupNumberPattern.matcher(streamFromTermial);
						if (m.find()) {
							String out = writeProfileAndConnect(host, m.group(1), user, password,
									FilenameUtils.getFullPath(vpnProfile) + "1" + FilenameUtils.getName(vpnProfile));
							if (StringUtils.contains(out,
									"VPN establishment capability for a remote user is disabled.  A VPN connection will not be established."))
								throw new BishopVPNException(
										"VPN establishment capability for a remote user is disabled.  A VPN connection will not be established.");
						} else {
							LOGGER.error("Group number not identified from terminal logs. Updation neeed");
						}
					}
				} else {
					LOGGER.info("Anyconnect VPN is already enabled.");
				}
			} catch (BishopVPNException e) {
				throw e;
			} catch (Exception e) {
				throw new BishopVPNException("Anyconnect VPN connection was unsuccessful", e);
			}
			if (!isAnyConnectEnabled()) {
				throw new BishopVPNException("AnyConnect timed out. AnyConnect VPN not available.");
			} else {
				G.executionMetrics.setVpn(true);
			}

		} else {
//			For debugging use --dump-http-traffic flag to print details to console
//			Some characters like $ needs to be escaped with a \
			String servercert = VPNUtils.getServerShaFingerprint(host);
			String terminalCommand = String.format(
					"echo -n \"%s\" | openconnect --protocol=anyconnect %s --background --user=%s --authgroup=%s%s--passwd-on-stdin",
					password.replace("$", "\\$"), host, user, group, servercert);
			if (StringUtils.isBlank(group))
				terminalCommand = String.format(
						"echo -n \"%s\" | openconnect --protocol=anyconnect %s --background --user=%s%s--passwd-on-stdin",
						password.replace("$", "\\$"), host, user, servercert);
			String[] terminalCommands = new String[] { "/bin/bash", "-c", terminalCommand };
			LOGGER.info("Command used in Terminal :\n{}", terminalCommand.replace(password, "**********"));
			Process proc = runCommandAndWait(terminalCommands);
			String streamOutput = WindowsProcess.streamOutput(proc);
			if (StringUtils.containsIgnoreCase(streamOutput, "Session authentication will expire")
					|| StringUtils.containsIgnoreCase(streamOutput, "Established DTLS connection")
					|| StringUtils.containsIgnoreCase(streamOutput, "Continuing in background")) {
				LOGGER.info("Connection successful to AnyConnect");
				G.executionMetrics.setVpn(true);
			} else {
				throw new BishopVPNException("Unable to connect to AnyConnect. " + streamOutput);
			}

			G.executionMetrics.setVpn(true);
		}

	}

	public static String writeProfileAndConnect(String host, String group, String user, String password,
			String vpnProfile) throws Exception, IOException, InterruptedException {
		String s;
		Process proc = null;
		writeAnyConnectProfile(host, group, user, password, vpnProfile);
		if (OSValidator.isWindows()) {
			String cmdcommand = ANYCONNECT_CLI_WINDOWS + " -s < " + vpnProfile.replace(" ", "^ ");
			String[] cmdCommands = new String[] { "cmd", "/C", cmdcommand };
			proc = runCommandAndWait("taskkill /F /IM vpncli.exe");
			proc = runCommandAndWait("taskkill /F /IM vpnui.exe");
			LOGGER.info("Command used in CMD:\n{}", cmdcommand);
			proc = runCommandAndWait(cmdCommands);
		} else if (OSValidator.isMac()) {
			String terminalCommand = ANYCONNECT_CLI_MAC + " -s < " + vpnProfile;
			String[] terminalCommands = new String[] { "/bin/bash", "-c", terminalCommand };
			LOGGER.info("Command used in Terminal :\n{}", terminalCommand);
			proc = runCommandAndWait("pkill AnyConnect");
			proc = runCommandAndWait(terminalCommands);
		}

		String out = WindowsProcess.streamOutput(proc);
		LOGGER.info("Stream output >> " + out);
		return out;
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

	public static void writeAnyConnectProfile(String host, String group, String user, String password, String filepath)
			throws Exception {
		try (FileWriter writer = new FileWriter(filepath); BufferedWriter bw = new BufferedWriter(writer)) {
			List<String> list = new ArrayList<String>();
			list.add("connect " + host);
			list.add(group);
			list.add(user);
			list.add(password);
			list.add("y");
			list.removeAll(Collections.singleton(""));
			Files.write(Paths.get(filepath), list);
		} catch (Exception e) {
			throw new BishopVPNException("Anyconnect vpn profile creation failed", e);
		}
	}

	public static void disableAnyConnect() {
		try {
			if (isAnyConnectEnabled()) {
				if (OSValidator.isWindows()) {
					String cmdcommand = ANYCONNECT_CLI_WINDOWS + " -s disconnect";
					String[] cmdCommands = new String[] { "cmd", "/C", cmdcommand };
					WindowsProcess.streamOutput(runCommandAndWait(cmdCommands));
				} else if (OSValidator.isMac()) {
					String terminalCommand = ANYCONNECT_CLI_MAC + " -s disconnect";
					String[] terminalCommands = new String[] { "/bin/bash", "-c", terminalCommand };
					WindowsProcess.streamOutput(runCommandAndWait(terminalCommands));
				} else if (OSValidator.isUnix()) {
					String terminalCommand = "pkill openconnect";
					String[] terminalCommands = new String[] { "/bin/bash", "-c", terminalCommand };
					WindowsProcess.streamOutput(runCommandAndWait(terminalCommands));
				}
				LOGGER.info("Disabled AnyConnect VPN.");
			}
		} catch (Exception e) {
			LOGGER.error("Anyconnect VPN was not disconnected.", e);
		}
	}

}