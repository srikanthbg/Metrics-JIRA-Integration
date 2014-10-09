package com.atlassian.oauth.client.bv.process;

import com.atlassian.oauth.client.bv.dao.jira.IssueDAO;
import com.atlassian.oauth.client.bv.model.jira.Issue;
import com.atlassian.oauth.client.bv.readers.jira.JiraAttributesReader;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Map;

/**
 * Created by Srikanth BG on 10/8/14.
 */
public class ProcessIssue implements Process  {

    static Logger log = Logger.getLogger(ProcessProject.class.getName());

    @Override
    public void processModel(JiraAttributesReader jiraAttributesReader, ClassPathXmlApplicationContext context, Map params) {

        List<Issue> listIssues = jiraAttributesReader.getAllIssues();
        Gson gson = new Gson();
        log.info(gson.toJson(listIssues));
        IssueDAO issueDAO = (IssueDAO)context.getBean("issueDAO");
        issueDAO.insertBatch(listIssues);

    }
}
