package samukadev.coderpg.persistence.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(
        name = "user_builds",
        indexes = {
                @Index(name = "idx_user_builds_user_id", columnList = "user_id"),
                @Index(name = "idx_user_builds_primary_lang", columnList = "primary_language"),
                @Index(name = "idx_user_builds_framework", columnList = "framework")
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class UserBuildEntity extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true,
                foreignKey = @ForeignKey(name = "fk_user_build_user"))
    private UserEntity user;

    @Column(name = "primary_language", nullable = false)
    private String primaryLanguage;

    @Column(name = "primary_language_level", nullable = false)
    private Integer primaryLanguageLevel;

    @Column(name = "primary_language_xp", nullable = false)
    private Integer primaryLanguageXp;

    @Column(name = "secondary_language", nullable = false)
    private String secondaryLanguage;

    @Column(name = "secondary_language_level", nullable = false)
    private Integer secondaryLanguageLevel;

    @Column(name = "secondary_language_xp", nullable = false)
    private Integer secondaryLanguageXp;

    @Column(name = "framework", nullable = false)
    private String framework;

    @Column(name = "framework_level", nullable = false)
    private Integer frameworkLevel;

    @Column(name = "framework_xp", nullable = false)
    private Integer frameworkXp;

    @Column(name = "database", nullable = false)
    private String database;

    @Column(name = "cloud", nullable = false)
    private String cloud;

    @Column(name = "tool1")
    private String tool1;

    @Column(name = "tool2")
    private String tool2;

}