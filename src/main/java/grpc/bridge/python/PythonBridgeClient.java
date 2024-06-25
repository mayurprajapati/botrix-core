package grpc.bridge.python;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.SneakyThrows;

public class PythonBridgeClient {
	private static ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052).usePlaintext().build();
	private static PythonBridgeClient client;

	@SneakyThrows
	private PythonBridgeClient() {
		File server = Paths.get(System.getenv("PYGRPC_SERVER_PATH")).toFile();
		ProcessBuilder processBuilder = new ProcessBuilder("python", server.getAbsolutePath());
		processBuilder.directory(server.getParentFile());
		processBuilder.redirectErrorStream(true);
		processBuilder.start().waitFor(2, TimeUnit.SECONDS);
	}

	public static PythonBridgeClient getInstance() {
		if (client == null) {
			client = new PythonBridgeClient();
		}
		return client;
	}

	public void resolveRecaptcha(String ip, int port) {
		var stub = PythonBridgeGrpc.newBlockingStub(channel);
		stub.resolveRecaptcha(DriverDebugAddressRequest.newBuilder().setIp(ip).setPort(port).build());
	}

	@SneakyThrows
	private static void installAndRegisterPythonGrpc() {
//		String pythonUrl = "https://www.python.org/ftp/python/3.9.0/python-3.9.0-embed-amd64.zip";
//		String pipUrl = "https://bootstrap.pypa.io/pip/pip.pyz";
//		String currentDir = Paths.get(System.getProperty("user.dir"), "python").toString();
//
//		try {
//			// Download and unzip Python
//			Path pythonZipPath = Paths.get(currentDir, "python-3.9.0.zip");
//			downloadFile(pythonUrl, pythonZipPath);
//			unzip(pythonZipPath.toString(), currentDir);
//			pythonPath = Paths.get(currentDir, "python.exe").toString();
//
//			// Download PIP
//			Path pipPath = Paths.get(currentDir, "pip.pyz");
//			downloadFile(pipUrl, pipPath);
//
//			// Configure PIP directory
//			Path pipConfigDir = Paths.get(currentDir, ".pip");
//			Files.createDirectories(pipConfigDir);
//			System.out.println("Python and PIP setup completed successfully.");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		Runtime.getRuntime().exec("python pygrpc/server.py");
	}

	public static void downloadFile(String url, Path destination) throws IOException {
		try (InputStream in = new URL(url).openStream()) {
			Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	public static void unzip(String zipFilePath, String destDir) throws IOException {
		File dir = new File(destDir);
		if (!dir.exists())
			dir.mkdirs();
		FileInputStream fis = new FileInputStream(zipFilePath);
		ZipInputStream zis = new ZipInputStream(fis);
		ZipEntry ze = zis.getNextEntry();
		while (ze != null) {
			String fileName = ze.getName();
			File newFile = new File(destDir + File.separator + fileName);
			if (ze.isDirectory()) {
				newFile.mkdirs();
			} else {
				new File(newFile.getParent()).mkdirs();
				try (FileOutputStream fos = new FileOutputStream(newFile)) {
					byte[] buffer = new byte[1024];
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
				}
			}
			zis.closeEntry();
			ze = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
		fis.close();
	}
}
