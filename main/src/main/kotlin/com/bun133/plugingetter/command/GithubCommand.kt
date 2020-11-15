package com.bun133.plugingetter.command

import com.bun133.plugingetter.GithubAsset
import com.bun133.plugingetter.GithubRelease
import com.bun133.plugingetter.GithubReleasesResponse
import com.bun133.plugingetter.Plugingetter
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GithubCommand(var plugin: Plugingetter) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return if (sender is Player) {
            if (sender.isOp) {
                run(sender, command, label, args)
            } else {
                sender.sendMessage("You don't have enough Perm!")
                true
            }
        } else {
            run(sender, command, label, args)
        }
    }

    fun run(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("" + ChatColor.RED + "Wrong Args!")
            return false
        } else {
            if (args.size == 1) {
                val releasesResponse = plugin.API.getReleases(args[0])
                val release = releasesResponse.releases[0]
                val asset: GithubAsset? = foundPluginAsset(release)
                if (asset == null) {
                    sender.sendMessage("" + ChatColor.RED + "[ERROR]Not Found .jar Assets!")
                    return true
                } else {
                    plugin.API.downloadRelease(asset, plugin.dataFolder.absoluteFile)
                    Bukkit.reload()
                    sender.sendMessage("Successfully Plugin added and Reloaded!")
                }
            } else if (args.size == 2) {
                val releasesResponse = plugin.API.getReleases(args[0])
                val release = foundRelease(releasesResponse, args[1])
                if (release == null) {
                    sender.sendMessage("" + ChatColor.RED + "[ERROR]Not Found Release:${args[1]}")
                    return true
                }
                val asset: GithubAsset? = foundPluginAsset(release)
                if (asset == null) {
                    sender.sendMessage("" + ChatColor.RED + "[ERROR]Not Found .jar Assets!")
                    return true
                } else {
                    plugin.API.downloadRelease(asset, plugin.dataFolder.absoluteFile)
                    Bukkit.reload()
                    sender.sendMessage("Successfully Plugin added and Reloaded!")
                }
            } else {
                return false
            }
        }
        return false
    }

    fun foundPluginAsset(release: GithubRelease): GithubAsset? {
        val list = release.assets.filter { it.browser_download_url.endsWith(".jar") }
        return if (list.isEmpty()) null
        else list[0]
    }

    fun foundRelease(releasesResponse: GithubReleasesResponse, ver: String): GithubRelease? {
        val list = releasesResponse.releases.filter { it.name === ver }
        return if (list.isEmpty()) null
        else list[0]
    }
}