package com.atlassian.oauth.client.bv.service.jira;

import com.atlassian.oauth.client.bv.model.jira.JiraAttributesReader;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Map;

/**
 * Created by Srikanth BG on 10/14/14.
 */
public interface ComponentService {

    public <T> List<T> getComponentList(JiraAttributesReader jiraAttributesReader, ClassPathXmlApplicationContext context, Map params);

    public void insertBatch(JiraAttributesReader jiraAttributesReader, ClassPathXmlApplicationContext context, Map params);
}
