package com.atlassian.oauth.client.bv.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

public abstract class AbstractBaseDAO extends JdbcDaoSupport
{
	@Autowired
	private DataSource dataSource;

	@PostConstruct
	void init()
	{
		setDataSource(dataSource);
	}
}
