package com.atlassian.oauth.client.bv.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;


/**
 * Created by Srikanth BG on 9/12/14.
 */
public class JiraJsonParser {

    static Logger log = Logger.getLogger(JiraJsonParser.class.getName());

    public JsonArray jsonArrayParser(String jsonString, String key) {
        try {

            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonString);
            JsonArray jsonArray = (JsonArray) jsonObject.get(key);
            return jsonArray;
        } catch (Exception e) {
            log.error("Error in parsing json string to json array");
            return null;
        }
    }

    public JsonObject jsonObjectParser(String jsonString) {
        try {

            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonString);
            return jsonObject;
        } catch (Exception e) {
            log.error("Error in parsing json string to json object");
            return null;
        }
    }
}
