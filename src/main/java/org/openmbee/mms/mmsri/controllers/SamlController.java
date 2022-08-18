package org.openmbee.mms.mmsri.controllers;

import org.openmbee.mms.data.domains.global.User;
import org.openmbee.mms.localuser.security.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SamlController {

    private static final Logger logger = LoggerFactory.getLogger(SamlController.class);

    @GetMapping(value = "/saml/auth")
    public String handleSamlAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return "redirect:/saml-test";
        } else {
            return "/";
        }
    }

    @RequestMapping("/saml-test")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            logger.error("Name: " + userDetails.getUsername());
            logger.error("Principal: " + ((UserDetailsImpl) authentication.getPrincipal()).getUsername());
            logger.error("Authorities: " + userDetails.getAuthorities().toString());
            logger.error("Details: " + userDetails.isEnabled());
        }
        assert authentication != null;
        model.addAttribute("username", authentication.getPrincipal());
        return "saml-test";
    }

}
