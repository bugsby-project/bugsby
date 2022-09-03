package com.bugsby.datalayer.repository.hibernate;

import com.bugsby.datalayer.model.User;
import com.bugsby.datalayer.repository.UserRepository;
import com.bugsby.datalayer.validator.Validator;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserHbRepository extends AbstractHbRepository<Long, User> implements UserRepository {
    protected UserHbRepository(Validator<Long, User> validator, String propertiesFile) {
        super(validator, propertiesFile);
    }

    @Override
    protected Query<User> getFindQuery(Session session, Long aLong) {
        return session.createQuery("from User where id=:id", User.class)
                .setParameter("id", aLong);
    }

    @Override
    protected Query<User> getFindAllQuery(Session session) {
        return session.createQuery("from User", User.class);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException();
        }
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session
                    .createQuery("from User where username=:username", User.class)
                    .setParameter("username", username);
            List<User> users = StreamSupport.stream(super.filter(session, query).spliterator(), false)
                    .collect(Collectors.toList());
            return users.stream().findAny();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException();
        }
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session
                    .createQuery("from User where email=:email", User.class)
                    .setParameter("email", email);
            List<User> users = StreamSupport.stream(super.filter(session, query).spliterator(), false)
                    .collect(Collectors.toList());
            return users.stream().findAny();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Iterable<String> getAllUsernames() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            List<String> usernames = session
                    .createQuery("select username from User", String.class)
                    .list();
            transaction.commit();
            return usernames;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            return Collections.emptyList();
        }
    }
}
