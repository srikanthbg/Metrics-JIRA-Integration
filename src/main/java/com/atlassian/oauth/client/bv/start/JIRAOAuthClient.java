package com.atlassian.oauth.client.bv.start;

import com.atlassian.oauth.client.bv.process.jira.Process;
import com.atlassian.oauth.client.bv.process.jira.ProcessIssue;
import com.atlassian.oauth.client.bv.process.jira.ProcessProjectIssue;
import com.atlassian.oauth.client.bv.model.jira.JiraAttributesReader;
import com.atlassian.oauth.client.bv.service.jira.ComponentService;
import com.atlassian.oauth.client.bv.service.jira.IncrementalIssueService;
import com.atlassian.oauth.client.bv.service.jira.IssueService;
import com.atlassian.oauth.client.bv.service.jira.ProjectService;
import com.atlassian.oauth.client.bv.model.jira.JiraProps;
import com.atlassian.oauth.client.bv.utils.Util;
import com.google.common.collect.Lists;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class JIRAOAuthClient
{



    static Logger log = Logger.getLogger(JIRAOAuthClient.class.getName());
    private static final String CALLBACK_URI = "";
    protected static  String CONSUMER_PRIVATE_KEY = Base64.encode(Util.getPrivateKey("./resources/private.der").getEncoded());

    public enum Command
    {
        REQUEST_TOKEN("requestToken"),
        ACCESS_TOKEN("accessToken"), REQUEST("request"),GET_ALL_PROJECTS("getAllProjects"),
        GET_ALL_ISSUES("getAllIssues"), GET_ALL_ISSUES_INCREMENTAL("getAllIssuesIncremental"),
        GET_ISSUES_OF_PROJECT("getIssuesOfProject"),GET_ISSUES_OF_PROJECT_INCREMENTAL("getIssuesOfProjectIncremental");

        private String name;

        Command(final String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }
    }

    public static void main(String[] args) {

        /*
            Load the jira properties
         */

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JiraProps jiraProps = (JiraProps) context.getBean("jiraProps");

        final String CONSUMER_KEY = jiraProps.getConsumerKey();

        ArrayList<String> arguments = Lists.newArrayList(args);

        try {
            if (arguments.isEmpty()) {
                throw new IllegalArgumentException("No command specified. Use one of " + getCommandNames());
            }
            String action = arguments.get(0);

            if (Command.REQUEST_TOKEN.getName().equals(action))

            {

                String baseUrl = jiraProps.getJiraBase();
                String callBack = "";

                AtlassianOAuthClient jiraoAuthClient = new AtlassianOAuthClient(CONSUMER_KEY, CONSUMER_PRIVATE_KEY, baseUrl, callBack);
                TokenSecretVerifierHolder requestToken = jiraoAuthClient.getRequestToken();
                String authorizeUrl = jiraoAuthClient.getAuthorizeUrlForToken(requestToken.token);
                log.info("Token is " + requestToken.token);
                log.info("Token secret is " + requestToken.secret);
                log.info("Retrieved request token. go to " + authorizeUrl);
            }

            else if (Command.ACCESS_TOKEN.getName().equals(action))

            {
                String baseUrl = jiraProps.getJiraBase();
                AtlassianOAuthClient jiraoAuthClient = new AtlassianOAuthClient(CONSUMER_KEY, CONSUMER_PRIVATE_KEY, baseUrl, CALLBACK_URI);
                String requestToken = jiraProps.getRequestToken();
                String tokenSecret = jiraProps.getTokenSecret();
                String verifier = jiraProps.getVerifier();
                String accessToken = jiraoAuthClient.swapRequestTokenForAccessToken(requestToken, tokenSecret, verifier);
                log.info("Access token is : " + accessToken);
            }

            else if (Command.REQUEST.getName().equals(action)) {

                /*
                     Sample request
                 */

                AtlassianOAuthClient jiraoAuthClient = new AtlassianOAuthClient(CONSUMER_KEY, CONSUMER_PRIVATE_KEY, null, CALLBACK_URI);
                String accessToken = arguments.get(1);
                String url = arguments.get(2);
                String responseAsString = jiraoAuthClient.makeAuthenticatedRequest(url, accessToken);
                log.info("RESPONSE IS" + responseAsString);
            }


            else {

                /*
                    JIRA operations start here
                 */

                AtlassianOAuthClient jiraoAuthClient = new AtlassianOAuthClient(CONSUMER_KEY, CONSUMER_PRIVATE_KEY, null, CALLBACK_URI);
                JiraAttributesReader jiraAttributesReader = new JiraAttributesReader(jiraoAuthClient, jiraProps);
                Map<Object, Object> params = new HashMap<Object, Object>();


                if (Command.GET_ALL_ISSUES.getName().equals(action))
                {
                    ComponentService issueService = new IssueService();
                    params.clear();
                    issueService.insertBatch(jiraAttributesReader,context,params);

                }

                if(Command.GET_ALL_ISSUES_INCREMENTAL.getName().equals(action))
                {

                    ComponentService issueService = new IncrementalIssueService();
                    issueService.insertBatch(jiraAttributesReader,context,params);
                }

                else if (Command.GET_ALL_PROJECTS.getName().equals(action))
                {

                    ComponentService projectService = new ProjectService();
                    params.clear();
                    projectService.insertBatch(jiraAttributesReader,context,params);
                }

                else if (Command.GET_ISSUES_OF_PROJECT.getName().equals(action))
                {

                    Process process = new ProcessProjectIssue();
                    String projectName = arguments.get(1);

                    params.clear();
                    params.put("projectName", projectName);

                    //process.processModel(jiraAttributesReader);
                }

                else if (Command.GET_ISSUES_OF_PROJECT_INCREMENTAL.getName().equals(action))
                {

                    Process process = new ProcessProjectIssue();
                    String projectName = arguments.get(1);

                    params.clear();
                    params.put("projectName", projectName);

                    //process.processModel(jiraAttributesReader);
                }

                else
                {
                    log.error("Command " + action + " not supported. Only " + getCommandNames() + " are supported.");
                }
            }
        }

        catch(Exception e)
        {
            log.error("FATAL exception processing JIRA data");
            log.error(e.getMessage());
            log.error(e);
            e.printStackTrace();
        }
    }

    private static String getCommandNames()
    {
        String names = "";
        for (Command value : Command.values())
        {
            names += value.getName() + " ";
        }
        return names;
    }


}
