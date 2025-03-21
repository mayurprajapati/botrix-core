package rpa.core.windowsprocess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.file.OSValidator;

public class WindowsProcess {
	private static Logger LOGGER = LoggerFactory.getLogger(WindowsProcess.class);

	public static void runCommandAndWait(String command) {
		runCommandAndWait(command, 30);
	}

	public static void runCommandAndWait(String command, int timeout) {
		Process proc;
		try {
			proc = Runtime.getRuntime().exec(command);
			proc.waitFor(timeout, TimeUnit.SECONDS);
			streamOutput(proc);
		} catch (IOException e) {
			LOGGER.error("", e);
		} catch (InterruptedException e) {
			LOGGER.error("", e);
		}
	}

	/**
	 * @param processes - Names of process to kill.
	 */
	public static void killWindowsProcess(String[] processes) {
		Process proc = null;
		try {
			if (OSValidator.isWindows()) {
				for (String stringProcess : processes) {
					String command = "taskkill /F /FI \"USERNAME eq %username%\" /IM " + stringProcess;
					String[] cmdCommands = new String[] { "cmd", "/C", command };
					proc = runCommandAndWait(cmdCommands);
					streamOutput(proc);
				}
				LOGGER.warn("All running process were stopped");
			}
		} catch (Exception e) {
			LOGGER.warn("all process was not stopped");
		}
	}

	/**
	 * To execute commands using different flags or application
	 * 
	 * @param commands to execute at once
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static Process runCommandAndWait(String[] commands) throws Exception {
		return runCommandAndWait(commands, 60);
	}

	public static Process runCommandAndWait(String[] commands, int timeout) throws Exception {
		Process proc;
		if (OSValidator.isWindows()) {
			if (!StringUtils.contains(commands[0], "cmd") && !StringUtils.contains(commands[0], "powershell")) {
				String[] cmd = { "cmd", "/C" };
				commands = ArrayUtils.addAll(cmd, commands);
			}
		} else {
			if (!StringUtils.contains(commands[0], "/bin/bash")) {
				String[] cmd = { "/bin/bash", "-c" };
				commands = ArrayUtils.addAll(cmd, commands);
			}
		}
		proc = Runtime.getRuntime().exec(commands);
		if (timeout == 0) {
			proc.waitFor();
		} else {
			proc.waitFor(timeout, TimeUnit.SECONDS);
		}
		return proc;
	}

	public static void killWindowsProcess(String stringProcess) {
		try {
			if (OSValidator.isWindows()) {
				String command = "taskkill /F /FI \"USERNAME eq %username%\" /IM " + stringProcess;
				String[] cmdCommands = new String[] { "cmd", "/C", command };
				streamOutput(runCommandAndWait(cmdCommands));
				LOGGER.warn("All already running process were stopped");
			} else {
				String command = "pkill " + stringProcess;
				String[] cmdCommands = new String[] { "/bin/bash", "-c", command };
				streamOutput(runCommandAndWait(cmdCommands));
				LOGGER.warn("All already running process were stopped");
			}
		} catch (Exception e) {
			LOGGER.warn("all process was not stopped");
		}
	}

	public String getOutputUsingCommand(String command) {
		Process process;
		String finalString = StringUtils.EMPTY;
		try {
			process = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String s;
			while ((s = reader.readLine()) != null) {
				finalString = finalString + s + "\n";
			}
		} catch (IOException e) {
			return "";
		}
		return finalString;
	}

	public static String streamOutput(Process p) throws IOException {
		return streamOutput(p, true);
	}

	public static String streamOutput(Process p, boolean enableLogger) throws IOException {
		String s;
		StringBuilder insBuilder = new StringBuilder();
		StringBuilder errsBuilder = new StringBuilder();
		Scanner in = new Scanner(p.getInputStream());
		Scanner err = new Scanner(p.getErrorStream());
		System.out.println("Reading streams..");
		while (in.hasNextLine()) {
			s = in.nextLine();
			if (StringUtils.isNotBlank(s))
				insBuilder.append(s).append(System.lineSeparator());
		}
		while (err.hasNextLine()) {
			s = err.nextLine();
			if (StringUtils.isNotBlank(s))
				errsBuilder.append(s).append(System.lineSeparator());
		}
		if (StringUtils.isNotBlank(errsBuilder)) {
			LOGGER.error("Error for the command : " + errsBuilder);
		}
		in.close();
		err.close();
		String streamOutput = insBuilder.toString() + System.lineSeparator() + errsBuilder.toString();
		if (enableLogger) {
			LOGGER.info("Stream output >> " + streamOutput);
		}
		return streamOutput;
	}
}
