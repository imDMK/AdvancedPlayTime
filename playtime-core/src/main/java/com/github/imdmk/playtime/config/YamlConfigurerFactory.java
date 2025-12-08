package com.github.imdmk.playtime.config;

import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

final class YamlConfigurerFactory {

    private YamlConfigurerFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    static YamlSnakeYamlConfigurer create() {
        final LoaderOptions loader = new LoaderOptions();
        loader.setAllowRecursiveKeys(false);
        loader.setMaxAliasesForCollections(50);

        final Constructor constructor = new Constructor(loader);

        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(2);
        options.setSplitLines(false);

        final Representer representer = new ConfigRepresenter(options);
        final Resolver resolver = new Resolver();

        final Yaml yaml = new Yaml(constructor, representer, options, loader, resolver);
        return new YamlSnakeYamlConfigurer(yaml);
    }
}

