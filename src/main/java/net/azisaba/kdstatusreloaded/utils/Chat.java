package net.azisaba.kdstatusreloaded.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

import java.text.MessageFormat;

@UtilityClass
public class Chat {

    // メッセージをフォーマットして、&で色をつける
    public static String f(String text, Object... args) {
        return MessageFormat.format(ChatColor.translateAlternateColorCodes('&', text), args);
    }

    // 色を消す
    public static String r(String text) {
        return ChatColor.stripColor(text);
    }
}