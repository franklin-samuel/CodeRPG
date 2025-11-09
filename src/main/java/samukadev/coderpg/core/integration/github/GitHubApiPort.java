package samukadev.coderpg.core.integration.github;

import samukadev.coderpg.domain.github.model.GitHubRepository;
import samukadev.coderpg.domain.github.model.GitHubUser;
import samukadev.coderpg.domain.github.model.GitHubCommit;

import java.util.List;

public interface GitHubApiPort {

    GitHubUser getUser(String username);

    List<GitHubRepository> getUserRepositories(String username);

    List<String> getUserEvents(String username, int page, int perPage);

    GitHubRepository getRepository(String owner, String repo);

    List<GitHubCommit> getRepositoryCommits(String owner, String repo, String since);

    boolean isApiAvailable();

    String getRateLimitStatus();
}