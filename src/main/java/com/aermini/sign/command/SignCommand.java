package com.aermini.sign.command;

import com.aermini.sign.AerSign;
import com.aermini.sign.manager.DatabaseManager;
import com.aermini.sign.ui.SignGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SignCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) return true;
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("aersign.reload")) {
                sender.sendMessage(AerSign.color("&c你没有权限使用此命令"));
                return true;
            }
            AerSign.getInstance().reloadConfig();
            sender.sendMessage(AerSign.color("&a配置文件已重载"));
            return true;
        }

        if (args[0].equalsIgnoreCase("resign")) {
            if (!sender.hasPermission("aersign.admin")) {
                sender.sendMessage(AerSign.color("&c你没有权限使用此命令"));
                return true;
            }
            if (args.length != 2) {
                sender.sendMessage(AerSign.color("&c用法: /aersign resign <玩家名>"));
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(AerSign.color("&c玩家 " + args[1] + " 不在线"));
                return true;
            }
            DatabaseManager db = AerSign.getInstance().getDatabaseManager();
            db.resetTodaySignStatus(target.getUniqueId().toString());
            sender.sendMessage(AerSign.color("&a已重置玩家 " + target.getName() + " 的签到状态"));
            return true;
        }
        if (args[0].equalsIgnoreCase("reall")) {
            if (!sender.hasPermission("aersign.admin")) {
                sender.sendMessage(AerSign.color("&c你没有权限使用此命令"));
                return true;
            }
            if (args.length != 2) {
                sender.sendMessage(AerSign.color("&c用法: /aersign reall <玩家名>"));
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(AerSign.color("&c玩家 " + args[1] + " 不在线"));
                return true;
            }
            DatabaseManager db = AerSign.getInstance().getDatabaseManager();
            db.resetAllSignData(target.getUniqueId().toString());
            sender.sendMessage(AerSign.color("&a已重置玩家 " + target.getName() + " 的签到数据"));
            return true;
        }
        if (args[0].equalsIgnoreCase("open")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 1) {
                    SignGUI.openGUI(player);
                } else if (args.length == 2) {
                    if (!player.hasPermission("aersign.open")) {
                        player.sendMessage(AerSign.color("&c你没有权限使用这个命令"));
                        return true;
                    }
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        player.sendMessage(AerSign.color("&c玩家 " + args[1] + " 不在线"));
                        return true;
                    }
                    SignGUI.openGUI(target);
                } else {
                    player.sendMessage(AerSign.color("&c用法: /aersign open <玩家名>"));
                }
            } else {
                // 控制台执行
                if (args.length != 2) {
                    sender.sendMessage(AerSign.color("&c用法: /aersign open <玩家名>"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(AerSign.color("&c玩家 " + args[1] + " 不在线"));
                    return true;
                }
                SignGUI.openGUI(target);
            }
        } else {}
        return true;
    }
}
