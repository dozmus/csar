package org.qmul.csar.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.function.Consumer;

public final class CsarPluginLoader {

    private static CsarPluginLoader instance;
    private final ServiceLoader<CsarPlugin> loader;

    private CsarPluginLoader() {
        loader = ServiceLoader.load(CsarPlugin.class, classLoader());
    }

    public void reload() {
        loader.reload();
    }

    public void forEachPlugin(Consumer<? super CsarPlugin> action) {
        loader.iterator().forEachRemaining(action);
    }

    public Iterator<CsarPlugin> plugins() {
        return loader.iterator();
    }

    public static synchronized CsarPluginLoader getInstance() {
        if (instance == null) {
            instance = new CsarPluginLoader();
        }
        return instance;
    }

    /**
     * Returns a {@link URLClassLoader} which inspects JARs in the current working directory non-recursively.
     */
    private ClassLoader classLoader() {
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
