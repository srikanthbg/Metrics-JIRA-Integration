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
import java.sql.Date;
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
        /*
            Issues are always processed in batches. Hence no implementation in this method.
         */
        return new ArrayList<T>();
    }

    @Override
    public void insertBatch(JiraAttributesReader jiraAttributesReader, ClassPathXmlApplicationContext context, Map params)

    {

        ComponentService projectService = new ProjectService();
        List<Project> projectList = projectService.getComponentList(jiraAttributesReader, context, params);
        JiraProps props = jiraAttributesReader.getProps();
        AtlassianOAuthClient atlassianOAuthClient = jiraAttributesReader.getAtlassianOAuthClient();

        List<String> failedProjects = new ArrayList<String>();
        List<String> successProjects = new ArrayList<String>();

        JsonParser jsonParser = new JsonParser();
        Process process = new ProcessIssue();
        IssueDAO issueDAO = (IssueDAO)context.getBean("IssueDAO");
        String issuesCount = "";
        boolean incremental = (Boolean)params.get("incremental");

        Date lastRun = null;
        Date today = null;
        int daysSinceLastRun = 0;
        String daysFilter = "";



        if(incremental)
        {
            lastRun =  issueDAO.getLastRunDate();
            today = Util.getTodaysDate();
            daysSinceLastRun = Util.getSqlDateDiff(lastRun, today);
            /*
               ex: created or updated > -5d
             */
            daysFilter = "&created>'-"+daysSinceLastRun+"d'&updated>'-"+daysSinceLastRun+"d'";
        }

        try {
            for (Project projectObj : projectList)
            {
                try {
                        String projectName = projectObj.getName();
                        String encodedProjectName = "'" + URLEncoder.encode(projectName, "UTF-8") + "'";
                        String issueCountQuery = props.getJiraServer() + props.getGetIssues() + encodedProjectName +
                            props.getGetIssuesFilter() + "0";

                        if(incremental)
                        {
                            /*
                                Add days filter for delta days
                             */
                               issueCountQuery =issueCountQuery + daysFilter;

                        }


                        log.info("issue count query --> " + issueCountQuery);

                        String issueCountResponse = atlassianOAuthClient.makeAuthenticatedRequest(issueCountQuery, props.getAccessToken());
                        log.info("Issue count response --> " + issueCountResponse);

                        JsonObject jsonObjectIssueCountObject = (JsonObject) jsonParser.parse(issueCountResponse);

                /*
                set batch attributes to query JIRA
                */
                        issuesCount = jsonObjectIssueCountObject.get("total").toString();
                        log.info("Project Name -->" + projectName + " Total Issues to be loaded --> " + issuesCount);
                        int batchSize = Integer.parseInt(props.getBatchSize());
                        int batchLoopCount = Util.getBatchCount(Integer.parseInt(issuesCount), batchSize);
                        int startAt = 0;

                        String issueQuery = props.getJiraServer() +
                                props.getGetIssues() + encodedProjectName + props.getGetIssuesFilter();
                        if(incremental)
                        {
                            issueQuery = issueQuery + daysFilter;
                        }
                        String issueQueryLoop = "";

                        for (int i = 0; i < batchLoopCount; i++)

                        {

                            issueQueryLoop = issueQuery + startAt;
                            startAt = startAt + batchSize;
                            log.info("Issue Query --> " + issueQueryLoop);

                            String issuesResponse = atlassianOAuthClient.makeAuthenticatedRequest(issueQueryLoop, props.getAccessToken());
                            log.info("Issue response --> " + issuesResponse);

                            List<Issue>  issueList = process.processModel(issuesResponse);

                            issueDAO.insertBatch(issueList);

                        }
                        successProjects.add(projectName);
                        log.info("successfully loaded projects -->" + successProjects.toString() + "Total issues loaded -->" + issuesCount);
                } catch (Exception e) {
                    log.error("Error processing project ISSUES -->" + projectObj.getName() + " Please re-run", e);
                    log.info("continuing to the next project in the queue");
                    failedProjects.add(projectObj.getName());
                    continue;
                }
            }
            log.info("FAILED projects -->" + failedProjects.toString());
            log.info("SUCCESSFULLY LOADED PROJECTS -->" + successProjects.toString());
        }
            catch(Exception e)
            {

                log.info("FATAL : Error in processing the project issues : ABORTING", e);

            }

    }

}
