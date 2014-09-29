package com.atlassian.oauth.client.bv.dao.jira;

import com.atlassian.oauth.client.bv.dao.AbstractBaseDAO;
import com.atlassian.oauth.client.bv.model.jira.Project;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.atlassian.oauth.client.bv.dao.support.SQLConstants.WRITE_PROJECT;

/**
 * Created by Srikanth BG on 9/23/14.
 */

@Repository("ProjectDAO")
public class ProjectDAO extends AbstractBaseDAO {

    public void insertBatch(final List<Project> projects){

        	          getJdbcTemplate().batchUpdate(WRITE_PROJECT, new BatchPreparedStatementSetter() {

                          @Override
                          public void setValues(PreparedStatement ps, int i) throws SQLException {
                              Project project = projects.get(i);
                              ps.setInt(1, project.getId());
                              ps.setString(2, project.getName());
                              ps.setString(3, project.getKey());
                              System.out.println(i + "-->" + ps);
                          }

                          @Override
                          public int getBatchSize() {
                              return projects.size();
                          }
                      });
                }


            public void insertBatch2(final String sql){

        	        getJdbcTemplate().batchUpdate(new String[]{sql});

        	    }

}
