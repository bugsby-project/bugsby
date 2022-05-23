package repository.hibernate;

import model.Issue;
import model.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import repository.IssueRepository;
import validator.Validator;

import java.util.Collections;

public class IssueHbRepository extends AbstractHbRepository<Long, Issue> implements IssueRepository {
    protected IssueHbRepository(Validator<Long, Issue> validator, String propertiesFile) {
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
