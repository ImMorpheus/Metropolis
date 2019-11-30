package me.morpheus.metropolis.api.custom;

import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

@CatalogedBy(CustomResourceLoaders.class)
public interface CustomResourceLoader<T> extends CatalogType {

    Collection<T> load();

    T load(Path path) throws IOException, ObjectMappingException;

    void save();

}
