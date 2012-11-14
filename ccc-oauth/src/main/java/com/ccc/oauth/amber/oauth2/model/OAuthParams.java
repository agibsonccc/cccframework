/**
 *       Copyright 2010 Newcastle University
 *
 *          http://research.ncl.ac.uk/smart/
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ccc.oauth.amber.oauth2.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * @author Maciej Machulak (m.p.machulak@ncl.ac.uk)
 * @author Lukasz Moren (lukasz.moren@ncl.ac.uk)
 * @author Aad van Moorsel (aad.vanmoorsel@ncl.ac.uk)
 */
@Entity
@Table(name="oauthparams")
public class OAuthParams implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -371573642940866828L;
	@Id
	@Column(name="clientId")
	private String clientId;
	@Column(name="clientSecret")
	private String clientSecret;
	@Column(name="redirect_uri")
	private String redirectUri;
	@Column(name="authz_endpoint")
	private String authzEndpoint;
	@Column(name="tokenendpoint")
	private String tokenEndpoint;
	@Column(name="authz_code")
	private String authzCode;
	@Column(name="access_token") 
	private String accessToken;
	@Column(name="expires_in")
	private String expiresIn;
	@Column(name="refresh_token")
	private String refreshToken;
	@Column(name="scope")
	private String scope;
	@Column(name="resource_url")
	private String resourceUrl;
	@Column(name="resource")
	private String resource;
	@Column(name="application")
	private String application;
	@Column(name="error_message")
	private String errorMessage;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accessToken == null) ? 0 : accessToken.hashCode());
		result = prime * result
				+ ((application == null) ? 0 : application.hashCode());
		result = prime * result
				+ ((authzCode == null) ? 0 : authzCode.hashCode());
		result = prime * result
				+ ((authzEndpoint == null) ? 0 : authzEndpoint.hashCode());
		result = prime * result
				+ ((clientId == null) ? 0 : clientId.hashCode());
		result = prime * result
				+ ((clientSecret == null) ? 0 : clientSecret.hashCode());
		result = prime * result
				+ ((errorMessage == null) ? 0 : errorMessage.hashCode());
		result = prime * result
				+ ((expiresIn == null) ? 0 : expiresIn.hashCode());
		result = prime * result
				+ ((redirectUri == null) ? 0 : redirectUri.hashCode());
		result = prime * result
				+ ((refreshToken == null) ? 0 : refreshToken.hashCode());
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
		result = prime * result
				+ ((resourceUrl == null) ? 0 : resourceUrl.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		result = prime * result
				+ ((tokenEndpoint == null) ? 0 : tokenEndpoint.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OAuthParams other = (OAuthParams) obj;
		if (accessToken == null) {
			if (other.accessToken != null)
				return false;
		} else if (!accessToken.equals(other.accessToken))
			return false;
		if (application == null) {
			if (other.application != null)
				return false;
		} else if (!application.equals(other.application))
			return false;
		if (authzCode == null) {
			if (other.authzCode != null)
				return false;
		} else if (!authzCode.equals(other.authzCode))
			return false;
		if (authzEndpoint == null) {
			if (other.authzEndpoint != null)
				return false;
		} else if (!authzEndpoint.equals(other.authzEndpoint))
			return false;
		if (clientId == null) {
			if (other.clientId != null)
				return false;
		} else if (!clientId.equals(other.clientId))
			return false;
		if (clientSecret == null) {
			if (other.clientSecret != null)
				return false;
		} else if (!clientSecret.equals(other.clientSecret))
			return false;
		if (errorMessage == null) {
			if (other.errorMessage != null)
				return false;
		} else if (!errorMessage.equals(other.errorMessage))
			return false;
		if (expiresIn == null) {
			if (other.expiresIn != null)
				return false;
		} else if (!expiresIn.equals(other.expiresIn))
			return false;
		if (redirectUri == null) {
			if (other.redirectUri != null)
				return false;
		} else if (!redirectUri.equals(other.redirectUri))
			return false;
		if (refreshToken == null) {
			if (other.refreshToken != null)
				return false;
		} else if (!refreshToken.equals(other.refreshToken))
			return false;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		if (resourceUrl == null) {
			if (other.resourceUrl != null)
				return false;
		} else if (!resourceUrl.equals(other.resourceUrl))
			return false;
		if (scope == null) {
			if (other.scope != null)
				return false;
		} else if (!scope.equals(other.scope))
			return false;
		if (tokenEndpoint == null) {
			if (other.tokenEndpoint != null)
				return false;
		} else if (!tokenEndpoint.equals(other.tokenEndpoint))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OAuthParams [clientId=" + clientId + ", clientSecret="
				+ clientSecret + ", redirectUri=" + redirectUri
				+ ", authzEndpoint=" + authzEndpoint + ", tokenEndpoint="
				+ tokenEndpoint + ", authzCode=" + authzCode + ", accessToken="
				+ accessToken + ", expiresIn=" + expiresIn + ", refreshToken="
				+ refreshToken + ", scope=" + scope + ", resourceUrl="
				+ resourceUrl + ", resource=" + resource + ", application="
				+ application + ", errorMessage=" + errorMessage + "]";
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

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getAuthzEndpoint() {
		return authzEndpoint;
	}

	public void setAuthzEndpoint(String authzEndpoint) {
		this.authzEndpoint = authzEndpoint;
	}

	public String getTokenEndpoint() {
		return tokenEndpoint;
	}

	public void setTokenEndpoint(String tokenEndpoint) {
		this.tokenEndpoint = tokenEndpoint;
	}

	public String getAuthzCode() {
		return authzCode;
	}

	public void setAuthzCode(String authzCode) {
		this.authzCode = authzCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}
}
