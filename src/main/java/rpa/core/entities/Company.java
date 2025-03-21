package rpa.core.entities;

import org.apache.commons.lang3.StringUtils;

import com.google.cloud.firestore.annotation.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Company {

	private String name = StringUtils.EMPTY;
	private String timezone = StringUtils.EMPTY;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
}
