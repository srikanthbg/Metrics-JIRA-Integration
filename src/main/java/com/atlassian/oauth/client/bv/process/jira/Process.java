package com.atlassian.oauth.client.bv.process.jira;

import com.atlassian.oauth.client.bv.model.jira.JiraAttributesReader;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Map;

/**
 * Created by Srikanth BG on 9/26/14.
 */
public interface Process {

    public <T> List<T> processModel(String jsonResponse);
}
