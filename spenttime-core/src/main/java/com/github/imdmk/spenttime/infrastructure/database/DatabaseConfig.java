package com.github.imdmk.spenttime.infrastructure.database;

import com.github.imdmk.spenttime.shared.config.ConfigSection;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

public class DatabaseConfig extends ConfigSection {

    @Comment({
            "# Database mode",
            "# Supported values: SQLITE, MYSQL"
    })
    public DatabaseMode databaseMode = DatabaseMode.SQLITE;

    @Comment("# Database file name (only SQLITE)")
    public String databaseFileName = "database.db";

    @Comment("# MySQL hostname (ignored for SQLITE)")
    public String hostname = "localhost";

    @Comment("# MySQL database name (ignored for SQLITE)")
    public String database = "database";

    @Comment("# MySQL username (ignored for SQLITE)")
    public String username = "root";

    @Comment({
            "# MySQL password (ignored for SQLITE)",
            "# WARNING: Never share this file with others if it contains your production password!"
    })
    public String password = "ExamplePassword1101";

    @Comment("# MySQL port (default: 3306) (ignored for SQLITE)")
    public int port = 3306;

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {};
    }

    @Override
    public @NotNull String getFileName() {
        return "databaseConfig.yml";
    }
}
