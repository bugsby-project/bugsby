package com.bugsby.datalayer.repository.hibernate;

import com.bugsby.datalayer.model.Involvement;
import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.IssueType;
import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.model.Role;
import com.bugsby.datalayer.model.Severity;
import com.bugsby.datalayer.model.Status;
import com.bugsby.datalayer.model.User;

import java.time.LocalDateTime;

public class Constants {
    public static final User[] defaultUsers = new User[]{
            new User(1L, "anne", "anne_p", "Anne", "Victoria", "anne@gmail.com"),
            new User(2L, "john", "johnOl", "John", "Smith", "john_smith@yahoo.com"),
            new User(3L, "ion", "pop", "Ion", "Pop", "ion_pop@yahoo.com"),
    };

    public static final Project[] defaultProjects = new Project[]{
            new Project(1L, "architecture_map", "Architecture", LocalDateTime.now()),
            new Project(2L, "google", "Browser", LocalDateTime.now()),
            new Project(3L, "Bugzilla", "Issue tracker", LocalDateTime.now()),
    };

    public static final Involvement[] defaultInvolvements = new Involvement[]{
            new Involvement(1L, Role.TESTER, defaultUsers[0], defaultProjects[0]),
            new Involvement(2L, Role.PRODUCT_OWNER, defaultUsers[1], defaultProjects[2]),
            new Involvement(3L, Role.QA_LEAD, defaultUsers[0], defaultProjects[2])
    };

    public static final Issue[] defaultIssues = new Issue[]{
            new Issue("Title1", "Desc1", Severity.MINOR, Status.IN_PROGRESS, IssueType.QUESTION, defaultProjects[0], defaultUsers[0]),
            new Issue("Title2", "Desc2", Severity.BLOCKER, Status.TO_DO, IssueType.WONT_FIX, defaultProjects[1], defaultUsers[0], defaultUsers[0])
    };

    public static final String DEFAULT_PROPERTIES_FILE = "application.properties";
}
