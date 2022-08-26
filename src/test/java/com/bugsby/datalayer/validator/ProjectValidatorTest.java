package com.bugsby.datalayer.validator;

import com.bugsby.datalayer.model.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.time.LocalDateTime;
import java.util.stream.Stream;

class ProjectValidatorTest {

    private static final ProjectValidator validator = new ProjectValidator();

    @TestFactory
    Stream<DynamicTest> validate() {
        record TestCase(String name, Project project, boolean wantErr, String errorMessage) {
            public void check() {
                try {
                    validator.validate(TestCase.this.project);
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

        var testCases = new TestCase[]{
                new TestCase("Valid project", new Project("t", "desc", LocalDateTime.now()), false, ""),
                new TestCase("Invalid title", new Project("", "desc", LocalDateTime.now()), true, ErrorMessages.PROJECT_INVALID_TITLE),
                new TestCase("Invalid description", new Project("t", "", LocalDateTime.now()), true, ErrorMessages.PROJECT_INVALID_DESCRIPTION),
                new TestCase("Invalid title and description", new Project("", "", LocalDateTime.now()), true, ErrorMessages.PROJECT_INVALID_TITLE + ErrorMessages.PROJECT_INVALID_DESCRIPTION)
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }
}