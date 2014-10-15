package com.atlassian.oauth.client.bv.dao.jira.impl;

import com.atlassian.oauth.client.bv.dao.AbstractBaseDAO;
import com.atlassian.oauth.client.bv.model.jira.Issue;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.atlassian.oauth.client.bv.dao.jira.support.SQLConstants.GET_LAST_RUN_DATE;
import static com.atlassian.oauth.client.bv.dao.jira.support.SQLConstants.WRITE_ISSUE;

/**
 * Created by Srikanth BG on 9/23/14.
 */

@Repository("IssueDAO")
public class IssueDAO extends AbstractBaseDAO {

    public void insertBatch(final List<Issue> issues) {

        	          getJdbcTemplate().batchUpdate(WRITE_ISSUE, new BatchPreparedStatementSetter() {

                          public void setValues(PreparedStatement ps, int i) throws SQLException {
                              Issue issue = issues.get(i);
                              ps.setLong(1, issue.getId());
                              ps.setInt(2, issue.getProjectID());
                              ps.setString(3, issue.getPriority());
                              ps.setString(4, issue.getStatus());
                              ps.setString(5, issue.getDesc());
                              ps.setString(6, issue.getAssignee());
                              ps.setString(7, issue.getReporter());
                              ps.setDate(8, issue.getCreateDate());
                              ps.setDate(9, issue.getUpdateDate());
                              //logger.info("prepared stmt -->" +ps);
                          }

                          public int getBatchSize() {
                              return issues.size();
                          }
                      });
                }

    public Date getLastRunDate(){

        return getJdbcTemplate().queryForObject(GET_LAST_RUN_DATE, Date.class);

    }

    public void updateLastRunDate()
    {
        getJdbcTemplate().execute("");
    }
}
