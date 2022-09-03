package com.bugsby.datalayer.repository.hibernate;

import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.User;
import com.bugsby.datalayer.repository.IssueRepository;
import com.bugsby.datalayer.validator.Validator;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Collections;

@Repository
public class IssueHbRepository extends AbstractHbRepository<Long, Issue> implements IssueRepository {
    @Autowired
    protected IssueHbRepository(@Autowired Validator<Long, Issue> validator,
                                @Value("${properties-file}") String propertiesFile) {
        super(validator, propertiesFile);
    }

    @Override
    protected Query<Issue> getFindQuery(Session session, Long aLong) {
        return session.createQuery("from Issue where id=:id", Issue.class)
                .setParameter("id", aLong);
    }

    @Override
    protected Query<Issue> getFindAllQuery(Session session) {
        return session.createQuery("from Issue", Issue.class);
    }

    @Override
    public Iterable<Issue> getAssignedIssues(User user) {
        if (user == null) {
            throw new IllegalArgumentException();
        }

        try (Session session = sessionFactory.openSession()) {
            Query<Issue> query = session
                    .createQuery("from Issue where assignee=:assignee order by status desc", Issue.class)
                    .setParameter("assignee", user);
            return super.filter(session, query);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
