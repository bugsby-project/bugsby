package repository.hibernate;

import model.*;
import org.junit.jupiter.api.*;
import validator.IssueValidator;
import validator.ProjectValidator;
import validator.UserValidator;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class IssueHbRepositoryTest {

    private static final UserHbRepository userRepo = new UserHbRepository(new UserValidator(), Constants.DEFAULT_PROPERTIES_FILE);
    private static final ProjectHbRepository projectRepo = new ProjectHbRepository(new ProjectValidator(), Constants.DEFAULT_PROPERTIES_FILE);
    private static final IssueHbRepository issueRepo = new IssueHbRepository(new IssueValidator(), Constants.DEFAULT_PROPERTIES_FILE);

    /**
     * Method for inserting the default issues in the database.
     * Thanks to the @BeforeEach annotation, for each test it is guaranteed that these involvements will be
     * present in the database.
     */
    @BeforeEach
    void insertDefaultIssues() {
        Arrays.stream(Constants.defaultUsers).forEach(userRepo::save);
        Arrays.stream(Constants.defaultProjects).forEach(projectRepo::save);
        Arrays.stream(Constants.defaultIssues).forEach(issueRepo::save);
    }

    /**
     * Method for deleting every involvement from the database.
     * Thanks to the @AfterEach annotation, it is guaranteed that for each test the database will be in a clean state.
     */
    @AfterEach
    void deleteDefaultIssues() {
        StreamSupport.stream(issueRepo.findAll().spliterator(), false)
                .map(Issue::getId)
                .forEach(issueRepo::delete);
        StreamSupport.stream(userRepo.findAll().spliterator(), false)
                .map(User::getId)
                .forEach(userRepo::delete);
        StreamSupport.stream(projectRepo.findAll().spliterator(), false)
                .map(Project::getId)
                .forEach(projectRepo::delete);
    }

    @TestFactory
    Stream<DynamicTest> save() {
        record TestCase(String name, Issue issue, Optional<Issue> expected,
                        Class<? extends Exception> exception,
                        String message) {
            public void check() {
                try {
                    Optional<Issue> computed = issueRepo.save(TestCase.this.issue);
                    Assertions.assertNull(TestCase.this.exception);

                    Assertions.assertEquals(TestCase.this.expected, computed);

                    if (computed.isEmpty()) {  // successfully saved
                        Optional<Issue> issueFound = issueRepo.find(TestCase.this.issue.getId());
                        Assertions.assertTrue(issueFound.isPresent());

                        // check if the relationships are set
                        Assertions.assertTrue(issueFound.get().getProject().getIssues().contains(issueFound.get()));
                    }
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    Assertions.assertEquals(TestCase.this.exception, e.getClass());
                    Assertions.assertEquals(TestCase.this.message, e.getMessage());
                }
            }
        }

        final Issue toSave = new Issue("Test save", "Description", Severity.BLOCKER, Status.TO_DO, IssueType.BUG, Constants.defaultProjects[0], Constants.defaultUsers[2]);
        var testCases = new TestCase[]{
                new TestCase("Save Issue successful", toSave, Optional.empty(), null, null),
                new TestCase("Save Issue null", null, null, IllegalArgumentException.class, null),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> delete() {
        record TestCase(String name, Long id, Optional<Issue> expected, Class<? extends Exception> exception,
                        String errorMessage) {
            public void check() {
                try {
                    Optional<Issue> computed = issueRepo.delete(TestCase.this.id);
                    Assertions.assertNull(TestCase.this.exception);

                    Assertions.assertEquals(TestCase.this.expected, computed);

                    if (computed.isPresent()) {  // successfully deleted
                        Assertions.assertTrue(issueRepo.find(TestCase.this.id).isEmpty());

                        Optional<Project> project = projectRepo.find(computed.get().getProject().getId());
                        Assertions.assertTrue(project.isPresent());
                        Assertions.assertFalse(project.get().getIssues().contains(computed.get()));
                    }
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    Assertions.assertEquals(TestCase.this.exception, e.getClass());
                    Assertions.assertEquals(TestCase.this.errorMessage, e.getMessage());
                }
            }
        }

        var testCases = new TestCase[]{
                new TestCase("Delete Issue successful", Constants.defaultIssues[0].getId(), Optional.of(Constants.defaultIssues[0]), null, null),
                new TestCase("Delete Issue non-existing id", Long.MAX_VALUE, Optional.empty(), null, null),
                new TestCase("Delete Issue null", null, null, IllegalArgumentException.class, null),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> update() {
        record TestCase(String name, Issue issue, Optional<Issue> expected,
                        Class<? extends Exception> exception,
                        String message) {
            public void check() {
                try {
                    Optional<Issue> computed = issueRepo.update(TestCase.this.issue);
                    Assertions.assertNull(TestCase.this.exception);

                    Assertions.assertEquals(TestCase.this.expected, computed);

                    if (computed.isEmpty()) {  // if successfully updated
                        Optional<Issue> updatedIssue = issueRepo.find(TestCase.this.issue.getId());
                        Assertions.assertTrue(updatedIssue.isPresent());
                        Assertions.assertEquals(TestCase.this.issue, updatedIssue.get());
                    }
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    Assertions.assertEquals(TestCase.this.exception, e.getClass());
                    Assertions.assertEquals(TestCase.this.message, e.getMessage());
                }
            }
        }

        Issue toUpdate = Constants.defaultIssues[0].clone();
        toUpdate.setTitle("Updated Title");
        toUpdate.setType(IssueType.ENHANCEMENT);

        var testCases = new TestCase[]{
                new TestCase("Update Issue successful", toUpdate, Optional.empty(), null, null),
                new TestCase("Update Issue null", null, null, IllegalArgumentException.class, null),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> find() {
        record TestCase(String name, Long id, Optional<Issue> expected, Class<? extends Exception> exception,
                        String message) {
            public void check() {
                try {
                    Optional<Issue> computed = issueRepo.find(TestCase.this.id);
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
                new TestCase("Find Issue successful", Constants.defaultIssues[0].getId(), Optional.of(Constants.defaultIssues[0]), null, null),
                new TestCase("Find Issue unsuccessful", Long.MAX_VALUE, Optional.empty(), null, null),
                new TestCase("Find Issue null id", null, null, IllegalArgumentException.class, null),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @Test
    void findAll() {
        List<Issue> issues = StreamSupport
                .stream(issueRepo.findAll().spliterator(), false)
                .collect(Collectors.toList());
        Assertions.assertEquals(Constants.defaultIssues.length, issues.size());
        Stream.of(Constants.defaultIssues).map(issues::contains).forEach(Assertions::assertTrue);
    }

    @TestFactory
    Stream<DynamicTest> getAssignedIssues() {
        record TestCase(String name, User user, Iterable<Issue> expected, Class<? extends Exception> exception) {
            public void check() {
                try {
                    Iterable<Issue> computed = issueRepo.getAssignedIssues(TestCase.this.user);
                    Assertions.assertNull(TestCase.this.exception);

                    Assertions.assertEquals(TestCase.this.expected, computed);
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    Assertions.assertEquals(TestCase.this.exception, e.getClass());
                }
            }
        }

        Iterable<Issue> assignedIssuesUser = Stream.of(Constants.defaultIssues)
                .filter(issue -> issue.getAssignee() != null && issue.getAssignee().equals(Constants.defaultUsers[0]))
                .sorted((issue1, issue2) -> issue2.getStatus().compareTo(issue1.getStatus()))
                .collect(Collectors.toList());

        var testCases = new TestCase[]{
                new TestCase("Get Assigned Issues null user", null, null, IllegalArgumentException.class),
                new TestCase("Get Assigned Issues valid user", Constants.defaultUsers[0], assignedIssuesUser, null)
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }
}