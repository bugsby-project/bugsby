package com.bugsby.datalayer.service;

import com.bugsby.datalayer.ai.Predictor;
import com.bugsby.datalayer.exceptions.AiServiceException;
import com.bugsby.datalayer.exceptions.EmailTakenException;
import com.bugsby.datalayer.exceptions.IssueNotFoundException;
import com.bugsby.datalayer.exceptions.ProjectNotFoundException;
import com.bugsby.datalayer.exceptions.UserAlreadyInProjectException;
import com.bugsby.datalayer.exceptions.UserNotFoundException;
import com.bugsby.datalayer.exceptions.UserNotInProjectException;
import com.bugsby.datalayer.exceptions.UsernameTakenException;
import com.bugsby.datalayer.model.Involvement;
import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.IssueType;
import com.bugsby.datalayer.model.ProfanityLevel;
import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.model.Role;
import com.bugsby.datalayer.model.Severity;
import com.bugsby.datalayer.model.SeverityLevel;
import com.bugsby.datalayer.model.Status;
import com.bugsby.datalayer.model.User;
import com.bugsby.datalayer.repository.InvolvementRepository;
import com.bugsby.datalayer.repository.IssueRepository;
import com.bugsby.datalayer.repository.ProjectRepository;
import com.bugsby.datalayer.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
class MasterServiceTest {

    @InjectMocks
    private MasterService service;

    @Mock
    private Predictor predictor;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InvolvementRepository involvementRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private IssueRepository issueRepository;

    private static final User USER = new User("a", "b", "AB", "BA", "a@g.com");
    private static final Project PROJECT = new Project(1L, "title", "description", LocalDateTime.now());
    private static final Project PROJECT_2 = new Project(2L, "title", "description", LocalDateTime.now());
    private static final Involvement INVOLVEMENT = new Involvement(Role.UX_DESIGNER, USER, PROJECT);
    private static final Issue ISSUE = new Issue("Title", "Desc", Severity.BLOCKER, Status.TO_DO, IssueType.DUPLICATE, INVOLVEMENT.getProject(), INVOLVEMENT.getUser());

    @BeforeEach
    void setUp() {
        USER.setId(Math.abs(new Random().nextLong()));
        USER.setInvolvements(Set.of(INVOLVEMENT));
        PROJECT.setInvolvements(Set.of(INVOLVEMENT));
        ISSUE.setId(Math.abs(new Random().nextLong()));
    }

    @TestFactory
    Stream<DynamicTest> createAccount() {
        record TestCase(String name, Runnable initialiseMocks, Service service, User user,
                        Class<? extends Exception> exception) {
            public void check() {
                TestCase.this.initialiseMocks.run();
                try {
                    TestCase.this.service.createAccount(TestCase.this.user);
                    Assertions.assertNull(TestCase.this.exception);
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    assertEquals(TestCase.this.exception, e.getClass());
                }
            }
        }

        Runnable initMocksCreateAccountSuccessful = () -> {
        };
        Runnable initMocksCreateAccountDuplicateUsername = () -> Mockito.when(userRepository.findUserByUsername(USER.getUsername()))
                .thenReturn(Optional.of(USER));
        Runnable initMocksCreateAccountDuplicateEmail = () -> {
            Mockito.when(userRepository.findUserByUsername(ArgumentMatchers.any(String.class)))
                    .thenReturn(Optional.empty());
            Mockito.when(userRepository.findUserByEmail(USER.getEmail()))
                    .thenReturn(Optional.of(USER));
        };

        var testCases = new TestCase[]{
                new TestCase("Create account successful", initMocksCreateAccountSuccessful, service, USER, null),
                new TestCase("Create account duplicate username", initMocksCreateAccountDuplicateUsername, service, USER, UsernameTakenException.class),
                new TestCase("Create account duplicate email", initMocksCreateAccountDuplicateEmail, service, USER, EmailTakenException.class),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> login() {
        record TestCase(String name, Runnable initialiseMocks, Service service, String username, User expected,
                        Class<? extends Exception> exception) {
            public void check() {
                TestCase.this.initialiseMocks.run();
                try {
                    User computed = TestCase.this.service.login(TestCase.this.username);
                    Assertions.assertNull(TestCase.this.exception);
                    assertEquals(TestCase.this.expected, computed);
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    assertEquals(TestCase.this.exception, e.getClass());
                }
            }
        }

        Runnable initMocksLoginInvalidData = () -> Mockito.when(userRepository.findUserByUsername(null))
                .thenThrow(IllegalArgumentException.class);
        Runnable initMocksLoginSuccessful = () -> Mockito.when(userRepository.findUserByUsername(USER.getUsername()))
                .thenReturn(Optional.of(USER));
        Runnable initMocksLoginUnsuccessful = () -> Mockito.when(userRepository.findUserByUsername(USER.getUsername()))
                .thenReturn(Optional.empty());

        var testCases = new TestCase[]{
                new TestCase("Login invalid data", initMocksLoginInvalidData, service, null, null, IllegalArgumentException.class),
                new TestCase("Login successful", initMocksLoginSuccessful, service, USER.getUsername(), USER, null),
                new TestCase("Login unsuccessful", initMocksLoginUnsuccessful, service, USER.getUsername(), null, UserNotFoundException.class)
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> createProject() {
        record TestCase(String name, Runnable initialiseMocks, Service service, Project project, Project expected,
                        Class<? extends Exception> exception) {
            public void check() {
                TestCase.this.initialiseMocks.run();
                try {
                    Project computed = TestCase.this.service.createProject(TestCase.this.project);
                    Assertions.assertNull(TestCase.this.exception);
                    assertEquals(TestCase.this.expected, computed);
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    assertEquals(TestCase.this.exception, e.getClass());
                }
            }
        }

        Runnable initMocksCreateProjectSuccessfully = () -> Mockito.when(projectRepository.save(PROJECT))
                .thenReturn(Optional.empty());

        var testCases = new TestCase[]{
                new TestCase("Create project successfully", initMocksCreateProjectSuccessfully, service, PROJECT, PROJECT, null)
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> getProjectById() {
        record TestCase(String name, Runnable initialiseMocks, Service service, long id, Project expected,
                        Class<? extends Exception> exception) {
            public void check() {
                TestCase.this.initialiseMocks.run();
                try {
                    Project computed = TestCase.this.service.getProjectById(TestCase.this.id);
                    Assertions.assertNull(TestCase.this.exception);
                    assertEquals(TestCase.this.expected, computed);
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    assertEquals(TestCase.this.exception, e.getClass());
                }
            }
        }

        Runnable initMocksGetProjectNonExistentId = () -> Mockito.when(projectRepository.find(ArgumentMatchers.any(Long.class)))
                .thenReturn(Optional.empty());
        Runnable initMocksGetProjectSuccessfully = () -> Mockito.when(projectRepository.find(PROJECT.getId()))
                .thenReturn(Optional.of(PROJECT));

        var testCases = new TestCase[]{
                new TestCase("Get project non-existent id", initMocksGetProjectNonExistentId, service, 0L, null, null),
                new TestCase("Get project successfully", initMocksGetProjectSuccessfully, service, PROJECT.getId(), PROJECT, null)
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> getInvolvementsByUserId() {
        record TestCase(String name, Runnable initialiseMocks, Service service, String username,
                        Set<Involvement> expected,
                        Class<? extends Exception> exception) {
            public void check() {
                TestCase.this.initialiseMocks.run();
                try {
                    Set<Involvement> computed = TestCase.this.service.getInvolvementsByUsername(username);
                    Assertions.assertNull(TestCase.this.exception);
                    assertEquals(TestCase.this.expected, computed);
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    assertEquals(e.getClass(), TestCase.this.exception);
                }
            }
        }

        Runnable initMocksGetInvolvementsWrongUser = () -> Mockito.when(userRepository.findUserByUsername(ArgumentMatchers.any(String.class)))
                .thenReturn(Optional.empty());
        Runnable initMocksGetInvolvementsEmpty = () -> {
            USER.setInvolvements(Collections.emptySet());
            Mockito.when(userRepository.findUserByUsername(ArgumentMatchers.any(String.class)))
                    .thenReturn(Optional.of(USER));
        };
        Runnable initMocksGetInvolvements = () -> {
            USER.setInvolvements(Set.of(INVOLVEMENT));
            Mockito.when(userRepository.findUserByUsername(ArgumentMatchers.any(String.class)))
                    .thenReturn(Optional.of(USER));
        };

        var testCases = new TestCase[]{
                new TestCase("Get Involvements by User username non-existent", initMocksGetInvolvementsWrongUser, service, "", null, UserNotFoundException.class),
                new TestCase("Get Involvements by User zero size", initMocksGetInvolvementsEmpty, service, USER.getUsername(), Collections.emptySet(), null),
                new TestCase("Get Involvements", initMocksGetInvolvements, service, USER.getUsername(), USER.getInvolvements(), null)
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> addParticipant() {
        record TestCase(String name, Runnable initialiseMocks, Service service, Involvement involvement, User requester,
                        Involvement expected,
                        Class<? extends Exception> exception) {
            public void check() {
                TestCase.this.initialiseMocks.run();
                try {
                    Involvement result = TestCase.this.service.addParticipant(TestCase.this.involvement, TestCase.this.requester);
                    Assertions.assertNull(TestCase.this.exception);
                    assertEquals(TestCase.this.expected, result);
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    assertEquals(TestCase.this.exception, e.getClass());
                }
            }
        }

        Involvement involvementToAdd = new Involvement(Role.UX_DESIGNER, USER, PROJECT);
        User userNonExistent = new User(Long.MAX_VALUE);

        Runnable initMocksAddParticipantNonExistentRequester = () -> Mockito.when(userRepository.find(ArgumentMatchers.any(Long.class)))
                .thenReturn(Optional.empty());

        Runnable initMocksAddParticipantWrongRequester = () -> {
            Mockito.when(userRepository.find(ArgumentMatchers.any(Long.class)))
                    .thenReturn(Optional.of(USER));
            Mockito.when(projectRepository.find(ArgumentMatchers.any(Long.class)))
                    .thenReturn(Optional.of(PROJECT));
        };

        Runnable initMocksAddParticipantNonExistentUser = () -> Mockito.when(userRepository.find(userNonExistent.getId()))
                .thenReturn(Optional.empty());

        Runnable initMocksAddParticipantAlreadyAdded = () -> {
            Mockito.when(userRepository.find(ArgumentMatchers.any(Long.class)))
                    .thenReturn(Optional.of(USER));
            Mockito.when(userRepository.findUserByUsername(ArgumentMatchers.any(String.class)))
                    .thenReturn(Optional.of(USER));
            Mockito.when(projectRepository.find(ArgumentMatchers.any(Long.class)))
                    .thenReturn(Optional.of(PROJECT));
        };

        var testCases = new TestCase[]{
                new TestCase("Add Participant non existent requester",
                        initMocksAddParticipantNonExistentRequester,
                        service,
                        involvementToAdd,
                        userNonExistent,
                        null,
                        UserNotFoundException.class
                ),
                new TestCase("Add Participant requester not a participant",
                        initMocksAddParticipantWrongRequester,
                        service,
                        new Involvement(Role.BACK_END_DEVELOPER, USER, PROJECT_2),
                        USER,
                        null,
                        UserNotInProjectException.class
                ),
                new TestCase("Add participant non existent user",
                        initMocksAddParticipantNonExistentUser,
                        service,
                        new Involvement(Role.TESTER, userNonExistent, PROJECT),
                        userNonExistent,
                        null,
                        UserNotFoundException.class
                ),
                new TestCase("Add participant already added",
                        initMocksAddParticipantAlreadyAdded,
                        service,
                        new Involvement(Role.FULL_STACK_DEVELOPER, USER, PROJECT),
                        USER,
                        null,
                        UserAlreadyInProjectException.class
                ),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> getAllUsernames() {
        record TestCase(String name, Runnable initialiseMocks, Service service, List<String> expected) {
            public void check() {
                TestCase.this.initialiseMocks.run();
                List<String> computed = TestCase.this.service.getAllUsernames();
                assertEquals(TestCase.this.expected.size(), computed.size());
                Assertions.assertTrue(TestCase.this.expected.containsAll(computed));
                Assertions.assertTrue(computed.containsAll(TestCase.this.expected));
            }
        }

        Runnable initMocksGetAllUsernamesEmpty = () -> Mockito.when(userRepository.getAllUsernames())
                .thenReturn(Collections.emptySet());
        Runnable initMocksGetAllUsernames = () -> Mockito.when(userRepository.getAllUsernames())
                .thenReturn(Set.of(USER.getUsername()));

        var testCases = new TestCase[]{
                new TestCase("Get all usernames empty result", initMocksGetAllUsernamesEmpty, service, Collections.emptyList()),
                new TestCase("Get all username default users", initMocksGetAllUsernames, service, List.of(USER.getUsername()))
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> addIssue() {
        record TestCase(String name, Runnable initialiseMocks, Service service, Issue issue, Issue expected,
                        Class<? extends Exception> exception) {
            public void check() {
                TestCase.this.initialiseMocks.run();
                try {
                    Issue computed = TestCase.this.service.addIssue(TestCase.this.issue);
                    Assertions.assertNull(TestCase.this.exception);
                    assertEquals(TestCase.this.expected, computed);
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    assertEquals(e.getClass(), TestCase.this.exception);
                }
            }
        }

        Runnable initMocksNonExistentReporter = () -> {
            try {
                Mockito.when(predictor.predictProfanityLevel(ArgumentMatchers.any(String.class)))
                        .thenReturn(ProfanityLevel.NOT_OFFENSIVE);
            } catch (AiServiceException e) {
                e.printStackTrace();
            }
            Mockito.when(userRepository.find(ArgumentMatchers.any(Long.class)))
                    .thenReturn(Optional.empty());
        };

        Runnable initMocksAddIssueSuccessfully = () -> {
            try {
                Mockito.when(predictor.predictProfanityLevel(ArgumentMatchers.any(String.class)))
                        .thenReturn(ProfanityLevel.NOT_OFFENSIVE);
            } catch (AiServiceException e) {
                e.printStackTrace();
            }
            Mockito.when(userRepository.find(ArgumentMatchers.any(Long.class)))
                    .thenReturn(Optional.of(USER));
            Mockito.when(issueRepository.save(ArgumentMatchers.any(Issue.class)))
                    .thenReturn(Optional.empty());
        };

        Runnable initMocksOffensiveContent = () -> {
            try {
                Mockito.when(predictor.predictProfanityLevel(ArgumentMatchers.any(String.class)))
                        .thenReturn(ProfanityLevel.OFFENSIVE);
            } catch (AiServiceException e) {
                e.printStackTrace();
            }
        };

        Runnable initMocksNonExistentAssignee = () -> {
            try {
                Mockito.when(predictor.predictProfanityLevel(ArgumentMatchers.any(String.class)))
                        .thenReturn(ProfanityLevel.NOT_OFFENSIVE);
            } catch (AiServiceException e) {
                e.printStackTrace();
            }
            ISSUE.setAssignee(new User(Long.MAX_VALUE));
            Mockito.when(userRepository.find(ArgumentMatchers.any(Long.class)))
                    .thenReturn(Optional.empty());
        };

        Runnable initMocksAssigneeNotInProject = () -> {
            try {
                Mockito.when(predictor.predictProfanityLevel(ArgumentMatchers.any(String.class)))
                        .thenReturn(ProfanityLevel.NOT_OFFENSIVE);
            } catch (AiServiceException e) {
                e.printStackTrace();
            }
            Mockito.when(userRepository.find(ArgumentMatchers.any(Long.class)))
                    .thenReturn(Optional.of(USER));
            ISSUE.setAssignee(USER);
            USER.setInvolvements(Collections.emptySet());
        };

        var testCases = new TestCase[]{
                new TestCase("Save issue non existent reporter", initMocksNonExistentReporter, service, ISSUE, null, UserNotFoundException.class),
                new TestCase("Save issue successfully", initMocksAddIssueSuccessfully, service, ISSUE, ISSUE, null),
                new TestCase("Save issues offensive content", initMocksOffensiveContent, service, ISSUE, null, IllegalArgumentException.class),
                new TestCase("Save issue non-existent assignee", initMocksNonExistentAssignee, service, ISSUE, null, UserNotFoundException.class),
                new TestCase("Save issue assignee not in project", initMocksAssigneeNotInProject, service, ISSUE, null, UserNotInProjectException.class),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> getAssignedIssues() {
        record TestCase(String name, Runnable initialiseMocks, Service service, String username, List<Issue> expected,
                        Class<? extends Exception> exception) {
            public void check() {
                TestCase.this.initialiseMocks.run();
                try {
                    List<Issue> computed = TestCase.this.service.getAssignedIssues(TestCase.this.username);
                    Assertions.assertNull(TestCase.this.exception);

                    assertEquals(TestCase.this.expected, computed);
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    assertEquals(TestCase.this.exception, e.getClass());
                }
            }
        }

        Runnable initMocksNonExistentUser = () -> Mockito.when(userRepository.findUserByUsername(ArgumentMatchers.any(String.class)))
                .thenReturn(Optional.empty());
        Runnable initMocksDefault = () -> Mockito.when(userRepository.findUserByUsername(ArgumentMatchers.any(String.class)))
                .thenReturn(Optional.of(USER));

        var testCases = new TestCase[]{
                new TestCase("Service Get Assigned Issues non existent user", initMocksNonExistentUser, service, USER.getUsername(), null, UserNotFoundException.class),
                new TestCase("Service Get Assigned Issues not empty", initMocksDefault, service, USER.getUsername(), USER.getAssignedIssues().stream().toList(), null)
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> getIssueById() {
        record TestCase(String name, Runnable initialiseMocks, Service service, long id, Issue expected) {
            public void check() {
                TestCase.this.initialiseMocks.run();
                Issue computed = TestCase.this.service.getIssueById(TestCase.this.id);
                assertEquals(TestCase.this.expected, computed);
            }
        }

        Runnable initMocksIdNonExistent = () -> Mockito.when(issueRepository.find(ArgumentMatchers.any(Long.class)))
                .thenReturn(Optional.empty());
        Runnable initMocksSuccessfully = () -> Mockito.when(issueRepository.find(ArgumentMatchers.any(Long.class)))
                .thenReturn(Optional.of(ISSUE));

        var testCases = new TestCase[]{
                new TestCase("Get Issue By Id non existent", initMocksIdNonExistent, service, Long.MAX_VALUE, null),
                new TestCase("Get Issue By Id successfully", initMocksSuccessfully, service, ISSUE.getId(), ISSUE)
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> deleteIssue() {
        record TestCase(String name, Runnable initialiseMocks, Service service, long id, String requester,
                        Issue expected,
                        Class<? extends Exception> exception) {
            public void check() {
                try {
                    TestCase.this.initialiseMocks.run();
                    Issue computed = TestCase.this.service.deleteIssue(TestCase.this.id, TestCase.this.requester);
                    Assertions.assertNull(TestCase.this.exception);
                    assertEquals(expected, computed);
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    assertEquals(TestCase.this.exception, e.getClass());
                }
            }
        }

        Runnable initMocksIssueDoesNotExist = () -> Mockito.when(issueRepository.find(ArgumentMatchers.any(Long.class)))
                .thenReturn(Optional.empty());
        Runnable initMocksRequesterNotParticipant = () -> Mockito.when(issueRepository.find(ArgumentMatchers.any(Long.class)))
                .thenReturn(Optional.of(ISSUE));
        Runnable initMocksSuccessfully = () -> {
            Mockito.when(issueRepository.find(ArgumentMatchers.any(Long.class)))
                    .thenReturn(Optional.of(ISSUE));
            Mockito.when(issueRepository.delete(ArgumentMatchers.any(Long.class)))
                    .thenReturn(Optional.of(ISSUE));
        };

        var testCases = new TestCase[]{
                new TestCase("Delete issue does not exist", initMocksIssueDoesNotExist, service, Long.MAX_VALUE, USER.getUsername(), null, IssueNotFoundException.class),
                new TestCase("Delete issue requester not a participant", initMocksRequesterNotParticipant, service, ISSUE.getId(), "", null, UserNotInProjectException.class),
                new TestCase("Delete issue successfully", initMocksSuccessfully, service, ISSUE.getId(), ISSUE.getReporter().getUsername(), ISSUE, null)
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @TestFactory
    Stream<DynamicTest> updateIssue() {
        record TestCase(String name, Runnable initialiseMocks, Service service, Issue issue, String requesterUsername,
                        Issue expected,
                        Class<? extends Exception> exception) {
            public void check() {
                TestCase.this.initialiseMocks.run();
                try {
                    Issue computed = TestCase.this.service.updateIssue(TestCase.this.issue, TestCase.this.requesterUsername);
                    Assertions.assertNull(TestCase.this.exception);
                    assertEquals(expected, computed);
                } catch (Exception e) {
                    Assertions.assertNotNull(TestCase.this.exception);
                    assertEquals(TestCase.this.exception, e.getClass());
                }
            }
        }

        Runnable noMocksNeeded = () -> {
        };
        Runnable initMocksIssueNotFound = () -> Mockito.when(issueRepository.find(ArgumentMatchers.any(Long.class)))
                .thenReturn(Optional.empty());
        Runnable initMocksUserNotInProject = () -> {
            Mockito.when(issueRepository.find(ArgumentMatchers.any(Long.class)))
                    .thenReturn(Optional.of(ISSUE));
        };
        Runnable initMocksSuccessfully = () -> {
            Mockito.when(issueRepository.find(ArgumentMatchers.any(Long.class)))
                    .thenReturn(Optional.of(ISSUE));
            Mockito.when(issueRepository.update(ArgumentMatchers.any(Issue.class)))
                    .thenReturn(Optional.empty());
        };

        var testCases = new TestCase[]{
                new TestCase("Update Issue null issue and user", noMocksNeeded, service, null, null, null, IllegalArgumentException.class),
                new TestCase("Update Issue null user", noMocksNeeded, service, ISSUE, null, null, IllegalArgumentException.class),
                new TestCase("Update Issue issue not found", initMocksIssueNotFound, service, ISSUE, "", null, IssueNotFoundException.class),
                new TestCase("Update Issue user not in project", initMocksUserNotInProject, service, ISSUE, "", null, UserNotInProjectException.class),
                new TestCase("Update Issue successful", initMocksSuccessfully, service, ISSUE, ISSUE.getAssignee().getUsername(), ISSUE, null)
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }

    @Test
    void predictSeverityLevel() {
        try {
            Mockito.when(predictor.predictSeverityLevel(ArgumentMatchers.any(String.class)))
                    .thenReturn(SeverityLevel.SEVERE);
            assertEquals(SeverityLevel.SEVERE, service.predictSeverityLevel("The database is lost"));
        } catch (AiServiceException e) {
            fail();
        }
    }

    @Test
    void predictSeverityLevel_throwsError() {
        try {
            Mockito.when(predictor.predictSeverityLevel(ArgumentMatchers.any(String.class)))
                    .thenThrow(AiServiceException.class);
            assertThrows(AiServiceException.class, () -> service.predictSeverityLevel(""));
        } catch (AiServiceException e) {
            assertTrue(true);
        }
    }

    @Test
    void predictIssueType() {
        try {
            Mockito.when(predictor.predictIssueType(ArgumentMatchers.any(String.class)))
                    .thenReturn(IssueType.BUG);
            assertEquals(IssueType.BUG, service.predictIssueType("this doesn't work"));
        } catch (AiServiceException e) {
            fail();
        }
    }

    @Test
    void predictIssueType_throwsError() {
        try {
            Mockito.when(predictor.predictIssueType(ArgumentMatchers.any(String.class)))
                    .thenThrow(AiServiceException.class);
            assertThrows(AiServiceException.class, () -> service.predictIssueType("this doesn't work"));
        } catch (AiServiceException e) {
            assertTrue(true);
        }
    }

    @Test
    void retrieveDuplicateIssues() {
        Mockito.when(projectRepository.find(ArgumentMatchers.any(Long.class)))
                .thenReturn(Optional.of(PROJECT));
        try {
            Mockito.when(predictor.detectDuplicateIssues(ArgumentMatchers.any(List.class), ArgumentMatchers.any(Issue.class)))
                    .thenReturn(List.of(ISSUE));
            assertEquals(List.of(ISSUE), service.retrieveDuplicateIssues(ISSUE));
        } catch (AiServiceException | ProjectNotFoundException e) {
            fail();
        }
    }

    @Test
    void retrieveDuplicateIssues_projectDoesNotExist() {
        Mockito.when(projectRepository.find(ArgumentMatchers.any(Long.class)))
                .thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> service.retrieveDuplicateIssues(ISSUE));
    }
}