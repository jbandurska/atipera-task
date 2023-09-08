package com.example.task;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

@RestController
public class RepositoryMessageController {

    @GetMapping("/repository")
    public ResponseEntity<Message> repositoryMessage(
            @RequestParam(value = "username", defaultValue = "") String username,
            @RequestHeader("Accept") String acceptHeader) {

        if (acceptHeader != null && acceptHeader.equalsIgnoreCase("application/xml")) return notAcceptableResponse();

        try {
            URL url = new URL("https://api.github.com/users/" + username + "/repos");
            return handleConnection(url, acceptHeader);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public ResponseEntity<Message> handleConnection(URL url, String acceptHeader) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", acceptHeader);

            if (connection.getResponseCode() == 404) return notFoundResponse();

            BufferedReader input = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            ResponseEntity<Message> response = okResponse(input.readLine());
            input.close();

            return response;
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public ResponseEntity<Message> notAcceptableResponse() {
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(new ErrorMessage("Application/xml is not an acceptable format."));
    }

    public ResponseEntity<Message> notFoundResponse() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage("User not found."));
    }

    public ResponseEntity<Message> okResponse(String jsonString) {
        ArrayList<GithubRepository> repositories = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                boolean isFork = jsonArray.getJSONObject(i).getBoolean("fork");
                if (!isFork) repositories.add(getRepositoryInfo(jsonArray.getJSONObject(i)));
            }
        } catch (Exception e) {
            throw new Error(e);
        }

        return ResponseEntity.ok(new RepositoryMessage(repositories));
    }

    public GithubRepository getRepositoryInfo(JSONObject repositoryJSON) {
        try {
            String name = repositoryJSON.getString("name");
            String ownerLogin = repositoryJSON.getJSONObject("owner").getString("login");
            String branchesUrlString = repositoryJSON.getString("branches_url");
            ArrayList<BranchInfo> branches = getBranchesInfo(branchesUrlString);

            return new GithubRepository(name, ownerLogin, branches);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public ArrayList<BranchInfo> getBranchesInfo(String branchesUrlString) {
        ArrayList<BranchInfo> branches = new ArrayList<>();

        String cleanBranchesUrlString = branchesUrlString.replace("{/branch}", "");
        try {
            URL branchesUrl = new URL(cleanBranchesUrlString);
            HttpURLConnection connection = (HttpURLConnection) branchesUrl.openConnection();

            BufferedReader input = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            JSONArray branchesJSONArray = new JSONArray(input.readLine());
            input.close();

            for (int i = 0; i < branchesJSONArray.length(); i++) {
                JSONObject branchJSON = branchesJSONArray.getJSONObject(i);
                branches.add(getBranchInfo(branchJSON));
            }

            return branches;
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public BranchInfo getBranchInfo(JSONObject branchJSON) {
        try {
            String name = branchJSON.getString("name");
            String lastCommitSha = branchJSON.getJSONObject("commit").getString("sha");
            return new BranchInfo(name, lastCommitSha);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}

