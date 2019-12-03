package me.morpheus.metropolis.api.config;

import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface ConfigService {

    GlobalConfig getGlobal();

    CompletableFuture<Void> reload();

    CompletableFuture<Void> save();

}
