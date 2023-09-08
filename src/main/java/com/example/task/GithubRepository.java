package com.example.task;

import java.util.ArrayList;

public record GithubRepository(String repositoryName, String ownerLogin, ArrayList<BranchInfo> branches) {
}
