package com.example.assistio.security;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import java.util.Collections;
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final String principal;
    private final String credentials;
    public JwtAuthenticationToken(String principal, String credentials) {
        super(null); 
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }
    @Override
    public Object getCredentials() {
        return credentials; 
    }
    @Override
    public Object getPrincipal() {
        return principal;
    }
    @Override
    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            throw new UnsupportedOperationException("Authentication cannot be changed on a JwtAuthenticationToken.");
        }
        super.setAuthenticated(authenticated);
    }
}
