package com.atlassian.oauth.client.bv.process;

import com.atlassian.oauth.client.bv.readers.jira.JiraAttributesReader;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Srikanth BG on 9/26/14.
 */
public interface Process {

    public void processModel(JiraAttributesReader jiraAttributesReader, ClassPathXmlApplicationContext context);
}
