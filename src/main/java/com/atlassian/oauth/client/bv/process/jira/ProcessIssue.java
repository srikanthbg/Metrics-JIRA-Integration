package com.atlassian.oauth.client.bv.process.jira;

import com.atlassian.oauth.client.bv.model.jira.Issue;
import com.atlassian.oauth.client.bv.utils.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;

import java.lang.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Srikanth BG on 10/8/14.
 */
public class ProcessIssue implements Process {

    static Logger log = Logger.getLogger(ProcessIssue.class.getName());

    @Override
    public <T> List<T> processModel(String jsonResponse) {


        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject)jsonParser.parse(jsonResponse);
        JsonArray issuesObject = (JsonArray)jsonObject.get("issues");
        Iterator iterIssues = issuesObject.iterator();

        List<T> issueList = new ArrayList<T>();

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

            issueList.add((T)issueObj);
        }



        return issueList;


    }
}
