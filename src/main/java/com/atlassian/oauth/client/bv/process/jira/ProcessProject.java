package com.atlassian.oauth.client.bv.process.jira;

import com.atlassian.oauth.client.bv.model.jira.Project;
import com.google.gson.*;
import org.apache.log4j.Logger;

import java.lang.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Srikanth BG on 9/26/14.
 *
 * Process the json string which contains the project json response
 */
public class ProcessProject implements Process {

    static Logger log = Logger.getLogger(ProcessProject.class.getName());


    public <T> List<T> processModel(String jsonString)

    {

        JsonParser jsonParser = new JsonParser();
        List<T> projectList = new ArrayList<T>();


        JsonArray projectArray = (JsonArray) jsonParser.parse(jsonString);
        Iterator projectIter = projectArray.iterator();

        JsonObject loopProjectObj = new JsonObject();
        try {

            while (projectIter.hasNext()) {
                loopProjectObj = (JsonObject) projectIter.next();
                Project projObj = new Project();

                /*
                    Get project id
                 */
                if (!(loopProjectObj.get("id") instanceof JsonNull))
                    projObj.setId(loopProjectObj.get("id").getAsInt());

                /*
                    get project key
                 */
                if (!(loopProjectObj.get("key") instanceof JsonNull))
                    projObj.setKey(loopProjectObj.get("key").getAsString());

                /*
                    get project name
                 */
                if (!(loopProjectObj.get("name") instanceof JsonNull))
                    projObj.setName(loopProjectObj.get("name").getAsString());


                projectList.add((T)projObj);

            }

            Gson gson = new Gson();
            log.info(gson.toJson(projectList));

            return projectList;

        } catch (Exception e) {
            log.error(e.getStackTrace());
            log.error("FATAl : could not load project list from JIRA");
            return null;
        }
    }
}

