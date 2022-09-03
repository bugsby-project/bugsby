package com.bugsby.datalayer.repository.hibernate;

import com.bugsby.datalayer.model.Involvement;
import com.bugsby.datalayer.model.User;
import com.bugsby.datalayer.repository.InvolvementRepository;
import com.bugsby.datalayer.validator.Validator;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Collections;

@Repository
public class InvolvementHbRepository extends AbstractHbRepository<Long, Involvement> implements InvolvementRepository {
    @Autowired
    protected InvolvementHbRepository(@Autowired Validator<Long, Involvement> validator,
                                      @Value("${properties-file}") String propertiesFile) {
        super(validator, propertiesFile);
    }

    @Override
    protected Query<Involvement> getFindQuery(Session session, Long aLong) {
        return session.createQuery("from Involvement where id=:id", Involvement.class)
                .setParameter("id", aLong);
    }

    @Override
    protected Query<Involvement> getFindAllQuery(Session session) {
        return session.createQuery("from Involvement", Involvement.class);
    }

    @Override
    public Iterable<Involvement> findInvolvementsByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException();
        }

        try (Session session = sessionFactory.openSession()) {
            Query<Involvement> query = session
                    .createQuery("from Involvement where user=:user", Involvement.class)
                    .setParameter("user", user);
            return super.filter(session, query);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
