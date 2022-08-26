package com.bugsby.datalayer.validator;

import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.IssueType;
import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.model.Severity;
import com.bugsby.datalayer.model.Status;
import com.bugsby.datalayer.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

class IssueValidatorTest {

    private static final Validator<Long, Issue> validator = new IssueValidator();

    @TestFactory
    Stream<DynamicTest> validate() {
        record TestCase(String name, Issue issue, boolean wantErr, String errorMessage) {
            public void check() {
                try {
                    validator.validate(TestCase.this.issue);
                    if (TestCase.this.wantErr) {  // we expected an error
                        Assertions.fail();
                    }
                } catch (ValidationException e) {
                    if (!TestCase.this.wantErr) { // we did not expect an error
                        Assertions.fail();
                    }
                    Assertions.assertEquals(TestCase.this.errorMessage, e.getMessage());
                }
            }
        }

        String allErrors = ErrorMessages.ISSUE_INVALID_TITLE
                + ErrorMessages.ISSUE_INVALID_SEVERITY
                + ErrorMessages.ISSUE_INVALID_STATUS
                + ErrorMessages.ISSUE_INVALID_TYPE
                + ErrorMessages.ISSUE_INVALID_PROJECT
                + ErrorMessages.ISSUE_INVALID_REPORTER;

        var testCases = new TestCase[]{
                new TestCase("Valid issue", new Issue("Title", "", Severity.BLOCKER, Status.DONE, IssueType.BUG, new Project(), new User()), false, null),
                new TestCase("Invalid issue null severity", new Issue("Title", "", null, Status.IN_PROGRESS, IssueType.DOCUMENTATION, new Project(), new User()), true, ErrorMessages.ISSUE_INVALID_SEVERITY),
                new TestCase("Invalid issue", new Issue("", "", null, null, null, null, null), true, allErrors)
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }
}