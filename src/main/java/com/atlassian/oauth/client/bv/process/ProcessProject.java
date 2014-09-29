package com.atlassian.oauth.client.bv.process;

import com.atlassian.oauth.client.bv.dao.jira.ProjectDAO;
import com.atlassian.oauth.client.bv.model.jira.Project;
import com.atlassian.oauth.client.bv.readers.jira.JiraAttributesReader;
import com.atlassian.oauth.client.bv.service.jira.ProjectService;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Srikanth BG on 9/26/14.
 */
public class ProcessProject implements Process {

    static Logger log = Logger.getLogger(ProcessProject.class.getName());

    @Resource(name = "ProjectService")
    private ProjectService projectService;

    @Override
    public void processModel(JiraAttributesReader jiraAttributesReader, ClassPathXmlApplicationContext context) {

        List<Project> listProjects = jiraAttributesReader.getAllProjects();

        //List<Project> listProjects = new ArrayList<Project>();
        Gson gson = new Gson();
        log.info(gson.toJson(listProjects));

       // projectService.insertBatch(listProjects);

        ProjectDAO projectDao = (ProjectDAO)context.getBean("ProjectDAO");
        projectDao.insertBatch(listProjects);

    }
}
