package net.azisaba.kdstatusreloaded.utils;

import org.jspecify.annotations.NonNull;

import java.util.UUID;

public class UUIDConverter {

    public static String convert(@NonNull UUID uuid) {
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