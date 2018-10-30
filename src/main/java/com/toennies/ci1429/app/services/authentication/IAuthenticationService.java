package com.toennies.ci1429.app.services.authentication;

public interface IAuthenticationService {

	boolean login(String pw);

	boolean isLoggedIn();

}
