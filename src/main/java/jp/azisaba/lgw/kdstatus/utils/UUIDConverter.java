package jp.azisaba.lgw.kdstatus.utils;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UUIDConverter {

    public static String convert(@NotNull UUID uuid) {
        return uuid.toString().replace("-", "");
    }

    public static String insertDashUUID(String uuid) {
        StringBuilder sb = new StringBuilder(uuid);
        sb.insert(8, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(13, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(18, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(23, "-");

        return sb.toString();
    }
}