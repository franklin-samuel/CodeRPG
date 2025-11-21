package samukadev.coderpg.infrastructure.github.mappers;

import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class LanguageMapper {

    private static final Map<String, String> LANGUAGE_MAP = Map.ofEntries(
            Map.entry("java", "Java"),
            Map.entry("javascript", "JavaScript"),
            Map.entry("typescript", "TypeScript"),
            Map.entry("python", "Python"),
            Map.entry("go", "Go"),
            Map.entry("rust", "Rust"),
            Map.entry("c++", "C++"),
            Map.entry("c#", "C#"),
            Map.entry("ruby", "Ruby"),
            Map.entry("php", "PHP"),
            Map.entry("kotlin", "Kotlin"),
            Map.entry("swift", "Swift"),
            Map.entry("dart", "Dart"),
            Map.entry("scala", "Scala"),
            Map.entry("elixir", "Elixir"),
            Map.entry("clojure", "Clojure")
    );

    public static String mapToSkillName(String githubLanguage) {
        if (githubLanguage == null) return null;

        String normalizedLanguage = githubLanguage.trim().toLowerCase();

        return LANGUAGE_MAP.get(normalizedLanguage);
    }

    public static boolean isSupported(String githubLanguage) {
        if (githubLanguage == null) return false;

        String normalizedLanguage = githubLanguage.trim().toLowerCase();
        return LANGUAGE_MAP.containsKey(normalizedLanguage);
    }

}