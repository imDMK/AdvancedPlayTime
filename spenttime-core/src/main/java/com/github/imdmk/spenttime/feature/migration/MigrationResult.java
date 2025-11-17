package com.github.imdmk.spenttime.feature.migration;

import java.time.Duration;

public record MigrationResult(int total, int successful, int failed, Duration took) {

    public static MigrationResult empty() {
        return new MigrationResult(0, 0, 0, Duration.ZERO);
    }
}
