package com.bun133.plugingetter;

import com.bun133.plugingetter.command.GithubCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugingetter extends JavaPlugin {
    public GithubAPI API;
    public GithubCommand Command;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.API = new GithubAPI();
        this.Command = new GithubCommand(this);
        this.getCommand("github").setExecutor(Command);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
