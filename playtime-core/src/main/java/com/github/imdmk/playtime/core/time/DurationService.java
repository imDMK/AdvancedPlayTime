package com.github.imdmk.playtime.core.time;

import com.github.imdmk.playtime.core.injector.ComponentPriority;
import com.github.imdmk.playtime.core.injector.annotations.Service;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.time.Duration;

@Service(priority = ComponentPriority.LOWEST)
public final class DurationService {

    private final DurationFormatter formatter;

    @Inject
    DurationService(DurationFormatConfig config) {
        this.formatter = new DurationFormatter(
                config.pattern,
                config.separator,
                config.lastSeparator,
                config.zero
        );
    }

    public String format(Duration duration) {
        return formatter.format(duration);
    }
}
