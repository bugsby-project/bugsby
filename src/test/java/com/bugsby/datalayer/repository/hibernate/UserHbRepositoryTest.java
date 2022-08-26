package com.bugsby.datalayer.repository.hibernate;

import com.bugsby.datalayer.model.User;
import com.bugsby.datalayer.validator.ErrorMessages;
import com.bugsby.datalayer.validator.UserValidator;
import com.bugsby.datalayer.validator.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class UserHbRepositoryTest {

    // connecting to the test database
    private static final UserHbRepository repo = new UserHbRepository(new UserValidator(), Constants.DEFAULT_PROPERTIES_FILE);

    /**
     * Method for inserting predetermined users.
     * Using the annotation @BeforeEach, it is guaranteed that at each test case these users will exist.
     */
    @BeforeEach
    void insertDefaultUsers() {
        Stream.of(Constants.defaultUsers).forEach(repo::save);
    }

    /**
     * Method for deleting all entries in the database.
     * Using the annotation @AfterEach, it is guaranteed that the database will be empty after every test.
     */
    @AfterEach
    void deleteDefaultUsers() {
        StreamSupport.stream(repo.findAll().spliterator(), false)
                .forEach(user -> repo.delete(user.getId()));
    }

    @TestFactory
    Stream<DynamicTest> save() {
        record TestCase(String name, User user, Optional<User> expected, Class<? extends Exception> exception,
                        String errorMessage) {
            public void check() {
                try {
                    Optional<User> computed = repo.save(TestCase.this.user);
                    Assertions.assertNull(TestCase.this.exception);
                    Assertions.assertEquals(TestCase.this.expected, computed);

                    if (computed.isEmpty())  // if successfully saved, find the user
                        Assertions.assertTrue(repo.find(TestCase.this.user.getId()).isPresent());
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    Assertions.assertEquals(TestCase.this.exception, e.getClass());
                    Assertions.assertEquals(TestCase.this.errorMessage, e.getMessage());
                }
            }
        }

        final User userToSave = new User(Long.MAX_VALUE, "michael_k", "mmk", "Michael", "K", "m_k@outlook.com");
        final User userDuplicatedId = Constants.defaultUsers[0].clone();
        var testCases = new TestCase[]{
                new TestCase("Save User successfully", userToSave, Optional.empty(), null, ""),
                new TestCase("Save User duplicate id", userDuplicatedId, Optional.of(userDuplicatedId), null, ""),
                new TestCase("Save User null entity", null, null, IllegalArgumentException.class, null),
                new TestCase("Save User invalid entity", new User("", "p", "fn", "ln", "a@g.com"), null, ValidationException.class, ErrorMessages.USER_INVALID_USERNAME),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> delete() {
        record TestCase(String name, Long id, Optional<User> expected, Class<? extends Exception> exception,
                        String errorMessage) {
            public void check() {
                try {
                    Optional<User> computed = repo.delete(TestCase.this.id);
                    Assertions.assertNull(TestCase.this.exception);

                    Assertions.assertEquals(TestCase.this.expected, computed);

                    if (computed.isPresent()) {  // successfully deleted
                        Assertions.assertTrue(repo.find(TestCase.this.id).isEmpty());
                    }
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    Assertions.assertEquals(TestCase.this.exception, e.getClass());
                    Assertions.assertEquals(TestCase.this.errorMessage, e.getMessage());
                }
            }
        }

        var testCases = new TestCase[]{
                new TestCase("Delete User successfully", Constants.defaultUsers[0].getId(), Optional.of(Constants.defaultUsers[0]), null, null),
                new TestCase("Delete User non-existent id", Long.MAX_VALUE, Optional.empty(), null, null),
                new TestCase("Delete User null", null, null, IllegalArgumentException.class, null),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> update() {
        record TestCase(String name, User user, Optional<User> expected, Class<? extends Exception> exception,
                        String message) {
            public void check() {
                try {
                    Optional<User> computed = repo.update(TestCase.this.user);
                    Assertions.assertNull(TestCase.this.exception);

                    Assertions.assertEquals(TestCase.this.expected, computed);

                    if (computed.isEmpty()) {  // if successfully updated
                        Optional<User> updatedUser = repo.find(TestCase.this.user.getId());
                        Assertions.assertTrue(updatedUser.isPresent());
                        Assertions.assertEquals(TestCase.this.user, updatedUser.get());
                    }
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    Assertions.assertEquals(TestCase.this.exception, e.getClass());
                    Assertions.assertEquals(TestCase.this.message, e.getMessage());
                }
            }
        }

        User userExisting = Constants.defaultUsers[0].clone();
        userExisting.setUsername("isabelle");

        final User userNotExisting = new User(Long.MAX_VALUE, "abc", "abc", "abc", "abc", "abc@gmail.com");

        User invalidUser = Constants.defaultUsers[0].clone();
        invalidUser.setPassword("");

        var testCases = new TestCase[]{
                new TestCase("Update User successfully", userExisting, Optional.empty(), null, null),
                new TestCase("Update User non-existing", userNotExisting, Optional.of(userNotExisting), null, null),
                new TestCase("Update User null", null, null, IllegalArgumentException.class, null),
                new TestCase("Update User invalid", invalidUser, null, ValidationException.class, ErrorMessages.USER_INVALID_PASSWORD),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> find() {
        record TestCase(String name, Long id, Optional<User> expected) {
            public void check() {
                Optional<User> computed = repo.find(TestCase.this.id);
                Assertions.assertEquals(TestCase.this.expected, computed);
            }
        }

        final Long idToFind = 1L;
        Optional<User> userToFind = Arrays.stream(Constants.defaultUsers)
                .filter(user -> user.getId().equals(idToFind))
                .findFirst();

        var testCases = new TestCase[]{
                new TestCase("Find User existing", idToFind, userToFind),
                new TestCase("Find User not existing", Long.MAX_VALUE, Optional.empty()),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @Test
    void findInvalidId() {
        try {
            repo.find(null);
            Assertions.fail();
        } catch (IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    void findAll() {
        // check if every default user is in the database
        var usersIterable = repo.findAll();
        List<User> users = StreamSupport.stream(usersIterable.spliterator(), false).collect(Collectors.toList());
        Assertions.assertEquals(Constants.defaultUsers.length, users.size());
        for (User user : Constants.defaultUsers) {
            Assertions.assertTrue(users.contains(user));
        }
    }

    @TestFactory
    Stream<DynamicTest> findUserByUsername() {
        record TestCase(String name, String username, Optional<User> expected, Class<? extends Exception> exception) {
            public void check() {
                try {
                    Optional<User> computed = repo.findUserByUsername(TestCase.this.username);
                    Assertions.assertNull(TestCase.this.exception);
                    Assertions.assertEquals(TestCase.this.expected, computed);
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    Assertions.assertEquals(TestCase.this.exception, e.getClass());
                }
            }
        }

        var testCases = new TestCase[]{
                new TestCase("Find User by Username successful", Constants.defaultUsers[0].getUsername(), Optional.of(Constants.defaultUsers[0]), null),
                new TestCase("Find User by Username unsuccessful", "x", Optional.empty(), null),
                new TestCase("Find User by Username null", null, null, IllegalArgumentException.class),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> findUserByEmail() {
        record TestCase(String name, String email, Optional<User> expected, Class<? extends Exception> exception) {
            public void check() {
                try {
                    Optional<User> computed = repo.findUserByEmail(TestCase.this.email);
                    Assertions.assertNull(TestCase.this.exception);
                    Assertions.assertEquals(TestCase.this.expected, computed);
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    Assertions.assertEquals(TestCase.this.exception, e.getClass());
                }
            }
        }

        var testCases = new TestCase[]{
                new TestCase("Find User by Email successful", Constants.defaultUsers[0].getEmail(), Optional.of(Constants.defaultUsers[0]), null),
                new TestCase("Find User by Email unsuccessful", "x", Optional.empty(), null),
                new TestCase("Find User by Email null", null, null, IllegalArgumentException.class),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @Test
    void testGetAllUsernames() {
        List<String> expected = Arrays.stream(Constants.defaultUsers)
                .map(User::getUsername)
                .collect(Collectors.toList());
        Iterable<String> computedIterable = repo.getAllUsernames();
        List<String> computed = StreamSupport.stream(computedIterable.spliterator(), false)
                .collect(Collectors.toList());
        Assertions.assertEquals(expected.size(), computed.size());
        Assertions.assertTrue(expected.containsAll(computed));
        Assertions.assertTrue(computed.containsAll(expected));
    }
}