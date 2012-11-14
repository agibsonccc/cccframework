package com.ccc.util.springhibernate.model;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.SequenceGenerator;

public class UseExistingOrGenerateIdGenerator extends SequenceGenerator {
    @Override
    public Serializable generate(SessionImplementor session, Object object)
                        throws HibernateException {
        Serializable id = session.getEntityPersister(null, object)
                      .getClassMetadata().getIdentifier(object, session);
        return id != null ? id : super.generate(session, object);
    }
}