package com.ccc.user.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.ccc.users.core.Question;
import com.ccc.util.springhibernate.dao.GenericManager;

@Repository("questionManager")
public class QuestionManager extends GenericManager<Question> {

public QuestionManager() {
	super(Question.class);
}
	

}
