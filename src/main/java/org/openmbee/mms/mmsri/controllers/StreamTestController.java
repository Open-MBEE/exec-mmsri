package org.openmbee.mms.mmsri.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.openmbee.mms.crud.controllers.BaseController;
import org.openmbee.mms.mmsri.services.TestNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.Map;

@RestController
@RequestMapping("/projects/{projectId}/refs/{refId}/stream")
@Tag(name = "StreamTest")
public class StreamTestController extends BaseController  {

    private TestNodeService testNodeService;
    @Autowired
    public void setTestNodeService(TestNodeService testNodeService) {
        this.testNodeService = testNodeService;
    }

    @GetMapping(produces = "application/x-ndjson")
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public ResponseEntity<ResponseBodyEmitter> getAllElements(
            @PathVariable String projectId,
            @PathVariable String refId,
            @RequestParam(required = false) String commitId,
            @RequestParam(required = false) Map<String, String> params) {

        return testNodeService.stream(projectId, refId, "", params);
    }
}
