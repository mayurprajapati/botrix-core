package rpa.core.file;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.driver.G;

public class ImageUtils {

	private static Logger LOGGER = LoggerFactory.getLogger(ImageUtils.class);

	public static void saveImage(String imageUrl, String destpath) throws IOException {
		String download_script = "var uri = arguments[0];" + "var callback = arguments[1];"
				+ "var callback = arguments[1];"
				+ "var toBase64 = function(buffer){for(var r,n=new Uint8Array(buffer),t=n.length,a=new Uint8Array(4*Math.ceil(t/3)),i=new Uint8Array(64),o=0,c=0;64>c;++c)i[c]=\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/\".charCodeAt(c);for(c=0;t-t%3>c;c+=3,o+=4)r=n[c]<<16|n[c+1]<<8|n[c+2],a[o]=i[r>>18],a[o+1]=i[r>>12&63],a[o+2]=i[r>>6&63],a[o+3]=i[63&r];return t%3===1?(r=n[t-1],a[o]=i[r>>2],a[o+1]=i[r<<4&63],a[o+2]=61,a[o+3]=61):t%3===2&&(r=(n[t-2]<<8)+n[t-1],a[o]=i[r>>10],a[o+1]=i[r>>4&63],a[o+2]=i[r<<2&63],a[o+3]=61),new TextDecoder(\"ascii\").decode(a)};\r\n"
				+ "var xhr = new XMLHttpRequest();" + "xhr.responseType = 'arraybuffer';"
				+ "xhr.onload = function(){ callback(toBase64(xhr.response)) };"
				+ "xhr.onerror = function(){ callback(xhr.status) };" + "xhr.open('GET', uri);" + "xhr.send();";
		Object result = G.jse.executeAsyncScript(download_script, imageUrl);
		String imgName = "/" + Calendar.getInstance().getTimeInMillis() + ".png";
		FileUtils.forceMkdir(new File(destpath));
		decodeToImage(String.valueOf(result), destpath + imgName);
	}

	private static BufferedImage decodeToImage(String imageString, String destpath) {
		BufferedImage image = null;
		byte[] imageByte;
		try {
			Base64.Decoder decoder = Base64.getDecoder();
			imageByte = decoder.decode(imageString);
			OutputStream out = null;
			try {
				out = new BufferedOutputStream(new FileOutputStream(destpath));
				out.write(imageByte);
			} catch (Exception e) {
				LOGGER.error("Failed to convert image" + e);
			} finally {
				if (out != null)
					out.close();
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return image;
	}
}
