package org.openmbee.mms.mmsri;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.openmbee")
@OpenAPIDefinition(
    info = @Info(
        title = "MMS Reference Implementation API",
        version = "4.0.20",
        description = "Documentation for MMS API",
        license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.txt")
    ),
    security = {@SecurityRequirement(name = "basicAuth"), @SecurityRequirement(name = "bearerToken")}
)
@SecurityScheme(
    name = "bearerToken",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class MMSRIApplication {

    public static void main(String[] args) {
        SpringApplication.run(MMSRIApplication.class, args);
    }

}
