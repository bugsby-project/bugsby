package service;

import ai.Predictor;
import exceptions.AiServiceException;
import exceptions.EmailTakenException;
import exceptions.IssueNotFoundException;
import exceptions.ProjectNotFoundException;
import exceptions.UserAlreadyInProjectException;
import exceptions.UserNotFoundException;
import exceptions.UserNotInProjectException;
import exceptions.UsernameTakenException;
import model.Involvement;
import model.Issue;
import model.IssueType;
import model.ProfanityLevel;
import model.Project;
import model.Role;
import model.Severity;
import model.SeverityLevel;
import model.Status;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.InvolvementRepository;
import repository.IssueRepository;
import repository.ProjectRepository;
import repository.UserRepository;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
        Runnable initMocksCreateAccountDuplicateUsername = () -> when(userRepository.findUserByUsername(USER.getUsername()))
                .thenReturn(Optional.of(USER));
        Runnable initMocksCreateAccountDuplicateEmail = () -> {
            when(userRepository.findUserByUsername(any(String.class)))
                    .thenReturn(Optional.empty());
            when(userRepository.findUserByEmail(USER.getEmail()))
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

        Runnable initMocksLoginInvalidData = () -> when(userRepository.findUserByUsername(null))
                .thenThrow(IllegalArgumentException.class);
        Runnable initMocksLoginSuccessful = () -> when(userRepository.findUserByUsername(USER.getUsername()))
                .thenReturn(Optional.of(USER));
        Runnable initMocksLoginUnsuccessful = () -> when(userRepository.findUserByUsername(USER.getUsername()))
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

        Runnable initMocksCreateProjectSuccessfully = () -> when(projectRepository.save(PROJECT))
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

        Runnable initMocksGetProjectNonExistentId = () -> when(projectRepository.find(any(Long.class)))
                .thenReturn(Optional.empty());
        Runnable initMocksGetProjectSuccessfully = () -> when(projectRepository.find(PROJECT.getId()))
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

        Runnable initMocksGetInvolvementsWrongUser = () -> when(userRepository.findUserByUsername(any(String.class)))
                .thenReturn(Optional.empty());
        Runnable initMocksGetInvolvementsEmpty = () -> {
            USER.setInvolvements(Collections.emptySet());
            when(userRepository.findUserByUsername(any(String.class)))
                    .thenReturn(Optional.of(USER));
        };
        Runnable initMocksGetInvolvements = () -> {
            USER.setInvolvements(Set.of(INVOLVEMENT));
            when(userRepository.findUserByUsername(any(String.class)))
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

        Runnable initMocksAddParticipantNonExistentRequester = () -> when(userRepository.find(any(Long.class)))
                .thenReturn(Optional.empty());

        Runnable initMocksAddParticipantWrongRequester = () -> {
            when(userRepository.find(any(Long.class)))
                    .thenReturn(Optional.of(USER));
            when(projectRepository.find(any(Long.class)))
                    .thenReturn(Optional.of(PROJECT));
        };

        Runnable initMocksAddParticipantNonExistentUser = () -> when(userRepository.find(userNonExistent.getId()))
                .thenReturn(Optional.empty());

        Runnable initMocksAddParticipantAlreadyAdded = () -> {
            when(userRepository.find(any(Long.class)))
                    .thenReturn(Optional.of(USER));
            when(userRepository.findUserByUsername(any(String.class)))
                    .thenReturn(Optional.of(USER));
            when(projectRepository.find(any(Long.class)))
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

        Runnable initMocksGetAllUsernamesEmpty = () -> when(userRepository.getAllUsernames())
                .thenReturn(Collections.emptySet());
        Runnable initMocksGetAllUsernames = () -> when(userRepository.getAllUsernames())
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
                when(predictor.predictProfanityLevel(any(String.class)))
                        .thenReturn(ProfanityLevel.NOT_OFFENSIVE);
            } catch (AiServiceException e) {
                e.printStackTrace();
            }
            when(userRepository.find(any(Long.class)))
                    .thenReturn(Optional.empty());
        };

        Runnable initMocksAddIssueSuccessfully = () -> {
            try {
                when(predictor.predictProfanityLevel(any(String.class)))
                        .thenReturn(ProfanityLevel.NOT_OFFENSIVE);
            } catch (AiServiceException e) {
                e.printStackTrace();
            }
            when(userRepository.find(any(Long.class)))
                    .thenReturn(Optional.of(USER));
            when(issueRepository.save(any(Issue.class)))
                    .thenReturn(Optional.empty());
        };

        Runnable initMocksOffensiveContent = () -> {
            try {
                when(predictor.predictProfanityLevel(any(String.class)))
                        .thenReturn(ProfanityLevel.OFFENSIVE);
            } catch (AiServiceException e) {
                e.printStackTrace();
            }
        };

        Runnable initMocksNonExistentAssignee = () -> {
            try {
                when(predictor.predictProfanityLevel(any(String.class)))
                        .thenReturn(ProfanityLevel.NOT_OFFENSIVE);
            } catch (AiServiceException e) {
                e.printStackTrace();
            }
            ISSUE.setAssignee(new User(Long.MAX_VALUE));
            when(userRepository.find(any(Long.class)))
                    .thenReturn(Optional.empty());
        };

        Runnable initMocksAssigneeNotInProject = () -> {
            try {
                when(predictor.predictProfanityLevel(any(String.class)))
                        .thenReturn(ProfanityLevel.NOT_OFFENSIVE);
            } catch (AiServiceException e) {
                e.printStackTrace();
            }
            when(userRepository.find(any(Long.class)))
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

        Runnable initMocksNonExistentUser = () -> when(userRepository.findUserByUsername(any(String.class)))
                .thenReturn(Optional.empty());
        Runnable initMocksDefault = () -> when(userRepository.findUserByUsername(any(String.class)))
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

        Runnable initMocksIdNonExistent = () -> when(issueRepository.find(any(Long.class)))
                .thenReturn(Optional.empty());
        Runnable initMocksSuccessfully = () -> when(issueRepository.find(any(Long.class)))
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

        Runnable initMocksIssueDoesNotExist = () -> when(issueRepository.find(any(Long.class)))
                .thenReturn(Optional.empty());
        Runnable initMocksRequesterNotParticipant = () -> when(issueRepository.find(any(Long.class)))
                .thenReturn(Optional.of(ISSUE));
        Runnable initMocksSuccessfully = () -> {
            when(issueRepository.find(any(Long.class)))
                    .thenReturn(Optional.of(ISSUE));
            when(issueRepository.delete(any(Long.class)))
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
        Runnable initMocksIssueNotFound = () -> when(issueRepository.find(any(Long.class)))
                .thenReturn(Optional.empty());
        Runnable initMocksUserNotInProject = () -> {
            when(issueRepository.find(any(Long.class)))
                    .thenReturn(Optional.of(ISSUE));
        };
        Runnable initMocksSuccessfully = () -> {
            when(issueRepository.find(any(Long.class)))
                    .thenReturn(Optional.of(ISSUE));
            when(issueRepository.update(any(Issue.class)))
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
            when(predictor.predictSeverityLevel(any(String.class)))
                    .thenReturn(SeverityLevel.SEVERE);
            assertEquals(SeverityLevel.SEVERE, service.predictSeverityLevel("The database is lost"));
        } catch (AiServiceException e) {
            fail();
        }
    }

    @Test
    void predictSeverityLevel_throwsError() {
        try {
            when(predictor.predictSeverityLevel(any(String.class)))
                    .thenThrow(AiServiceException.class);
            assertThrows(AiServiceException.class, () -> service.predictSeverityLevel(""));
        } catch (AiServiceException e) {
            assertTrue(true);
        }
    }

    @Test
    void predictIssueType() {
        try {
            when(predictor.predictIssueType(any(String.class)))
                    .thenReturn(IssueType.BUG);
            assertEquals(IssueType.BUG, service.predictIssueType("this doesn't work"));
        } catch (AiServiceException e) {
            fail();
        }
    }

    @Test
    void predictIssueType_throwsError() {
        try {
            when(predictor.predictIssueType(any(String.class)))
                    .thenThrow(AiServiceException.class);
            assertThrows(AiServiceException.class, () -> service.predictIssueType("this doesn't work"));
        } catch (AiServiceException e) {
            assertTrue(true);
        }
    }

    @Test
    void retrieveDuplicateIssues() {
        when(projectRepository.find(any(Long.class)))
                .thenReturn(Optional.of(PROJECT));
        try {
            when(predictor.detectDuplicateIssues(any(List.class), any(Issue.class)))
                    .thenReturn(List.of(ISSUE));
            assertEquals(List.of(ISSUE), service.retrieveDuplicateIssues(ISSUE));
        } catch (AiServiceException | ProjectNotFoundException e) {
            fail();
        }
    }

    @Test
    void retrieveDuplicateIssues_projectDoesNotExist() {
        when(projectRepository.find(any(Long.class)))
                .thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> service.retrieveDuplicateIssues(ISSUE));
    }

    @Test
    void failingTest() {
        assertEquals(1, 2);
    }
}