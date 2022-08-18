package org.openmbee.mms.mmsri.security;

import org.openmbee.mms.data.domains.global.User;
import org.openmbee.mms.rdb.repositories.UserRepository;
import org.openmbee.mms.localuser.security.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MMSSamlUserDetailsService implements SAMLUserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(MMSSamlUserDetailsService.class);
    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetailsImpl loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(credential.getNameID().getValue());
        User u = user.orElseGet(() -> addUser(credential.getNameID().getValue()));
        return new UserDetailsImpl(u);
    }

    @Transactional
    public User addUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setEnabled(true);
        userRepository.save(user);
        return user;
    }
}