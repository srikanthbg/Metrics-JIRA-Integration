package com.atlassian.oauth.client.bv.dao.support;

/**
 * Created by Srikanth BG on 9/23/14.
 */
public interface SQLConstants {

    String WRITE_ISSUE = "INSERT INTO bv_metrics.issue " +
                         "(issueID,projectID,priority,status,desc,assignee,reporter,createDate,updateDate" +
                         "VALUES (?,?,?,?,?,?,?,?,?)  ON DUPLICATE KEY UPDATE createDate=?,updateDate=?";

    String WRITE_PROJECT = "INSERT INTO bv_metrics.project (`projectID`,`name`,`key`) VALUES (?,?,?)";
}
