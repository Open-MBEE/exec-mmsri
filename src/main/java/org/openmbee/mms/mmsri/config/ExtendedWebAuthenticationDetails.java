package org.openmbee.mms.mmsri.config;

import javax.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class ExtendedWebAuthenticationDetails extends WebAuthenticationDetails {

    private String requestUrl;
    private String method;
    private String query;

    public ExtendedWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        //include request url in authentication event
        this.requestUrl = request.getRequestURI();
        this.method = request.getMethod();
        this.query = request.getQueryString();
        this.query = this.query == null ? "" : ("?" + this.query);
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getMethod() {
        return method;
    }

    public String getQuery() {
        return query;
    }
}
