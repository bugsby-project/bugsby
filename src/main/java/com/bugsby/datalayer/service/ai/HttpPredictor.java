package com.bugsby.datalayer.service.ai;

import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.IssueType;
import com.bugsby.datalayer.model.ProfanityLevel;
import com.bugsby.datalayer.model.Severity;
import com.bugsby.datalayer.model.SeverityLevel;
import com.bugsby.datalayer.model.Status;
import com.bugsby.datalayer.service.exceptions.AiServiceException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

@Component
public class HttpPredictor implements Predictor {
    private final String url;
    private static final String SUGGESTED_SEVERITY = "/suggested-severity?title=:title";
    private static final String SUGGESTED_TYPE = "/suggested-type?title=:title";
    private static final String IS_OFFENSIVE = "/is-offensive?text=:text";
    private static final String DUPLICATE_ISSUES = "/duplicate-issues";
    private static final double OFFENSIVE_THRESHOLD = 0.8;

    // todo
    public HttpPredictor(@Value("${ai.url}") String url) {
        this.url = url;
    }

    @Override
    public SeverityLevel predictSeverityLevel(String title) throws AiServiceException {
        String titleEncoded = URLEncoder.encode(title, StandardCharsets.UTF_8);
        String urlString = url + SUGGESTED_SEVERITY;
        urlString = urlString.replaceAll(":title", titleEncoded);
        String response = doHttpCall(urlString);
        return SeverityLevel.valueOf(response.toUpperCase());
    }

    @Override
    public IssueType predictIssueType(String title) throws AiServiceException {
        String titleEncoded = URLEncoder.encode(title, StandardCharsets.UTF_8);
        String urlString = url + SUGGESTED_TYPE;
        urlString = urlString.replaceAll(":title", titleEncoded);
        String response = doHttpCall(urlString);
        return IssueType.valueOf(response);
    }

    @Override
    public ProfanityLevel predictProfanityLevel(String text) throws AiServiceException {
        String textEncoded = URLEncoder.encode(text, StandardCharsets.UTF_8);
        String urlString = url + IS_OFFENSIVE;
        urlString = urlString.replaceAll(":text", textEncoded);
        double probability = Double.parseDouble(doHttpCall(urlString));
        if (probability < OFFENSIVE_THRESHOLD) {
            return ProfanityLevel.NOT_OFFENSIVE;
        }
        return ProfanityLevel.OFFENSIVE;
    }

    @Override
    public List<Issue> detectDuplicateIssues(List<Issue> projectIssues, Issue issue) throws AiServiceException {
        String urlString = url + DUPLICATE_ISSUES;
        DuplicateIssuesRequest requestData = new DuplicateIssuesRequest(projectIssues.stream()
                .map(IssueDto::from)
                .toList(),
                IssueDto.from(issue));

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String data = objectMapper.writeValueAsString(requestData);
            connection.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));

            try (InputStream inputStream = connection.getInputStream()) {
                List<IssueDto> result = objectMapper.readValue(inputStream, new TypeReference<>() {
                });
                connection.disconnect();

                return result.stream()
                        .map(IssueDto::id)
                        .map(identifier -> projectIssues.stream()
                                .filter(x -> x.getId().equals(identifier))
                                .findFirst()
                                .orElse(null))
                        .toList();
            }

        } catch (IOException e) {
            throw new AiServiceException(e.getMessage());
        }
    }

    private String doHttpCall(String urlString) throws AiServiceException {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = in.readLine();
            in.close();

            connection.disconnect();

            return response;
        } catch (IOException e) {
            throw new AiServiceException(e.getMessage());
        }
    }

    private static record DuplicateIssuesRequest(List<IssueDto> projectIssues, IssueDto issue) {
    }

    private static record IssueDto(Long id, String title, String description, Severity severity, Status status,
                                   IssueType type) {
        public static IssueDto from(Issue issue) {
            return new IssueDto(issue.getId(), issue.getTitle(), issue.getDescription(), issue.getSeverity(), issue.getStatus(), issue.getType());
        }
    }
}
