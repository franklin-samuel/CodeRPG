package samukadev.coderpg.web.routes;

public interface UsersRoute extends BaseRoute {
    String ROOT = BASE_API + "/users";
    String ME =  BASE_API + "/me";
    String BY_ID = ROOT + "/{id}";
    String ONBOARDING = ROOT + "/onboarding";
    String BUILD = ROOT + "/build";
    String STATS = ROOT + "/stats";
}
