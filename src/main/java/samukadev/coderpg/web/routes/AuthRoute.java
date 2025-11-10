package samukadev.coderpg.web.routes;

public interface AuthRoute extends BaseRoute {
    String ROOT = BASE_API + "/auth";
    String LOGIN = BASE_API + "/login";
    String LOGOUT = BASE_API + "/logout";
    String REFRESH = BASE_API + "/refresh";
    String STATUS = BASE_API + "/status";
}
