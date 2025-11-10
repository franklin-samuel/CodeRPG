package samukadev.coderpg.web.routes;

public interface XpRoute extends BaseRoute {
    String ROOT = BASE_API + "/xp";
    String EVENTS = ROOT + "/events";
    String STATS = ROOT + "/stats";
    String HISTORY = ROOT + "/history";
    String LEADERBOARD = ROOT + "/leaderboard";
}
