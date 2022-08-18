package org.openmbee.mms.mmsri.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.openmbee.mms.localuser.security.UserDetailsImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLCredential;

public class MMSSamlAuthenticationProvider extends SAMLAuthenticationProvider {

    @Override
    protected UserDetailsImpl getPrincipal(SAMLCredential credential, Object userDetail) {
        if (userDetail != null) {
            return (UserDetailsImpl) userDetail;
        }
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getEntitlements(SAMLCredential credential, Object userDetail) {
        if(userDetail instanceof ExpiringUsernameAuthenticationToken) {
            return new ArrayList<>(((ExpiringUsernameAuthenticationToken) userDetail).getAuthorities());
        } else if (userDetail instanceof UserDetailsImpl) {
            return new ArrayList<GrantedAuthority>(((UserDetailsImpl) userDetail).getAuthorities());
        } else {
            return Collections.emptyList();
        }
    }

}