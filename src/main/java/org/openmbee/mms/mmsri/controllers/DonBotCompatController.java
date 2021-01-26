package org.openmbee.mms.mmsri.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.openmbee.mms.core.config.ContextHolder;
import org.openmbee.mms.core.config.Privileges;
import org.openmbee.mms.core.config.ProjectSchemas;
import org.openmbee.mms.core.dao.ProjectDAO;
import org.openmbee.mms.core.dao.ProjectIndex;
import org.openmbee.mms.core.objects.ProjectsResponse;
import org.openmbee.mms.crud.controllers.BaseController;
import org.openmbee.mms.data.domains.global.Project;
import org.openmbee.mms.json.ProjectJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
@Tag(name = "DonBotCompatibility")
public class DonBotCompatController extends BaseController {

    ProjectDAO projectRepository;
    ProjectIndex projectIndex;
    ProjectSchemas schemas;


    @Autowired
    public DonBotCompatController(ProjectDAO projectRepository, ProjectIndex projectIndex, ProjectSchemas schemas) {
        this.projectRepository = projectRepository;
        this.projectIndex = projectIndex;
        this.schemas = schemas;
    }

    @GetMapping("/orgs/{orgId}/projects")
    @PreAuthorize("@mss.hasOrgPrivilege(authentication, #orgId, 'ORG_READ', true)")
    public ProjectsResponse getOrgProjects(@PathVariable String orgId, Authentication auth) {
        ProjectsResponse response = new ProjectsResponse();

        List<Project> allProjects = projectRepository.findAll();
        allProjects.removeIf(proj -> !mss.hasProjectPrivilege(auth, proj.getProjectId(), Privileges.PROJECT_READ.name(), true));

        response.getProjects().addAll(allProjects.stream().map(proj -> {
            ContextHolder.setContext(proj.getProjectId());
            if (proj.getDocId() != null && !proj.isDeleted()) {
                Optional<ProjectJson> projectJsonOption = projectIndex.findById(proj.getDocId());
                if (projectJsonOption.isPresent() && projectJsonOption.get().getOrgId().equals(orgId)) {
                    return projectJsonOption.get();
                } else {
                    logger.error("Project json not found for id: {}", proj.getProjectId());
                }
            }
            return null;
        }).collect(Collectors.toList()));

        return response;
    }
}
