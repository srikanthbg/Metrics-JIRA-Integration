package com.atlassian.oauth.client.bv.service.jira;

import com.atlassian.oauth.client.bv.dao.jira.impl.IssueDAO;
import com.atlassian.oauth.client.bv.model.jira.Issue;
import com.atlassian.oauth.client.bv.model.jira.JiraAttributesReader;
import com.atlassian.oauth.client.bv.model.jira.JiraProps;
import com.atlassian.oauth.client.bv.model.jira.Project;
import com.atlassian.oauth.client.bv.process.jira.*;
import com.atlassian.oauth.client.bv.process.jira.Process;
import com.atlassian.oauth.client.bv.start.AtlassianOAuthClient;
import com.atlassian.oauth.client.bv.utils.Util;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Srikanth BG on 10/14/14.
 */
public class IssueService implements ComponentService {

    @Resource(name = "IssueDAO")
    private IssueDAO issueDao;

    static Logger log = Logger.getLogger(IssueService.class.getName());

    @Override
    public <T> List<T> getComponentList(JiraAttributesReader jiraAttributesReader, ClassPathXmlApplicationContext context, Map params)

    {


        Process process = new ProcessIssue();
        String issueResponse = (String)params.get("issueResponse");
        List<T> list = process.processModel(issueResponse);
        log.info(list);
        return list;


    }

    @Override
    public void insertBatch(JiraAttributesReader jiraAttributesReader, ClassPathXmlApplicationContext context, Map params)

    {

        ComponentService projectService = new ProjectService();
        List<Project> projectList = projectService.getComponentList(jiraAttributesReader, context, params);
        JiraProps props = jiraAttributesReader.getProps();
        AtlassianOAuthClient atlassianOAuthClient = jiraAttributesReader.getAtlassianOAuthClient();

        List<String> failedProjects = new ArrayList<String>();

        JsonParser jsonParser = new JsonParser();


        try {
            for (Project projectObj : projectList)
            {
                try {

                    {
                        String projectName = projectObj.getName();

                        String encodedProjectName = "'" + URLEncoder.encode(projectName, "UTF-8") + "'";
                        String issueCountQuery = props.getJiraServer() + props.getGetIssues() + encodedProjectName +
                                props.getGetIssuesFilter() + "0";
                        log.info("issue count query --> " + issueCountQuery);

                        String issueCountResponse = atlassianOAuthClient.makeAuthenticatedRequest(issueCountQuery, props.getAccessToken());
                        log.info("Issue count response --> " + issueCountResponse);

                        JsonObject jsonObjectIssueCountObject = (JsonObject) jsonParser.parse(issueCountResponse);

                /*
                set batch attributes to query JIRA
                */
                        String issuesCount = jsonObjectIssueCountObject.get("total").toString();
                        int batchSize = Integer.parseInt(props.getBatchSize());
                        int batchLoopCount = Util.getBatchCount(Integer.parseInt(issuesCount), batchSize);
                        int startAt = 0;

                        String issueQuery = props.getJiraServer() +
                                props.getGetIssues() + encodedProjectName + props.getGetIssuesFilter();
                        String issueQueryLoop = "";

                        for (int i = 0; i < batchLoopCount; i++)

                        {

                            issueQueryLoop = issueQuery + startAt;
                            startAt = startAt + batchSize;
                            log.info("Issue Query --> " + issueQueryLoop);

                            String issuesResponse = atlassianOAuthClient.makeAuthenticatedRequest(issueQueryLoop, props.getAccessToken());
                            log.info("Issue response --> " + issuesResponse);

                            params.put("issueResponse", issuesResponse);
                            List<Issue> issueList = getComponentList(jiraAttributesReader, context, params);


                            issueDao.insertBatch(issueList);

                        }
                    }
                } catch (Exception e) {
                    log.error("Error processing project ISSUES -->" + projectObj.getName() + " Please re-run");
                    failedProjects.add(projectObj.getName());
                    continue;
                }
            }

            log.info("FAILED projects -->" + failedProjects.toString());
        }
            catch(Exception e)
            {
                log.info("FATAL : Error in processing the project issues : ABORTING");

            }

    }

}
