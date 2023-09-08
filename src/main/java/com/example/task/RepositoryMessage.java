package com.example.task;

import java.util.ArrayList;

public record RepositoryMessage(ArrayList<GithubRepository> repositories) implements Message {
}
