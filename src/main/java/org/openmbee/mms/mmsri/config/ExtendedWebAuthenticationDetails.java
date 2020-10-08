package org.openmbee.mms.mmsri.config;

import javax.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class ExtendedWebAuthenticationDetails extends WebAuthenticationDetails {

    private final String requestUrl;
    private final String method;

    public ExtendedWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        //include request url in authentication event
        this.requestUrl = request.getRequestURI();
        this.method = request.getMethod();
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getMethod() {
        return method;
    }
}