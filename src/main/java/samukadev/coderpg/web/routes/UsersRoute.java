package samukadev.coderpg.web.routes;

public interface UsersRoute extends BaseRoute {
    String ROOT = BASE_API + "/users";
    String ME =  ROOT + "/me";
    String BY_ID = ROOT + "/{id}";
    String ONBOARDING = ROOT + "/onboarding";
    String BUILD = ROOT + "/build";
    String STATS = ROOT + "/stats";

    String USER_FOLLOWERS = ROOT + "/{userId}/followers";
    String USER_FOLLOWING = ROOT + "/{userId}/following";
    String FOLLOW_RELATIONSHIP = ROOT + "/{userId}/followers/{followerId}";
}
