package com.atlassian.oauth.client.bv.process.jira;

import org.apache.log4j.Logger;

import java.lang.*;
import java.util.List;

/**
 * Created by Srikanth BG on 10/8/14.
 */
public class ProcessProjectIssue implements Process {

    static Logger log = Logger.getLogger(ProcessProject.class.getName());

    @Override
    public List<Object> processModel(String jsonResponse) {

        /*List<Issue> listIssues = jiraAttributesReader.getProjectIssues((String)params.get("projectName"));
        Gson gson = new Gson();
        log.info(gson.toJson(listIssues));
        IssueDAO issueDAO = (IssueDAO)context.getBean("IssueDAO");
        issueDAO.insertBatch(listIssues);*/
        return null;

    }
}
