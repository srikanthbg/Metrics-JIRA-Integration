package com.atlassian.oauth.client.bv.model.jira;

/**
 * Created by Srikanth BG on 9/12/14.
 */
public class Project {

    private int id;
    private String key;
    private String name;

    public Project()
    {

    }
    public Project(int id, String key, String name) {
        this.id = id;
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
