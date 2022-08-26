package com.bugsby.datalayer.repository.hibernate;

import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.validator.ErrorMessages;
import com.bugsby.datalayer.validator.ProjectValidator;
import com.bugsby.datalayer.validator.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class ProjectHbRepositoryTest {

    private static final ProjectHbRepository repo = new ProjectHbRepository(new ProjectValidator(), Constants.DEFAULT_PROPERTIES_FILE);

    /**
     * Method for inserting the default projects in the database.
     * Due to the @BeforeEach annotation, it is guaranteed that these projects will exist in the database
     * in every test.
     */
    @BeforeEach
    void insertDefaultProjects() {
        Stream.of(Constants.defaultProjects).forEach(repo::save);
    }

    /**
     * Method for deleting all the projects in the database.
     * Due to the @AfterEach annotation, it is guaranteed that after each test there won't be any project
     * in the database.
     */
    @AfterEach
    void deleteAllProjects() {
        StreamSupport.stream(repo.findAll().spliterator(), false).forEach(project -> repo.delete(project.getId()));
    }

    @TestFactory
    Stream<DynamicTest> save() {
        record TestCase(String name, Project project, Optional<Project> expected, Class<? extends Exception> exception,
                        String message) {
            public void check() {
                try {
                    Optional<Project> computed = repo.save(TestCase.this.project);
                    Assertions.assertNull(TestCase.this.exception);

                    Assertions.assertEquals(TestCase.this.expected, computed);
                    if (computed.isEmpty()) {  // successfully saved
                        Assertions.assertTrue(repo.find(TestCase.this.project.getId()).isPresent());
                    }
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    Assertions.assertEquals(TestCase.this.exception, e.getClass());
                    Assertions.assertEquals(TestCase.this.message, e.getMessage());
                }
            }
        }

        final Project projectToSave = new Project(Long.MAX_VALUE, "P", "P", LocalDateTime.now());
        var testCases = new TestCase[]{
                new TestCase("Save Project successfully", projectToSave, Optional.empty(), null, null),
                new TestCase("Save Project null", null, null, IllegalArgumentException.class, null),
                new TestCase("Save Project invalid", new Project("", "P", LocalDateTime.now()), null, ValidationException.class, ErrorMessages.PROJECT_INVALID_TITLE),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> delete() {
        record TestCase(String name, Long id, Optional<Project> expected, Class<? extends Exception> exception,
                        String errorMessage) {
            public void check() {
                try {
                    Optional<Project> computed = repo.delete(TestCase.this.id);
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
                new TestCase("Delete Project successfully", Constants.defaultProjects[0].getId(), Optional.of(Constants.defaultProjects[0]), null, null),
                new TestCase("Delete Project non-existent id", Long.MAX_VALUE, Optional.empty(), null, null),
                new TestCase("Delete Project null", null, null, IllegalArgumentException.class, null),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> update() {
        record TestCase(String name, Project project, Optional<Project> expected, Class<? extends Exception> exception,
                        String message) {
            public void check() {
                try {
                    Optional<Project> computed = repo.update(TestCase.this.project);
                    Assertions.assertNull(TestCase.this.exception);

                    Assertions.assertEquals(TestCase.this.expected, computed);

                    if (computed.isEmpty()) {  // if successfully updated
                        Optional<Project> updatedProject = repo.find(TestCase.this.project.getId());
                        Assertions.assertTrue(updatedProject.isPresent());
                        Assertions.assertEquals(TestCase.this.project, updatedProject.get());
                    }
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    Assertions.assertEquals(TestCase.this.exception, e.getClass());
                    Assertions.assertEquals(TestCase.this.message, e.getMessage());
                }
            }
        }

        Project projectExisting = Constants.defaultProjects[0].clone();
        projectExisting.setTitle("apache");

        final Project projectNotExisting = new Project(Long.MAX_VALUE, "abc", "abc", LocalDateTime.now());

        Project invalidProject = Constants.defaultProjects[0].clone();
        invalidProject.setTitle("");

        var testCases = new TestCase[]{
                new TestCase("Update User successfully", projectExisting, Optional.empty(), null, null),
                new TestCase("Update User non-existing", projectNotExisting, Optional.of(projectNotExisting), null, null),
                new TestCase("Update User null", null, null, IllegalArgumentException.class, null),
                new TestCase("Update User invalid", invalidProject, null, ValidationException.class, ErrorMessages.PROJECT_INVALID_TITLE),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> find() {
        record TestCase(String name, Long id, Optional<Project> expected, Class<? extends Exception> exception) {
            public void check() {
                try {
                    Optional<Project> computed = repo.find(TestCase.this.id);
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
                new TestCase("Find Project successfully", Constants.defaultProjects[0].getId(), Optional.of(Constants.defaultProjects[0]), null),
                new TestCase("Find Project id not found", Long.MAX_VALUE, Optional.empty(), null),
                new TestCase("Find Project null id", null, null, IllegalArgumentException.class),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @Test
    void findAll() {
        var projectsIterable = repo.findAll();
        List<Project> projects = StreamSupport.stream(projectsIterable.spliterator(), false).collect(Collectors.toList());
        Assertions.assertEquals(Constants.defaultProjects.length, projects.size());
        Stream.of(Constants.defaultProjects).forEach(project -> Assertions.assertTrue(projects.contains(project)));
    }
}