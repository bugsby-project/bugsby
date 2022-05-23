package repository;

import model.Involvement;
import model.User;

public interface InvolvementRepository extends Repository<Long, Involvement> {

    /**
     * Method for obtaining the involvements of a certain user
     * @param user, the user to retrieve their involvements in projects
     * @return an {@code Iterable} of involvements
     * @throws IllegalArgumentException if user is null
     */
    Iterable<Involvement> findInvolvementsByUser(User user);
}
