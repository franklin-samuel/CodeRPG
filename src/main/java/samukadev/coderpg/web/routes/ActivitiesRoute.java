package samukadev.coderpg.web.routes;

public interface ActivitiesRoute extends BaseRoute {
    String ROOT = BASE_API + "/activities";
    String FEED = ROOT + "/feed";
    String BY_USER = ROOT + "/users/{userId}";
    String PUBLIC = ROOT + "/public";
    String BY_TYPE = ROOT + "/type/{type}";
}
