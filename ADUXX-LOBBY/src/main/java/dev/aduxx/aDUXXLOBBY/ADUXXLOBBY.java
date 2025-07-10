package dev.aduxx.aDUXXLOBBY;
import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.aduxx.aDUXXLOBBY.LobbyCommand;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "aduxx-lobby", name = "ADUXX-LOBBY", version = "1.0", authors = {"aduxx"})
public class ADUXXLOBBY {
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataFolder;

    @Inject
    public ADUXXLOBBY(ProxyServer server, Logger logger, @DataDirectory  Path dataFolder) {
        this.server = server;
        this.logger = logger;
        this.dataFolder = dataFolder;
    }

    @com.velocitypowered.api.event.Subscribe
    public void onProxyInit(ProxyInitializeEvent event) {
        LobbyCommand.loadConfigs(dataFolder);
        server.getCommandManager().register("lobby", new LobbyCommand(server));
    }
}