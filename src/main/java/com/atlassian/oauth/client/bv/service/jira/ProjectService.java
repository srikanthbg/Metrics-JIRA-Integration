package com.atlassian.oauth.client.bv.service.jira;

import com.atlassian.oauth.client.bv.dao.jira.impl.ProjectDAO;
import com.atlassian.oauth.client.bv.model.jira.JiraAttributesReader;
import com.atlassian.oauth.client.bv.model.jira.Project;
import com.atlassian.oauth.client.bv.process.jira.*;
import com.atlassian.oauth.client.bv.process.jira.Process;
import com.atlassian.oauth.client.bv.start.AtlassianOAuthClient;
import com.atlassian.oauth.client.bv.model.jira.JiraProps;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by Srikanth BG on 9/24/14.
 */

@Service("ProjectService")
public class ProjectService implements ComponentService{

    @Resource(name = "ProjectDAO")
    private ProjectDAO projectDao;

    static Logger log = Logger.getLogger(ProjectService.class.getName());


    public void insertBatch(JiraAttributesReader jiraAttributesReader, ClassPathXmlApplicationContext context, Map params)

    {
        List<Project> projectList = getComponentList(jiraAttributesReader,context,params);
        ProjectDAO projectDAO = (ProjectDAO)context.getBean("ProjectDAO");
        projectDAO.insertBatch(projectList);
    }

    @Override
    public <T> List<T> getComponentList(JiraAttributesReader jiraAttributesReader, ClassPathXmlApplicationContext context, Map params) {
        {

            Process process = new ProcessProject();

            JiraProps props = jiraAttributesReader.getProps();
            AtlassianOAuthClient atlassianOAuthClient = jiraAttributesReader.getAtlassianOAuthClient();


            String projectQuery = props.getJiraServer() + props.getGetProjects();
            log.info("project List Query --> " + projectQuery);

            String getAllProjectResponse = atlassianOAuthClient.makeAuthenticatedRequest(projectQuery, props.getAccessToken());
            List<T> list = process.processModel(getAllProjectResponse);
            log.info(list);
            return list;
        }
    }
}
