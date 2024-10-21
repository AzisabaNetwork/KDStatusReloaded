package jp.azisaba.lgw.kdstatus.utils;

import jp.azisaba.lgw.kdstatus.TestSize;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UUIDConverterTest {
    @Test
    @Tag(TestSize.SMALL)
    public void UUIDToString() {
        UUID uuid = UUID.randomUUID();
        assertEquals(uuid.toString(), UUIDConverter.insertDashUUID(uuid.toString().replace("-", "")));
        assertEquals(uuid, UUID.fromString(UUIDConverter.insertDashUUID(UUIDConverter.convert(uuid))));
    }
}
