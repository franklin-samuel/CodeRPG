package samukadev.coderpg.core;

public interface Command<R> {
    R execute(final Context context);
}
