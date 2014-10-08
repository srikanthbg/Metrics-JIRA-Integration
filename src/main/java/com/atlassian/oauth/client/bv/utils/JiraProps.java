package com.atlassian.oauth.client.bv.utils;

/**
 * Created by Srikanth BG on 10/7/14.
 */
public class JiraProps {

    private String accessToken;
    private String jiraServer;
    private String consumerKey;
    private String getProjects;
    private String getIssues;
    private String getIssuesFilter;
    private String issueMaxResults;
    private String batchSize;
    private String jiraBase;
    private String requestToken;
    private String tokenSecret;
    private String verifier;

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public String getVerifier() {
        return verifier;
    }

    public void setVerifier(String verifier) {
        this.verifier = verifier;
    }



    public String getJiraBase() {
        return jiraBase;
    }

    public void setJiraBase(String jiraBase) {
        this.jiraBase = jiraBase;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getJiraServer() {
        return jiraServer;
    }

    public void setJiraServer(String jiraServer) {
        this.jiraServer = jiraServer;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getGetProjects() {
        return getProjects;
    }

    public void setGetProjects(String getProjects) {
        this.getProjects = getProjects;
    }

    public String getGetIssues() {
        return getIssues;
    }

    public void setGetIssues(String getIssues) {
        this.getIssues = getIssues;
    }

    public String getGetIssuesFilter() {
        return getIssuesFilter;
    }

    public void setGetIssuesFilter(String getIssuesFilter) {
        this.getIssuesFilter = getIssuesFilter;
    }

    public String getIssueMaxResults() {
        return issueMaxResults;
    }

    public void setIssueMaxResults(String issueMaxResults) {
        this.issueMaxResults = issueMaxResults;
    }

    public String getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(String batchSize) {
        this.batchSize = batchSize;
    }


}
