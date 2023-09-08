package com.example.task;

public sealed interface Message permits ErrorMessage, RepositoryMessage {
}
