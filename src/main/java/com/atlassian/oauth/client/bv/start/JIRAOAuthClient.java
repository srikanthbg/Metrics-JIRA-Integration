package com.atlassian.oauth.client.bv.start;

import com.atlassian.oauth.client.bv.dao.jira.ProjectDAO;
import com.atlassian.oauth.client.bv.model.jira.Issue;
import com.atlassian.oauth.client.bv.model.jira.Project;
import com.atlassian.oauth.client.bv.process.*;
import com.atlassian.oauth.client.bv.process.Process;
import com.atlassian.oauth.client.bv.readers.jira.JiraAttributesReader;
import com.atlassian.oauth.client.bv.service.jira.ProjectService;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.io.*;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;


public class JIRAOAuthClient
{

    public static ProjectService getProjectService() {
        return projectService;
    }

    public static void setProjectService(ProjectService projectService) {
        JIRAOAuthClient.projectService = projectService;
    }

    @Resource(name = "ProjectService")
    private static ProjectService projectService;


    static Logger log = Logger.getLogger(JIRAOAuthClient.class.getName());
    private static final String CALLBACK_URI = "";
    protected static  String CONSUMER_PRIVATE_KEY = Base64.encode(getPrivateKey("private.der").getEncoded());

    public enum Command
    {
        REQUEST_TOKEN("requestToken"),
        ACCESS_TOKEN("accessToken"), REQUEST("request"),GET_ALL_PROJECTS("getAllProjects"),
        GET_ALL_ISSUES("getAllIssues"),GET_ISSUES_OF_PROJECT("getIssuesOfProject");

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

    public static void main(String[] args)
    {

        /*
            Load the jira properties
         */

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        Properties props = new Properties();
        InputStream input = null;

        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            input = loader.getResourceAsStream("jira.properties");
            props.load(input);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage() + " --> " + "jira properties not found");
        } catch (IOException e) {
            log.error(e.getMessage() + " --> " + "IO exception");
        }

        final String CONSUMER_KEY = props.getProperty("consumerKey");


        ArrayList<String> arguments = Lists.newArrayList(args);
        if (arguments.isEmpty())
        {
            throw new IllegalArgumentException("No command specified. Use one of " + getCommandNames() );
        }
        String action = arguments.get(0);
        if (Command.REQUEST_TOKEN.getName().equals(action))
        {
            String baseUrl = arguments.get(1);
            String callBack = "";
            if (arguments.size() == 3)
            {
                callBack = arguments.get(2);
            }
            AtlassianOAuthClient jiraoAuthClient = new AtlassianOAuthClient(CONSUMER_KEY, CONSUMER_PRIVATE_KEY, baseUrl, callBack);
            //STEP 1: Get request token
            TokenSecretVerifierHolder requestToken = jiraoAuthClient.getRequestToken();
            String authorizeUrl = jiraoAuthClient.getAuthorizeUrlForToken(requestToken.token);
            System.out.println("Token is " + requestToken.token);
            System.out.println("Token secret is " + requestToken.secret);
            System.out.println("Retrieved request token. go to " + authorizeUrl);
        }
        else if (Command.ACCESS_TOKEN.getName().equals(action))
        {
            String baseUrl = arguments.get(1);
            AtlassianOAuthClient jiraoAuthClient = new AtlassianOAuthClient(CONSUMER_KEY, CONSUMER_PRIVATE_KEY, baseUrl, CALLBACK_URI);
            String requestToken = arguments.get(2);
            String tokenSecret = arguments.get(3);
            String verifier = arguments.get(4);
            String accessToken = jiraoAuthClient.swapRequestTokenForAccessToken(requestToken, tokenSecret, verifier);
            System.out.println("Access token is : " + accessToken);
        }
        else if (Command.REQUEST.getName().equals(action))
        {
            /*
                Sample Request Test
             */

            AtlassianOAuthClient jiraoAuthClient = new AtlassianOAuthClient(CONSUMER_KEY, CONSUMER_PRIVATE_KEY, null, CALLBACK_URI);
            String accessToken = arguments.get(1);
            String url = arguments.get(2);
            String responseAsString = jiraoAuthClient.makeAuthenticatedRequest(url, accessToken);
            System.out.println("RESPONSE IS" + responseAsString);
        }
        else
        {


            AtlassianOAuthClient jiraoAuthClient = new AtlassianOAuthClient(CONSUMER_KEY, CONSUMER_PRIVATE_KEY, null, CALLBACK_URI);
            JiraAttributesReader jiraAttributesReader = new JiraAttributesReader(jiraoAuthClient,props);


            if(Command.GET_ALL_ISSUES.getName().equals(action))
            {
                List<Issue> listAllIssues = jiraAttributesReader.getAllIssues();
                Gson gson = new Gson();
                log.info(gson.toJson(listAllIssues));
            }
            else if(Command.GET_ALL_PROJECTS.getName().equals(action))
            {

                Process process = new ProcessProject();
                process.processModel(jiraAttributesReader,context);
            }

            else if(Command.GET_ISSUES_OF_PROJECT.getName().equals(action))
            {
                String projectName = arguments.get(1);

                List<Issue> issueList = jiraAttributesReader.getProjectIssues(projectName);

                for (Issue issue : issueList)
                {
                    log.info(issue.toString());

                }

                Gson gson = new Gson();
                log.info(gson.toJson(issueList));

            }

            else{
                log.error("Command " + action + " not supported. Only " + getCommandNames() + " are supported.");
            }
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

    private static PrivateKey getPrivateKey(String filename) {
        try {
            URL path = ClassLoader.getSystemResource(filename);
            if (path == null) {
                //The file was not found, insert error handling here
            }
            File f = new File(path.toURI());

            FileInputStream fis = new FileInputStream(f);
            DataInputStream dis = new DataInputStream(fis);
            byte[] keyBytes = new byte[(int) f.length()];
            dis.readFully(keyBytes);
            dis.close();

            PKCS8EncodedKeySpec spec =
                    new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);

        } catch (Exception e) {
                e.printStackTrace();
                return null;
        }
    }


}
