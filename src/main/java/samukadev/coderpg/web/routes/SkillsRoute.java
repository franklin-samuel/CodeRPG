package samukadev.coderpg.web.routes;

public interface SkillsRoute extends BaseRoute {
    String ROOT = BASE_API + "/skills";
    String HISTORY = ROOT + "/history";
    String EQUIPMENT = ROOT + "/equipment";
    String BY_TYPE = ROOT + "/type/{type}";
}


