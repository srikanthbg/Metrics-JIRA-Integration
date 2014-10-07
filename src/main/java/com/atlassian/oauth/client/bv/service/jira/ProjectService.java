package com.atlassian.oauth.client.bv.service.jira;

import com.atlassian.oauth.client.bv.dao.jira.ProjectDAO;
import com.atlassian.oauth.client.bv.model.jira.Project;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Srikanth BG on 9/24/14.
 */

@Service("ProjectService")
public class ProjectService {

    @Resource(name = "ProjectDAO")
    private ProjectDAO projectDao;


    public void insertBatch(final List<Project> projects)
    {
        projectDao.insertBatch(projects);
    }
}
