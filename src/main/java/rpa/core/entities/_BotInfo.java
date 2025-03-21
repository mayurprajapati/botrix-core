package rpa.core.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.cloud.firestore.annotation.Exclude;
import com.google.cloud.firestore.annotation.PropertyName;

import lombok.Getter;
import lombok.Setter;

public class _BotInfo {
	private String botClass = StringUtils.EMPTY;
	private String action = StringUtils.EMPTY;
	private String object = StringUtils.EMPTY;
	private String system = StringUtils.EMPTY;
	private String environment = StringUtils.EMPTY;
	private String projectName = StringUtils.EMPTY;
	private String company = StringUtils.EMPTY;
	private String firstName = StringUtils.EMPTY;
	private String lastName = StringUtils.EMPTY;
	private String userId = StringUtils.EMPTY;
	private String password = StringUtils.EMPTY;
	private String loginURL = StringUtils.EMPTY;
	private String databaseHostPort = StringUtils.EMPTY;
	private String email = StringUtils.EMPTY;
	private String credential = StringUtils.EMPTY;
	@Getter @Setter private Map<String, Object> databaseQueries = new HashMap<>();
	private String customAttributeMapping = StringUtils.EMPTY;
	private String id = StringUtils.EMPTY;

	// to skip current bot's run
	private boolean shouldSkipRun = false;

	/**
	 * bot can be picked using bots array's index or this index <br>
	 * {@link #index} has given more priority over bots array's index to make it
	 * more editable & organizable
	 */
	@PropertyName("_index")
	private int index = -1;

	/**
	 * {@link #dependsOn} can store only 1 bot <br>
	 * because one bot can only depend on other single bot <br>
	 * But, single bot can have multiple dependents. So same data can be processed
	 * again & again by multiple dependents
	 * <p>
	 * DynamicFlow gives ability to use dependency system by using many-to-one
	 * relationship. <br>
	 * Here many-to-one means, many bots can depend on one bot
	 * <p>
	 */
	private List<_BotInfo> dependsOn = new ArrayList<>(1);

	public String getDatabaseHostPort() {
		return databaseHostPort;
	}

	public void setDatabaseHostPort(String databaseHostPort) {
		this.databaseHostPort = databaseHostPort;
	}

	public String getCustomAttributeMapping() {
		return customAttributeMapping;
	}

	public _BotInfo setCustomAttributeMapping(String customAttributeMapping) {
		this.customAttributeMapping = customAttributeMapping;
		return this;
	}

	public String getBotClass() {
		return botClass;
	}

	@Exclude
	public _BotInfo dependsOn(_BotInfo other) {
		this.dependsOn.add(other);
		return this;
	}

	@Exclude
	public List<_BotInfo> getDependsOn() {
		return this.dependsOn;
	}

	public _BotInfo setBotClass(String bot) {
		this.botClass = bot;
		return this;
	}

	public String getAction() {
		return action;
	}

	public _BotInfo setAction(String action) {
		this.action = action;
		return this;
	}

	public String getObject() {
		return object;
	}

	public _BotInfo setObject(String object) {
		this.object = object;
		return this;
	}

	public String getSystem() {
		return system;
	}

	public _BotInfo setSystem(String system) {
		this.system = system;
		return this;
	}

	public String getEnvironment() {
		return environment;
	}

	public _BotInfo setEnvironment(String environment) {
		this.environment = environment;
		return this;
	}

	public String getProjectName() {
		return projectName;
	}

	public _BotInfo setProjectName(String projectName) {
		this.projectName = projectName;
		return this;
	}

	public String getCompany() {
		return company;
	}

	public _BotInfo setCompany(String company) {
		this.company = company;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public _BotInfo setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public _BotInfo setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public _BotInfo setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public _BotInfo setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getLoginURL() {
		return loginURL;
	}

	public _BotInfo setLoginURL(String loginURL) {
		this.loginURL = loginURL;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public _BotInfo setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getCredential() {
		return credential;
	}

	public _BotInfo setCredential(String credential) {
		this.credential = credential;
		return this;
	}

	public String getId() {
		return id;
	}

	public void constructBotId(int botIndex) {
		this.id = botIndex + "_" + botClass;
	}

	public boolean getShouldSkipRun() {
		return shouldSkipRun;
	}

	public void setShouldSkipRun(boolean shouldSkipRun) {
		this.shouldSkipRun = shouldSkipRun;
	}

	@PropertyName("_index")
	public int getIndex() {
		return index;
	}

	@PropertyName("_index")
	public void setIndex(int index) {
		this.index = index;
	}
}