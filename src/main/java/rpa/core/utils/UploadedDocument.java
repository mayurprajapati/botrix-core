package rpa.core.utils;

import org.apache.commons.lang3.StringUtils;

public class UploadedDocument {
	private String id;
	private String url;
	private String botActivity = StringUtils.EMPTY;
	private String fileGroup;
	private String filePath;
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBotActivity() {
		return botActivity;
	}

	public void setBotActivity(String botActivity) {
		this.botActivity = botActivity;
	}

	public String getFileGroup() {
		return fileGroup;
	}

	public void setFileGroup(String fileGroup) {
		this.fileGroup = fileGroup;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
