package com.ccc.mail.core.servers;

import java.io.Serializable;
import java.util.Properties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.ccc.util.filesystem.PropertyFileUtil;

@Entity
@Table(name="server")
public class Server implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2305132685318882753L;

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + port;
		result = prime * result
				+ ((serverAddress == null) ? 0 : serverAddress.hashCode());
		result = prime * result + serverId;
		result = prime * result
				+ ((serverName == null) ? 0 : serverName.hashCode());
		result = prime * result
				+ ((serverType == null) ? 0 : serverType.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Server)) {
			return false;
		}
		Server other = (Server) obj;
		if (port != other.port) {
			return false;
		}
		if (serverAddress == null) {
			if (other.serverAddress != null) {
				return false;
			}
		} else if (!serverAddress.equals(other.serverAddress)) {
			return false;
		}
		if (serverId != other.serverId) {
			return false;
		}
		if (serverName == null) {
			if (other.serverName != null) {
				return false;
			}
		} else if (!serverName.equals(other.serverName)) {
			return false;
		}
		if (serverType == null) {
			if (other.serverType != null) {
				return false;
			}
		} else if (!serverType.equals(other.serverType)) {
			return false;
		}
		return true;
	}


	public static Server configure(String serverName) {
		return configure(serverName,FILE_NAME);
	}
	/**
	 * This will configure the server with the given properties file. The following
	 * properties are valid:
	 * mail.serverName.encryptiontype : ssl,tls
	 * mail.serverName.port 
	 * mail.serverName.address default: 127.0.0.1
	 * mail.serverName.servertype
	 * @param serverName the name of the server to configure
	 * @param fileName the file name to load properties from
	 * @return the a configured server with the specified values
	 */
	public static Server configure(String serverName,String fileName) {
		Server s = new Server();
		Properties props=PropertyFileUtil.loadProperties(fileName);
		s.setEncryptionType(get("mail." + serverName + ".encryptiontype",props));
		s.setAuth(Boolean.parseBoolean(props.getProperty("mail." + serverName + ".auth","false")));
		s.setServerType(props.getProperty("mail." + serverName + ".servertype","imap"));
		s.setPort(Integer.parseInt(props.getProperty("mail." + serverName + ".port","143")));
		s.setServerAddress(props.getProperty("mail." + serverName + ".address","127.0.0.1"));
		s.setServerName(serverName);
		return s;
	}

	private static String get(String name,Properties properties) {
		return properties.getProperty(name);
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Server [serverId=" + serverId + ", serverName=" + serverName
				+ ", serverType=" + serverType + ", serverAddress="
				+ serverAddress + ", port=" + port + "]";
	}
	@NotNull
	@Column(name="server_type")
	private String serverType;
	@NotNull
	@Column(name="server_address")
	private String serverAddress;
	@NotNull
	@Column(name="port")
	private int port;

	@Column(name="encryption_type")
	protected String encryptionType;
	@Column(name="is_auth")
	private boolean isAuth;
	@Id
	@NotNull
	@Column(name="server_id")
	private int serverId;
	@NotNull
	@Column(name="server_name")
	private String serverName;

	public final static String FILE_NAME="server.properties";
	public boolean isAuth() {
		return isAuth;
	}
	public void setAuth(boolean isAuth) {
		this.isAuth = isAuth;
	}
	public int getServerId() {
		return serverId;
	}
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getServerType() {
		return serverType;
	}
	public void setServerType(String serverType) {
		this.serverType = serverType;
	}
	public String getServerAddress() {
		return serverAddress;
	}
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	public String getEncryptionType() {
		return encryptionType;
	}

	public void setEncryptionType(String encryptionType) {
		this.encryptionType = encryptionType;
	}
}//end Server
