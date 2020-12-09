package org.openmbee.mms.mmsri.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.openmbee.mms.core.exceptions.BadRequestException;
import org.openmbee.mms.core.objects.ElementsRequest;
import org.openmbee.mms.core.objects.ElementsResponse;
import org.openmbee.mms.core.services.NodeService;
import org.openmbee.mms.crud.controllers.BaseController;
import org.openmbee.mms.mmsri.services.TestNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    public ResponseEntity<StreamingResponseBody> getAllElements(
            @PathVariable String projectId,
            @PathVariable String refId,
            @RequestParam(required = false) String commitId,
            @RequestParam(required = false) Map<String, String> params) {

        return testNodeService.stream(projectId, refId, "", params);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_EDIT_CONTENT', false)")
    public ResponseEntity<StreamingResponseBody> createOrUpdateElements(
            HttpServletRequest request,
            @PathVariable String projectId,
            @PathVariable String refId,
            @RequestBody ElementsRequest req,
            @RequestParam(required = false) String overwrite,
            @RequestParam(required = false) Map<String, String> params,
            Authentication auth) {

        ElementsResponse response = new ElementsResponse();
        if (!req.getElements().isEmpty()) {
            return testNodeService.createOrUpdateFromStream(projectId, refId, req, params, auth.getName());
        }
        throw new BadRequestException(response.addMessage("Empty"));
    }
}
