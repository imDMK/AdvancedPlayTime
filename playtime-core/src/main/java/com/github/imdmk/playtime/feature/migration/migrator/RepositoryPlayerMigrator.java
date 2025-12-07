package com.github.imdmk.playtime.feature.migration.migrator;

import com.github.imdmk.playtime.shared.Validator;
import com.github.imdmk.playtime.user.User;
import com.github.imdmk.playtime.user.UserFactory;
import com.github.imdmk.playtime.user.repository.UserRepository;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.concurrent.CompletableFuture;

public final class RepositoryPlayerMigrator implements PlayerMigrator {

    private final UserRepository userRepository;
    private final UserFactory userFactory;

    @Inject
    public RepositoryPlayerMigrator(
            @NotNull UserRepository userRepository,
            @NotNull UserFactory userFactory
    ) {
        this.userRepository = Validator.notNull(userRepository, "userRepository cannot be null");
        this.userFactory = Validator.notNull(userFactory, "userFactory cannot be null");
    }

    @Override
    public CompletableFuture<User> migrate(@NotNull OfflinePlayer player) {
        final User user = userFactory.createFrom(player);
        return userRepository.save(user);
    }
}
