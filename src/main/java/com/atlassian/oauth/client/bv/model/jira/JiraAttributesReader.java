package com.atlassian.oauth.client.bv.model.jira;

import com.atlassian.oauth.client.bv.start.AtlassianOAuthClient;
import com.atlassian.oauth.client.bv.utils.JiraJsonParser;
import org.apache.log4j.Logger;

/**
 * Created by Srikanth BG on 9/12/14.
 */
public class JiraAttributesReader extends JiraJsonParser {


    static Logger log = Logger.getLogger(JiraAttributesReader.class.getName());

    public AtlassianOAuthClient atlassianOAuthClient;
    public JiraProps props;

    public JiraAttributesReader(AtlassianOAuthClient atlassianOAuthClient, JiraProps props) {
        this.atlassianOAuthClient = atlassianOAuthClient;
        this.props = props;
    }

    public AtlassianOAuthClient getAtlassianOAuthClient() {
        return atlassianOAuthClient;
    }

    public void setAtlassianOAuthClient(AtlassianOAuthClient atlassianOAuthClient) {
        this.atlassianOAuthClient = atlassianOAuthClient;
    }

    public JiraProps getProps() {
        return props;
    }

    public void setProps(JiraProps props) {
        this.props = props;
    }

}
