package org.openmbee.mms.mmsri.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mmsversion")
@Tag(name = "MMSVersion")
public class MMSVersionController {

    private BuildProperties buildProperties;

    @Autowired
    public MMSVersionController(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @GetMapping
    @SecurityRequirements(value = {})
    public ResponseEntity<Map<String, Object>> getMMSVersion() {
        Map<String, Object> versionResponse = new HashMap<>();
        versionResponse.put("mmsVersion", this.buildProperties.getVersion());
        return ResponseEntity.ok().body(versionResponse);
    }
}
