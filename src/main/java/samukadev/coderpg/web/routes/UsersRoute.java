package samukadev.coderpg.web.routes;

public interface UsersRoute extends BaseRoute {
    String ROOT = BASE_API + "/users";
    String ME =  ROOT + "/me";
    String BY_ID = ROOT + "/{id}";
    String ONBOARDING = ME + "/onboarding";
    String BUILD = ME + "/build";
    String STATS = ME + "/stats";

    String USER_FOLLOWERS = ROOT + "/{userId}/followers";
    String USER_FOLLOWING = ROOT + "/{userId}/following";

    String MY_FOLLOWERS = ME + "/followers";
    String MY_FOLLOWING = ME + "/following";
    String FOLLOW_USER = ME + "/following/{userId}";
}
