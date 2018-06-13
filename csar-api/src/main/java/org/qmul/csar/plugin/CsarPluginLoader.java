package org.qmul.csar.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.function.Consumer;

/**
 * A plugin loader for classes which extend {@link CsarPlugin} residing in JARs.
 * This will examine the current working directory non-recursively.
 */
public final class CsarPluginLoader {

    private final ServiceLoader<CsarPlugin> loader;

    public CsarPluginLoader() {
        loader = ServiceLoader.load(CsarPlugin.class, classLoader());
    }

    public void reload() {
        loader.reload();
    }

    public void forEachPlugin(Consumer<? super CsarPlugin> action) {
        loader.iterator().forEachRemaining(action);
    }

    /**
     * Returns an iterator over the currently loaded plugins.
     */
    public Iterator<CsarPlugin> plugins() {
        return loader.iterator();
    }

    /**
     * Returns a {@link URLClassLoader} which inspects JARs in the current working directory non-recursively.
     */
    private static ClassLoader classLoader() {
        File[] files = new File(".").listFiles(f -> f.getPath().toLowerCase().endsWith(".jar"));
        URL[] urls = new URL[files.length];

        for (int i = 0; i < files.length; i++) {
            try {
                urls[i] = files[i].toURI().toURL();
            } catch (MalformedURLException ignored) {
            }
        }
        return new URLClassLoader(urls);
    }
}
