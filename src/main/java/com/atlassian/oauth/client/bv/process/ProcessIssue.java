package com.atlassian.oauth.client.bv.process;

import com.atlassian.oauth.client.bv.dao.jira.IssueDAO;
import com.atlassian.oauth.client.bv.model.jira.Issue;
import com.atlassian.oauth.client.bv.model.jira.Project;
import com.atlassian.oauth.client.bv.readers.jira.JiraAttributesReader;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Srikanth BG on 10/8/14.
 */
public class ProcessIssue implements Process  {

    static Logger log = Logger.getLogger(ProcessProject.class.getName());

    @Override
    public void processModel(JiraAttributesReader jiraAttributesReader, ClassPathXmlApplicationContext context, Map params) {

        List<Project> listProject = jiraAttributesReader.getAllProjects();

        List<String> failedProjects = new ArrayList<String>();

            for (Project projectObj : listProject) {

              try {
                  List<Issue> issueList = jiraAttributesReader.getProjectIssues(projectObj.getName());
                  IssueDAO issueDAO = (IssueDAO) context.getBean("IssueDAO");
                  issueDAO.insertBatch(issueList);
                  log.info("issues of project--> " + projectObj.getName() + " is getting loaded");
              }
              catch(Exception e)
              {
                  log.error("Project issues error --> " + projectObj.getName() + " please re-run");
                  log.error(e.getCause());
                  failedProjects.add(projectObj.getName());
                  continue;
              }

                log.info("failed projects to be re-run -->" + failedProjects.toString());
            }

    }
}
