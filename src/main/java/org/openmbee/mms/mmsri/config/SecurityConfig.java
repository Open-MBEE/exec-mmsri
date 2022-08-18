package org.openmbee.mms.mmsri.config;

import org.openmbee.mms.authenticator.config.AuthSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
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
@Order(100)
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Value("${mms.hsts.enabled:false}")
    private boolean hsts;

    @Value("${cors.allowed.origins:*}")
    private String allowedOrigins;

    @Autowired
    AuthSecurityConfig authSecurityConfig;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/saml");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.headers().cacheControl();

        if (hsts) {
            http.headers().httpStrictTransportSecurity().includeSubDomains(true).maxAgeInSeconds(31536000);
        }

        http.authorizeRequests().antMatchers("/").permitAll().anyRequest().authenticated();

        //filter only needed if not permitAll
        //http.addFilterAfter(corsFilter(), ExceptionTranslationFilter.class);

        //authSecurityConfig.setAuthConfig(http);
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
