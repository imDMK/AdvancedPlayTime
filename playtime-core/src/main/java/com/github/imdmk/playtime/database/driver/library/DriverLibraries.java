package com.github.imdmk.playtime.database.driver.library;

import com.alessiodp.libby.Library;

final class DriverLibraries {

    static final Library MYSQL = Library.builder()
            .groupId("com.mysql")
            .artifactId("mysql-connector-j")
            .version("9.5.0")
            .build();

    static final Library MARIADB = Library.builder()
            .groupId("org.mariadb.jdbc")
            .artifactId("mariadb-java-client")
            .version("3.5.6")
            .build();

    static final Library SQLITE = Library.builder()
            .groupId("org.xerial")
            .artifactId("sqlite-jdbc")
            .version("3.51.0.0")
            .build();

    static final Library POSTGRESQL = Library.builder()
            .groupId("org.postgresql")
            .artifactId("postgresql")
            .version("42.7.8")
            .build();

    static final Library H2 = Library.builder()
            .groupId("com.h2database")
            .artifactId("h2")
            .version("2.4.240")
            .build();

    static final Library SQL = Library.builder()
            .groupId("com.microsoft.sqlserver")
            .artifactId("mssql-jdbc")
            .version("13.2.1.jre11")
            .build();

    private DriverLibraries() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }
}
