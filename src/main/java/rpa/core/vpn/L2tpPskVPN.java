package rpa.core.vpn;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.driver.G;
import rpa.core.exceptions.BishopVPNException;
import rpa.core.file.OSValidator;
import rpa.core.windowsprocess.WindowsProcess;

public class L2tpPskVPN {
	private static Logger LOGGER = LoggerFactory.getLogger(L2tpPskVPN.class);

	
	public static String connect(String host, String psk, String user, String password)
			throws Exception, IOException, InterruptedException {
		String s = null;
		Process proc = null;
		if (OSValidator.isWindows()) {
			String connectionScript = String.format(""
					+ "$ServerAddress = \"%s\"\n"
					+ "$ConnectionName = \"%s\"\n"
					+ "$PresharedKey = \"%s\"\n"
					+ "$username = \"%s\"\n"
					+ "$plainpassword= \"%s\"\n"
					+ "try {\n"
					+ "    Add-VpnConnection -Name \"$ConnectionName\" -ServerAddress \"$ServerAddress\" -TunnelType L2tp -L2tpPsk \"$PresharedKey\" -RememberCredential -EncryptionLevel \"Optional\" -AuthenticationMethod Pap,MsChapv2 -Force\n"
					+ "}\n"
					+ "catch {\n"
					+ "    Write-Host \"An Error Occured: $Error\" -ForegroundColor RED\n"
					+ "}\n"
					+ "finally {\n"
					+ "    $Error.Clear()\n"
					+ "}\n"
//					+ "Set-VpnConnection -Name $ConnectionName -SplitTunneling $True\n"
					+ "Set-VpnConnectionUsernamePassword -connectionname $ConnectionName -username $username -password $plainpassword\n"
					+ "$vpn = Get-VpnConnection -Name $ConnectionName;\n"
					+ "if($vpn.ConnectionStatus -eq \"Disconnected\") {\n"
					+ "    rasdial $ConnectionName $username $plainpassword;\n"
					+ "} \n"
					+ "", host, G.executionMetrics.getBishopAccount(), psk, user, password);
			Path tempFile = Files.createTempFile("l2tpvpn", ".ps1");
			System.out.println("connectionScript: " + connectionScript.replace(password, "********"));
			System.out.println("Writing connection script to:  " + tempFile);
			Files.write(tempFile, connectionScript.getBytes(StandardCharsets.UTF_8));
			String command[] = {"powershell.exe",  tempFile.toString()};
			proc  = WindowsProcess.runCommandAndWait(command);
			proc.getOutputStream().close();
			s = WindowsProcess.streamOutput(proc);
			System.out.println(s);
			if (!StringUtils.containsIgnoreCase(s, "Successfully connected to " + G.executionMetrics.getBishopAccount())
					|| (StringUtils.containsIgnoreCase(s, "An Error Occured") && !StringUtils.containsIgnoreCase(s, "This VPN connection has already been created"))) 
				throw new BishopVPNException("L2TP PSK VPN connection not successful.\n" + s);
		} else if (OSValidator.isMac()) {
			throw new BishopVPNException("L2TP PSK VPN connection is not supported on Mac");
		} else if (OSValidator.isUnix()) {
			throw new BishopVPNException("L2TP PSK VPN connection is not supported on Linux");
		}
		return s;
	}

	public static void disconnect() {
		try {
			if (OSValidator.isWindows()) {
				String connectionScript = String.format("$ConnectionName = \"%s\"\n"
						+ "$vpn = Get-VpnConnection -Name $ConnectionName;\n"
						+ "if($vpn.ConnectionStatus -eq \"Connected\"){\n"
						+ "		rasdial $ConnectionName /DISCONNECT;\n"
						+ "}", G.executionMetrics.getBishopAccount());
				Path tempFile = Files.createTempFile("l2tpvpn", ".ps1");
				System.out.println("Writing connection script to:  " + tempFile);
				Files.write(tempFile, connectionScript.getBytes(StandardCharsets.UTF_8));
				String command[] = {"powershell.exe",  tempFile.toString()};
				Process proc  = WindowsProcess.runCommandAndWait(command);
				proc.getOutputStream().close();
				String s = WindowsProcess.streamOutput(proc);
				if(!StringUtils.containsIgnoreCase(s, "The system could not find the phone book"))
					System.out.println(s);
				System.out.println("L2TP PSK VPN connection disconnected successfully");
			} else if (OSValidator.isMac()) {
				System.out.println("L2TP PSK VPN connection is not supported on Mac");
			} else if (OSValidator.isUnix()) {
				System.out.println("L2TP PSK VPN connection is not supported on Linux");
			}
			LOGGER.info("Disabled L2tp ipsec VPN.");
		} catch (Exception e) {
			LOGGER.error("NetExtender VPN was not disconnected.", e);
		}
	}

}