package rpa.core.entities;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;

import lombok.Getter;
import lombok.Setter;

@IgnoreExtraProperties
public class Credentials {
	private String BishopAccount;
	private String company;
	private String createdBy;
	private Timestamp createdOn;
	private String credentialId;
	private Object email;
	private String firstname;
	private String lastname;
	private String loginUrl;
	private String password;
	private String system;
	private String username;
	private VPNInfo vpnInfo;
	private APIInfo apiInfo;
	@Getter @Setter private ArrayList<String> linkedFlows = new ArrayList<>();;

	public APIInfo getApiInfo() {
		return apiInfo;
	}

	public void setApiInfo(APIInfo apiInfo) {
		this.apiInfo = apiInfo;
	}

	public static class APIInfo {
		private String clientId;
		private String clientSecret;
		private String companyId;

		public String getCompanyId() {
			return companyId;
		}

		public void setCompanyId(String companyId) {
			this.companyId = companyId;
		}

		@Override
		public String toString() {
			return "APIInfo [clientId=" + clientId + ", clientSecret=" + clientSecret + ", companyId=" + companyId
					+ "]";
		}

		public String getClientId() {
			return clientId;
		}

		public void setClientId(String clientId) {
			this.clientId = clientId;
		}

		public String getClientSecret() {
			return clientSecret;
		}

		public void setClientSecret(String clientSecret) {
			this.clientSecret = clientSecret;
		}
	}

	public static class VPNInfo {
		private String connectionSoftware = StringUtils.EMPTY;
		private String domainServerUrl = StringUtils.EMPTY;
		private String groupName = StringUtils.EMPTY;
		private String password = StringUtils.EMPTY;
		private String username = StringUtils.EMPTY;
		private String port = StringUtils.EMPTY;
		private String configFileName = StringUtils.EMPTY;

		public String getConfigFileName() {
			return configFileName;
		}

		public void setConfigFileName(String configFileName) {
			this.configFileName = configFileName;
		}

		public String getConnectionSoftware() {
			return connectionSoftware;
		}

		public void setConnectionSoftware(String connectionSoftware) {
			this.connectionSoftware = connectionSoftware;
		}

		public String getDomainServerUrl() {
			return domainServerUrl;
		}

		public void setDomainServerUrl(String domainServerUrl) {
			this.domainServerUrl = domainServerUrl;
		}

		public String getGroupName() {
			return groupName;
		}

		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPort() {
			return port;
		}

		public void setPort(String port) {
			this.port = port;
		}

		@Override
		public String toString() {
			return "VPNInfo [connectionSoftware=" + connectionSoftware + ", domainServerUrl=" + domainServerUrl
					+ ", groupName=" + groupName + ", password=" + password + ", username=" + username + ", port="
					+ port + ", configFileName=" + configFileName + "]";
		}
	}

	public String getBishopAccount() {
		return BishopAccount;
	}

	public void setBishopAccount(String BishopAccount) {
		this.BishopAccount = BishopAccount;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public String getCredentialId() {
		return credentialId;
	}

	public void setCredentialId(String credentialId) {
		this.credentialId = credentialId;
	}

	public Object getEmail() {
		return email;
	}

	public void setEmail(Object email) {
		this.email = email;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public VPNInfo getVpnInfo() {
		if (vpnInfo != null && StringUtils.isBlank(vpnInfo.getConnectionSoftware()))
			vpnInfo = null;
		return vpnInfo;
	}

	public void setVpnInfo(VPNInfo vpnInfo) {
		this.vpnInfo = vpnInfo;
	}

	@Override
	public String toString() {
		return "Credentials [BishopAccount=" + BishopAccount + ", company=" + company + ", createdBy=" + createdBy
				+ ", createdOn=" + createdOn + ", credentialId=" + credentialId + ", email=" + email + ", firstname="
				+ firstname + ", lastname=" + lastname + ", loginUrl=" + loginUrl + ", password=" + "********"
				+ ", system=" + system + ", username=" + username + ", vpnInfo=" + vpnInfo + ", apiInfo=" + apiInfo
				+ "]";
	}
}
