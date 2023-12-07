package org.openmbee.mms.mmsri.config;

import org.openmbee.mms.authenticator.config.AuthSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableTransactionManagement
@EnableAsync
public class SecurityConfig extends WebSecurityConfigurerAdapter implements
    WebMvcConfigurer {

    @Value("${mms.hsts.enabled:false}")
    private boolean hsts;

    @Value("${cors.allowed.origins:*}")
    private String allowedOrigins;

    @Autowired
    AuthSecurityConfig authSecurityConfig;
// Below Code is used for Spring 2.x
    @Override
    public void configure(HttpSecurity http) throws Exception {
        //permit all for anonymous access for public projects
        http.csrf().disable().authorizeRequests()
                .antMatchers("/actuator/health/**").permitAll()
                .antMatchers("/actuator/**").hasAuthority("mmsadmin")
                .anyRequest().permitAll().and().httpBasic();
        http.headers().cacheControl();
        http.addFilterAfter(new LoggingFilter(), AnonymousAuthenticationFilter.class);
        if (hsts) {
            http.headers()
                    .httpStrictTransportSecurity()
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000);
        }

        //filter only needed if not permitAll
        //http.addFilterAfter(corsFilter(), ExceptionTranslationFilter.class);
        authSecurityConfig.setAuthConfig(http);
    }

    @Bean
    public RequestMappingHandlerMapping useTrailingSlash() {
        RequestMappingHandlerMapping requestMappingHandlerMapping = new RequestMappingHandlerMapping();
        requestMappingHandlerMapping.setUseTrailingSlashMatch(true);
        return requestMappingHandlerMapping;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(3600L)
                .allowedOriginPatterns(allowedOrigins.split(","));
//        registry.addMapping("/**")
//            .allowedOrigins("https://openmbee-mms.apps.arena-workspace.navair.navy.mil") // Point this to your frontend's domain
//            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//            .allowedHeaders("Authorization", "Content-Type", "Cache-Control")
//            .allowCredentials(true);
    }

    private CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        for (String origin: allowedOrigins.split(",")) {
            config.addAllowedOriginPattern(origin);
        }
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorParameter(false)
            .ignoreAcceptHeader(false)
            .defaultContentType(MediaType.APPLICATION_JSON);
    }
}
//package org.openmbee.mms.example.config;
//
//import org.openmbee.mms.authenticator.config.AuthSecurityConfig;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.MediaType;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.web.access.ExceptionTranslationFilter;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
//
//import static org.springframework.http.HttpHeaders.*;
//import static org.springframework.http.HttpMethod.*;
//
//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//@EnableTransactionManagement
//@EnableAsync
//public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {
//
//    @Value("${cors.allowed.origins:*}")
//    private String allowedOrigins;
//
//    @Autowired
//    AuthSecurityConfig authSecurityConfig;
//
//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable().authorizeRequests().anyRequest().permitAll().and().httpBasic();
//        http.headers().cacheControl();
//        http.addFilterAfter(corsFilter(), ExceptionTranslationFilter.class);
//        authSecurityConfig.setAuthConfig(http);
//    }
//
//    @Bean
//    public RequestMappingHandlerMapping useTrailingSlash() {
//        RequestMappingHandlerMapping requestMappingHandlerMapping = new RequestMappingHandlerMapping();
//        requestMappingHandlerMapping.setUseTrailingSlashMatch(true);
//        return requestMappingHandlerMapping;
//    }
//
//    @Bean
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
//    }
//
//    private CorsFilter corsFilter() {
//        /*
//         CORS requests are managed only if headers Origin and Access-Control-Request-Method are available on OPTIONS requests
//         (this filter is simply ignored in other cases).
//         This filter can be used as a replacement for the @Cors annotation.
//        */
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
////        for(String origin: allowedOrigins.split(",")) {
////            config.addAllowedOrigin(origin);
////        }
//        config.addAllowedOrigin("openmbee.apps.arena-workspace.navair.navy.mil");
//        config.addAllowedOrigin("openmbee-mms.apps.arena-workspace.navair.navy.mil");
//        config.addAllowedHeader(ORIGIN);
//        config.addAllowedHeader(CONTENT_TYPE);
//        config.addAllowedHeader(ACCEPT);
//        config.addAllowedHeader(AUTHORIZATION);
//        config.addAllowedMethod(GET);
//        config.addAllowedMethod(PUT);
//        config.addAllowedMethod(POST);
//        config.addAllowedMethod(OPTIONS);
//        config.addAllowedMethod(DELETE);
//        config.addAllowedMethod(PATCH);
//        config.setMaxAge(3600L);
//
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }
//
//    @Override
//    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
//        configurer.favorParameter(false)
//            .ignoreAcceptHeader(false)
//            .defaultContentType(MediaType.APPLICATION_JSON);
//    }
//}