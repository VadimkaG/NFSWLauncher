package ru.vadimka.nfswlauncher.protocol;

import ru.vadimka.nfswlauncher.AuthException;
import ru.vadimka.nfswlauncher.ValueObjects.Account;

public interface ServerInterface {
	
	public String get(String alias);
	
	public void login(Account acc) throws AuthException;
	
	public void register(String login, String password) throws AuthException;
	
	public String getLinkForgotPassword();
	
	public String getServerEngine();
	
	public ServerInterface getResponse();
	
	public boolean isOnline();
	
	public String getNameProtocol();
}
