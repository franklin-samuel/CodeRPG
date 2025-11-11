package samukadev.coderpg.web.routes;

public interface AuthRoute extends BaseRoute {
    String ROOT = BASE_API + "/auth";
    String LOGOUT = ROOT + "/logout";
    String REFRESH = ROOT + "/refresh";
    String STATUS = ROOT + "/status";
}
