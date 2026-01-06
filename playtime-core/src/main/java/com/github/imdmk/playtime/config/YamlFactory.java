package com.github.imdmk.playtime.config;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

final class YamlFactory {

    private YamlFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    static Yaml create() {
        final LoaderOptions loader = new LoaderOptions();
        loader.setAllowRecursiveKeys(false);
        loader.setMaxAliasesForCollections(50);

        final Constructor constructor = new Constructor(loader);
        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(2);
        options.setSplitLines(false);

        final Representer representer = new YamlRepresenter(options);
        final Resolver resolver = new Resolver();

        return new Yaml(constructor, representer, options, loader, resolver);
    }
}

