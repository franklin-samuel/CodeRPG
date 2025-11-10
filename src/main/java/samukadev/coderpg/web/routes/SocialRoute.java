package samukadev.coderpg.web.routes;

public interface SocialRoute extends BaseRoute {
    String ROOT = BASE_API + "/social";
    String FOLLOW = ROOT + "/follow/{userId}";
    String UNFOLLOW = ROOT + "/unfollow/{userId}";
    String FOLLOWERS = ROOT + "/followers";
    String FOLLOWING = ROOT + "/following";
    String FOLLOWERS_BY_ID = ROOT + "/followers/{userId}";
    String FOLLOWINGS_BY_ID = ROOT + "/following/{userId}";

}
