package com.atlassian.oauth.client.bv.readers.jira;

import com.atlassian.oauth.client.bv.start.AtlassianOAuthClient;
import com.atlassian.oauth.client.bv.model.jira.Issue;
import com.atlassian.oauth.client.bv.model.jira.Project;
import com.atlassian.oauth.client.bv.utils.JiraJsonParser;
import com.atlassian.oauth.client.bv.utils.JiraProps;
import com.atlassian.oauth.client.bv.utils.Util;
import com.google.gson.*;
import org.apache.log4j.Logger;

import java.net.URLEncoder;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Created by Srikanth BG on 9/12/14.
 */
public class JiraAttributesReader extends JiraJsonParser {

    private AtlassianOAuthClient atlassianOAuthClient;
    private JiraProps props;

    static Logger log = Logger.getLogger(JiraAttributesReader.class.getName());

    public JiraAttributesReader(AtlassianOAuthClient atlassianOAuthClient, JiraProps props)
    {
        this.atlassianOAuthClient = atlassianOAuthClient;
        this.props = props;
    }

    public List<Project> getAllProjects()
    {

        String projectQuery = props.getJiraServer()+props.getGetProjects();
        log.info("project List Query --> " + projectQuery);

        String getAllProjectResponse = atlassianOAuthClient.makeAuthenticatedRequest(projectQuery,props.getAccessToken());



        JsonParser jsonParser = new JsonParser();
        List<Project> projectList = new ArrayList<Project>();

        JsonArray projectArray = (JsonArray)jsonParser.parse(getAllProjectResponse);
        Iterator projectIter = projectArray.iterator();

        JsonObject loopProjectObj = new JsonObject();
        try {

            while (projectIter.hasNext()) {
                loopProjectObj = (JsonObject) projectIter.next();
                Project projObj = new Project();

                /*
                    Get project id
                 */
                if (!(loopProjectObj.get("id") instanceof JsonNull))
                    projObj.setId(loopProjectObj.get("id").getAsInt());

                /*
                    get project key
                 */
                if (!(loopProjectObj.get("key") instanceof JsonNull))
                    projObj.setKey(loopProjectObj.get("key").getAsString());

                /*
                    get project name
                 */
                if (!(loopProjectObj.get("name") instanceof JsonNull))
                    projObj.setName(loopProjectObj.get("name").getAsString());

                projectList.add(projObj);

            }

            return projectList;
        }
        catch(Exception e)
        {
            log.error(e.getStackTrace());
            return null;
        }
    }

    public List<Issue> getProjectIssues(String projectName)
    {
        try
        {

            JsonParser jsonParser = new JsonParser();
            List<Issue> finalIssueList = new ArrayList<Issue>();

            /*
                    Get the count of all issues for the project
             */

            String encodedProjectName = "'" + URLEncoder.encode(projectName,"UTF-8") + "'";
            String issueCountQuery = props.getJiraServer()+props.getGetIssues()+ encodedProjectName +
                    props.getGetIssuesFilter()+"0";
            log.info("issue count query --> " + issueCountQuery);

            String issueCountResponse = atlassianOAuthClient.makeAuthenticatedRequest(issueCountQuery,props.getAccessToken());
            log.info("Issue count response --> " + issueCountResponse);

            JsonObject jsonObjectIssueCountObject = (JsonObject)jsonParser.parse(issueCountResponse);

            /*
                set batch attributes to query JIRA
             */
            String issuesCount = jsonObjectIssueCountObject.get("total").toString();
            int batchSize = Integer.parseInt(props.getBatchSize());
            int batchLoopCount = Util.getBatchCount(Integer.parseInt(issuesCount),batchSize);
            int startAt=0;

            /*
                Get all issues of the project
             */

            String issueQuery = props.getJiraServer()+
                props.getGetIssues()+ encodedProjectName  + props.getGetIssuesFilter();
            String issueQueryLoop = "";

            /*
                Get 50 issues per call from JIRA
             */

            for(int i=0 ; i<batchLoopCount ; i++)
            {
                List<Issue> issueList = new ArrayList<Issue>();
                issueQueryLoop = issueQuery + startAt;
                startAt = startAt + batchSize;
                log.info("Issue Query --> " + issueQueryLoop);

                String issuesResponse = atlassianOAuthClient.makeAuthenticatedRequest(issueQueryLoop,props.getAccessToken());
                log.info("Issue response --> " + issuesResponse);

                JsonObject jsonObject = (JsonObject)jsonParser.parse(issuesResponse);
                JsonArray issuesObject = (JsonArray)jsonObject.get("issues");
                Iterator iterIssues = issuesObject.iterator();

                String assignee;
                Date createDate;
                Date updateDate;
                String summary;
                String priority;
                int projectID;
                String status;
                String reporter;

                JsonObject loopIssueObj = new JsonObject();
                Issue issueObj;
                while(iterIssues.hasNext())
                {
                    assignee = "null";
                    createDate = null;
                    updateDate = null;
                    summary = "null";
                    priority = "null";
                    projectID = -1;
                    status = "null";
                    reporter = "null";

                    issueObj = new Issue();
                    loopIssueObj = (JsonObject)iterIssues.next();


                /*
                    get issue id
                 */

                    if(!(loopIssueObj.get("id") instanceof JsonNull))
                        issueObj.setId(loopIssueObj.get("id").getAsInt());

                /*
                    get issue key
                 */

                    if(!(loopIssueObj.get("key") instanceof JsonNull))
                        issueObj.setKey(loopIssueObj.get("key").getAsString());

                /*
                    get assignee
                 */


                    if( !(loopIssueObj.getAsJsonObject("fields").getAsJsonObject().get("assignee") instanceof JsonNull))
                    {
                        assignee = loopIssueObj.getAsJsonObject("fields").getAsJsonObject().get("assignee").getAsJsonObject().get("name").getAsString();
                    }
                    issueObj.setAssignee(assignee);

                /*
                    get create date
                 */

                    if(!(loopIssueObj.getAsJsonObject("fields").get("created") instanceof JsonNull)) {
                        createDate = Util.formatToSQLDate(loopIssueObj.getAsJsonObject("fields").get("created").getAsString());
                    }
                    issueObj.setCreateDate(createDate);


                /*
                    get update date
                 */

                    if(!(loopIssueObj.getAsJsonObject("fields").get("updated") instanceof JsonNull)) {
                        updateDate = Util.formatToSQLDate(loopIssueObj.getAsJsonObject("fields").get("updated") .getAsString());
                    }
                    issueObj.setUpdateDate(updateDate);

                 /*
                    get summary
                 */
                    if(!(loopIssueObj.getAsJsonObject("fields").get("summary") instanceof JsonNull)) {
                        summary = loopIssueObj.getAsJsonObject("fields").get("summary").getAsString();
                    }
                    issueObj.setDesc(summary);

                 /*
                    get priority
                 */
                    if(!(loopIssueObj.get("fields").getAsJsonObject().get("priority")  instanceof  JsonNull))
                    {
                        priority = loopIssueObj.get("fields").getAsJsonObject().get("priority").getAsJsonObject().get("name").getAsString();
                    }
                    issueObj.setPriority(priority);

                /*
                    get project id
                 */

                    if( !(loopIssueObj.get("fields").getAsJsonObject().get("project") instanceof JsonNull))
                    {
                        projectID = loopIssueObj.get("fields").getAsJsonObject().get("project").getAsJsonObject().get("id").getAsInt();
                    }
                    issueObj.setProjectID(projectID);


                /*
                    get status
                 */

                    if( !(loopIssueObj.get("fields").getAsJsonObject().get("status") instanceof JsonNull)) {
                        status = loopIssueObj.get("fields").getAsJsonObject().get("status").getAsJsonObject().get("name").getAsString();
                    }
                    issueObj.setStatus(status);

                 /*
                    get reporter
                 */

                    if( !(loopIssueObj.get("fields").getAsJsonObject().get("reporter") instanceof  JsonNull)) {
                        reporter = loopIssueObj.get("fields").getAsJsonObject().get("reporter").getAsJsonObject().get("name").getAsString();
                    }
                    issueObj.setReporter(reporter);

                    issueList.add(issueObj);
                }

                finalIssueList.addAll(issueList);
            }

            return finalIssueList;
        }catch(Exception e)
        {
            log.error(e.getMessage());
            return null;
        }

    }
}
