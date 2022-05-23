package repository.hibernate;

import model.Project;
import org.hibernate.Session;
import org.hibernate.query.Query;
import repository.ProjectRepository;
import validator.Validator;

public class ProjectHbRepository extends AbstractHbRepository<Long, Project> implements ProjectRepository {
    protected ProjectHbRepository(Validator<Long, Project> validator, String propertiesFile) {
        super(validator, propertiesFile);
    }

    @Override
    protected Query<Project> getFindQuery(Session session, Long aLong) {
        return session.createQuery("from Project where id=:id", Project.class)
                .setParameter("id", aLong);
    }

    @Override
    protected Query<Project> getFindAllQuery(Session session) {
        return session.createQuery("from Project", Project.class);
    }
}
