package org.openmbee.mms.mmsri.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import org.springframework.stereotype.Component;

@Component
public class AuthAuditConfig{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @EventListener
    public void auditEventHappened(AuditApplicationEvent auditApplicationEvent) {
        AuditEvent auditEvent = auditApplicationEvent.getAuditEvent();
        if (auditEvent.getData() == null || auditEvent.getData().isEmpty() ||
                !(auditEvent.getData().get("details") instanceof ExtendedWebAuthenticationDetails)) {
            logger.info(auditEvent.getPrincipal());
            return;
        }
        ExtendedWebAuthenticationDetails details = (ExtendedWebAuthenticationDetails)auditEvent.getData().get("details");
        logger.info(auditEvent.getPrincipal() + " - " + details.getMethod() + " - " + details.getRequestUrl() + details.getQuery());
    }

    @Bean
    public AuditEventRepository getAuditEventRepository() {
        return new InMemoryAuditEventRepository();
    }
}

