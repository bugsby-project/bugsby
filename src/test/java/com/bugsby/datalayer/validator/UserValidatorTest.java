package com.bugsby.datalayer.validator;

import com.bugsby.datalayer.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

class UserValidatorTest {

    private static final UserValidator validator = new UserValidator();

    @TestFactory
    Stream<DynamicTest> validate() {
        record TestCase(String name, User user, boolean wantErr, String errorMessage) {
            public void check() {
                try {
                    validator.validate(TestCase.this.user);
                    if (TestCase.this.wantErr) {
                        Assertions.fail();
                    }
                } catch (ValidationException e) {
                    if (!TestCase.this.wantErr) {
                        Assertions.fail();
                    }
                    Assertions.assertEquals(TestCase.this.errorMessage, e.getMessage());
                }
            }
        }

        var testCases = new TestCase[]{
                new TestCase("Valid user", new User("u", "p", "f_n", "l_n", "a@gmail.com"), false, ""),
                new TestCase("Invalid email1", new User("u", "p", "f_n", "l_n", ""), true, ErrorMessages.USER_INVALID_EMAIL),
                new TestCase("Invalid email2", new User("u", "p", "f_n", "l_n", "a"), true, ErrorMessages.USER_INVALID_EMAIL),
                new TestCase("Invalid email3", new User("u", "p", "f_n", "l_n", "@gmail.com"), true, ErrorMessages.USER_INVALID_EMAIL),
                new TestCase("Invalid email4", new User("u", "p", "f_n", "l_n", "-s/@////.c"), true, ErrorMessages.USER_INVALID_EMAIL),
                new TestCase("Invalid username", new User("", "p", "f_n", "l_n", "a@gmail.com"), true, ErrorMessages.USER_INVALID_USERNAME),
                new TestCase("Invalid password and names", new User("u", "", "", "", "a@gmail.com"), true, ErrorMessages.USER_INVALID_PASSWORD + ErrorMessages.USER_INVALID_FIRST_NAME + ErrorMessages.USER_INVALID_LAST_NAME),
        };

        return DynamicTest.stream(Stream.of(testCases), TestCase::name, TestCase::check);
    }
}