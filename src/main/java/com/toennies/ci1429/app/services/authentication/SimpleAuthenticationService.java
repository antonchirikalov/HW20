package com.toennies.ci1429.app.services.authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Represents a very simple authentication service. Just checks given pw with
 * local defined password.
 */
@Component
@Scope("session")
public class SimpleAuthenticationService implements IAuthenticationService {

	@Value("${authenticationservice.pssword}")
	private String loginPw;

	// Through this config parameter, hw20 doesn't ask for login if following
	// parameter is set to true in .yml
	@Value("${authenticationservice.alreadyloggedin}")
	private boolean loggedIn = false;

	@Override
	public boolean login(String pw) {
		if (loginPw.equalsIgnoreCase(pw)) {
			loggedIn = true;
		}
		return isLoggedIn();
	}

	@Override
	public boolean isLoggedIn() {
		return loggedIn;
	}

}
