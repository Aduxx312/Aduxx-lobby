package dev.aduxx.aDUXXLOBBY;


import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.Optional;

import org.yaml.snakeyaml.Yaml;

public class LobbyCommand implements SimpleCommand {
    private final ProxyServer server;
    private static String lobbyServerName = "lobby";
    private static Map<String, String> messages;

    public LobbyCommand(ProxyServer server) {
        this.server = server;
    }

    public static void loadConfigs(Path dataFolder) {
        try {
            Yaml yaml = new Yaml();


            Path configPath = dataFolder.resolve("config.yml");
            if (!Files.exists(configPath)) {
                Files.createDirectories(dataFolder);
                Files.writeString(configPath, "serverLobby: \"lobby\"");
            }
            Map<String, Object> config = yaml.load(Files.newBufferedReader(configPath));
            lobbyServerName = (String) config.getOrDefault("serverLobby", "lobby");


            Path messagesPath = dataFolder.resolve("messages.yml");
            if (!Files.exists(messagesPath)) {
                Files.writeString(messagesPath,
                        "nullLobbyServer: \"&cNie znaleziono serwera lobby!\"\n" +
                                "alreadyConnected: \"&4Jesteś już połączony z serwerem lobby!\"");
            }
            messages = yaml.load(Files.newBufferedReader(messagesPath));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if (!(source instanceof Player)) {
            source.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&bCOMMAND ONLY FOR PLAYERS"));
            return;
        }

        Player player = (Player) source;
        Optional<String> currentServer = player.getCurrentServer().map(s -> s.getServerInfo().getName());

        if (currentServer.isPresent() && currentServer.get().equalsIgnoreCase(lobbyServerName)) {
            player.sendMessage(msg("alreadyConnected"));
            return;
        }

        server.getServer(lobbyServerName).ifPresentOrElse(
                registeredServer -> player.createConnectionRequest(registeredServer).fireAndForget(),
                () -> player.sendMessage(msg("nullLobbyServer"))
        );
    }

    private static net.kyori.adventure.text.Component msg(String key) {
        String raw = messages.getOrDefault(key, "&cNO MESSAGES");
        return LegacyComponentSerializer.legacyAmpersand().deserialize(raw);
    }
}
