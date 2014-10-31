package com.atlassian.oauth.client.bv.service.jira;

import com.atlassian.oauth.client.bv.dao.jira.impl.IssueDAO;
import com.atlassian.oauth.client.bv.model.jira.Issue;
import com.atlassian.oauth.client.bv.model.jira.JiraAttributesReader;
import com.atlassian.oauth.client.bv.model.jira.JiraProps;
import com.atlassian.oauth.client.bv.model.jira.Project;
import com.atlassian.oauth.client.bv.process.jira.Process;
import com.atlassian.oauth.client.bv.process.jira.ProcessIssue;
import com.atlassian.oauth.client.bv.start.AtlassianOAuthClient;
import com.atlassian.oauth.client.bv.utils.Util;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.oauth.OAuth;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Srikanth BG on 10/14/14.
 */
public class IncrementalIssueService implements ComponentService {

    @Resource(name = "IssueDAO")
    private IssueDAO issueDao;

    static Logger log = Logger.getLogger(IncrementalIssueService.class.getName());

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



        Date lastRun = issueDAO.getLastRunDate();
        Date today = Util.getTodaysDate();;
        int daysSinceLastRun = Util.getSqlDateDiff(lastRun, today);
        String daysFilter = "&&updated>-"+daysSinceLastRun+"d";
        InputStream inputStream;
        String issueCountResponse="";
        JsonObject jsonObjectIssueCountObject;

        try {
            for (Project projectObj : projectList)
            {
                try {
                        String projectName = projectObj.getName();
                            /*
                                Get the issue count using POST
                                Add days filter for delta days
                             */

                        StringBuffer strBuffer = new StringBuffer("{\"jql\":\"project = '").append(projectName).append("'")
                                                .append(daysFilter).append("\"")
                                                .append(",\"startAt\":0,\"maxResults\":15,\"fields\":[\"id\",\"key\",\"assignee\",\"issuetype\",\"reporter\",\"created\",\"updated\",\"priority\",\"project\",\"status\",\"summary\"]}");
                        log.info("issue count - Incremental Issue Service " +  strBuffer.toString());

                        inputStream = new ByteArrayInputStream(strBuffer.toString().getBytes());

                        issueCountResponse = atlassianOAuthClient.makeAuthenticatedRequestPost(props.getJiraServer()+"search",props.getAccessToken(),inputStream);
                        jsonObjectIssueCountObject = (JsonObject) jsonParser.parse(issueCountResponse);


                /*
                set batch attributes to query JIRA
                */
                        issuesCount = jsonObjectIssueCountObject.get("total").toString();
                        log.info("Project Name -->" + projectName + " Total Issues to be loaded --> " + issuesCount);
                        int batchSize = Integer.parseInt(props.getBatchSize());
                        int batchLoopCount = Util.getBatchCount(Integer.parseInt(issuesCount), batchSize);
                        int startAt = 0;

                        strBuffer = new StringBuffer("");

                        for (int i = 0; i < batchLoopCount; i++)

                        {
                            StringBuffer issueQueryLoop =  new StringBuffer("{\"jql\":\"project = '").append(projectName).append("'")
                                    .append(daysFilter).append("\"")
                                    .append(",\"fields\":[\"id\",\"key\",\"assignee\",\"issuetype\",\"reporter\",\"created\",\"updated\",\"priority\",\"project\",\"status\",\"summary\"],")
                                    .append("\"startAt\":").append(startAt).append("}");


                            log.info("Issue query in loop - Incremental Issue Service - " + issueQueryLoop);

                            inputStream = new ByteArrayInputStream(issueQueryLoop.toString().getBytes());
                            startAt = startAt + batchSize;

                            String issuesResponse = atlassianOAuthClient.makeAuthenticatedRequestPost(props.getJiraServer()+"search", props.getAccessToken(), inputStream);

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
