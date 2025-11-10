package samukadev.coderpg.web.routes;

public interface MissionsRoute extends BaseRoute {
    String ROOT = BASE_API + "/missions";
    String DAILY = ROOT + "/daily";
    String WEEKLY = ROOT + "/weekly";
    String ACTIVE = ROOT + "/active";
    String COMPLETE = ROOT + "{id}/complete";
    String BY_ID = ROOT + "/{id}";

}
