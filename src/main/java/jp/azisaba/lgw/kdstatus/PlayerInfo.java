package jp.azisaba.lgw.kdstatus;

import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlayerInfo {
    private final UUID uuid;
    private final String name;
}