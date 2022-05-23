package repository.hibernate;

import model.Involvement;
import model.Project;
import model.Role;
import model.User;
import org.junit.jupiter.api.*;
import validator.InvolvementValidator;
import validator.ProjectValidator;
import validator.UserValidator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class InvolvementHbRepositoryTest {

    private static final InvolvementHbRepository involvementRepo = new InvolvementHbRepository(new InvolvementValidator(), Constants.DEFAULT_PROPERTIES_FILE);
    private static final UserHbRepository userRepo = new UserHbRepository(new UserValidator(), Constants.DEFAULT_PROPERTIES_FILE);
    private static final ProjectHbRepository projectRepo = new ProjectHbRepository(new ProjectValidator(), Constants.DEFAULT_PROPERTIES_FILE);

    /**
     * Method for inserting the default involvements in the database.
     * Thanks to the @BeforeEach annotation, for each test it is guaranteed that these involvements will be
     * present in the database.
     */
    @BeforeEach
    void insertDefaultInvolvements() {
        Stream.of(Constants.defaultUsers).forEach(userRepo::save);
        Stream.of(Constants.defaultProjects).forEach(projectRepo::save);
        Stream.of(Constants.defaultInvolvements).forEach(involvementRepo::save);
    }

    /**
     * Method for deleting every involvement from the database.
     * Thanks to the @AfterEach annotation, it is guaranteed that for each test the database will be in a clean state.
     */
    @AfterEach
    void deleteData() {
        StreamSupport.stream(involvementRepo.findAll().spliterator(), false).forEach(involvement -> involvementRepo.delete(involvement.getId()));
        StreamSupport.stream(userRepo.findAll().spliterator(), false).forEach(user -> userRepo.delete(user.getId()));
        StreamSupport.stream(projectRepo.findAll().spliterator(), false).forEach(project -> projectRepo.delete(project.getId()));
    }

    @TestFactory
    Stream<DynamicTest> save() {
        record TestCase(String name, Involvement involvement, Optional<Involvement> expected,
                        Class<? extends Exception> exception,
                        String message) {
            public void check() {
                try {
                    Optional<Involvement> computed = involvementRepo.save(TestCase.this.involvement);
                    Assertions.assertNull(TestCase.this.exception);

                    Assertions.assertEquals(TestCase.this.expected, computed);

                    if (computed.isEmpty()) {  // successfully saved
                        Optional<Involvement> involvementFound = involvementRepo.find(TestCase.this.involvement.getId());
                        Assertions.assertTrue(involvementFound.isPresent());

                        // check if the corresponding user and project have the respective involvement
                        Assertions.assertTrue(involvementFound.get().getUser().getInvolvements().contains(involvementFound.get()));
                        Assertions.assertTrue(involvementFound.get().getProject().getInvolvements().contains(involvementFound.get()));
                    }
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    Assertions.assertEquals(TestCase.this.exception, e.getClass());
                    Assertions.assertEquals(TestCase.this.message, e.getMessage());
                }
            }
        }

        final Involvement toSave = new Involvement(null, Role.UI_DESIGNER, Constants.defaultUsers[1], Constants.defaultProjects[2]);
        var testCases = new TestCase[]{
                new TestCase("Save Involvement successful", toSave, Optional.empty(), null, null),
                new TestCase("Save Involvement null", null, null, IllegalArgumentException.class, null),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> delete() {
        record TestCase(String name, Long id, Optional<Involvement> expected, Class<? extends Exception> exception,
                        String errorMessage) {
            public void check() {
                try {
                    Optional<Involvement> computed = involvementRepo.delete(TestCase.this.id);
                    Assertions.assertNull(TestCase.this.exception);

                    Assertions.assertEquals(TestCase.this.expected, computed);

                    if (computed.isPresent()) {  // successfully deleted
                        Assertions.assertTrue(involvementRepo.find(TestCase.this.id).isEmpty());

                        // check if the corresponding user and project don't have the involvement anymore
                        Optional<User> user = userRepo.find(computed.get().getUser().getId());
                        Assertions.assertTrue(user.isPresent());
                        Assertions.assertFalse(user.get().getInvolvements().contains(computed.get()));

                        Optional<Project> project = projectRepo.find(computed.get().getUser().getId());
                        Assertions.assertTrue(project.isPresent());
                        Assertions.assertFalse(project.get().getInvolvements().contains(computed.get()));
                    }
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    Assertions.assertEquals(TestCase.this.exception, e.getClass());
                    Assertions.assertEquals(TestCase.this.errorMessage, e.getMessage());
                }
            }
        }

        var testCases = new TestCase[]{
                new TestCase("Delete Involvement successful", Constants.defaultInvolvements[0].getId(), Optional.of(Constants.defaultInvolvements[0]), null, null),
                new TestCase("Delete Involvement non-existing id", Long.MAX_VALUE, Optional.empty(), null, null),
                new TestCase("Delete Involvement null", null, null, IllegalArgumentException.class, null),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> update() {
        record TestCase(String name, Involvement involvement, Optional<Involvement> expected,
                        Class<? extends Exception> exception,
                        String message) {
            public void check() {
                try {
                    Optional<Involvement> computed = involvementRepo.update(TestCase.this.involvement);
                    Assertions.assertNull(TestCase.this.exception);

                    Assertions.assertEquals(TestCase.this.expected, computed);

                    if (computed.isEmpty()) {  // if successfully updated
                        Optional<Involvement> updatedInvolvement = involvementRepo.find(TestCase.this.involvement.getId());
                        Assertions.assertTrue(updatedInvolvement.isPresent());
                        Assertions.assertEquals(TestCase.this.involvement, updatedInvolvement.get());
                    }
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    Assertions.assertEquals(TestCase.this.exception, e.getClass());
                    Assertions.assertEquals(TestCase.this.message, e.getMessage());
                }
            }
        }

        Involvement toUpdate = Constants.defaultInvolvements[0].clone();
        toUpdate.setRole(Role.UX_DESIGNER);
        var testCases = new TestCase[]{
                new TestCase("Update Involvement successful", toUpdate, Optional.empty(), null, null),
                new TestCase("Update Involvement null", null, null, IllegalArgumentException.class, null),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> find() {
        record TestCase(String name, Long id, Optional<Involvement> expected, Class<? extends Exception> exception,
                        String message) {
            public void check() {
                try {
                    Optional<Involvement> computed = involvementRepo.find(TestCase.this.id);
                    if (TestCase.this.exception != null) {
                        Assertions.fail();
                    }

                    Assertions.assertEquals(TestCase.this.expected, computed);
                } catch (Exception e) {
                    if (TestCase.this.exception == null) {
                        Assertions.fail();
                    }

                    Assertions.assertEquals(TestCase.this.exception, e.getClass());
                }
            }
        }

        var testCases = new TestCase[]{
                new TestCase("Find Involvement successful", Constants.defaultInvolvements[0].getId(), Optional.of(Constants.defaultInvolvements[0]), null, null),
                new TestCase("Find Involvement unsuccessful", Long.MAX_VALUE, Optional.empty(), null, null),
                new TestCase("Find Involvement null id", null, null, IllegalArgumentException.class, null),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @Test
    void findAll() {
        List<Involvement> involvements = StreamSupport
                .stream(involvementRepo.findAll().spliterator(), false)
                .collect(Collectors.toList());
        Assertions.assertEquals(Constants.defaultInvolvements.length, involvements.size());
        Stream.of(Constants.defaultInvolvements).map(involvements::contains).forEach(Assertions::assertTrue);
    }

    @TestFactory
    Stream<DynamicTest> findInvolvementsByUser() {
        record TestCase(String name, User user, Iterable<Involvement> expected, Class<? extends Exception> exception) {
            public void check() {
                try {
                    Iterable<Involvement> computed = involvementRepo.findInvolvementsByUser(TestCase.this.user);
                    Assertions.assertNull(TestCase.this.exception);
                    Assertions.assertEquals(expected, computed);
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    Assertions.assertEquals(e.getClass(), exception);
                }
            }
        }

        User nonExistentUser = new User(Long.MAX_VALUE, "", "", "", "", "");

        List<Involvement> involvementsUser0 = Arrays.stream(Constants.defaultInvolvements)
                .filter(involvement -> involvement.getUser().equals(Constants.defaultUsers[0]))
                .collect(Collectors.toList());

        List<Involvement> involvementsUser2 = Arrays.stream(Constants.defaultInvolvements)
                .filter(involvement -> involvement.getUser().equals(Constants.defaultUsers[2]))
                .collect(Collectors.toList());

        var testCases = new TestCase[]{
                new TestCase("Find Involvements non-existent user", nonExistentUser, Collections.emptyList(), null),
                new TestCase("Find Involvements null user", null, null, IllegalArgumentException.class),
                new TestCase("Find Involvements successful empty", Constants.defaultUsers[2], involvementsUser2, null),
                new TestCase("Find Involvements successful non zero size", Constants.defaultUsers[0], involvementsUser0, null)
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }
}