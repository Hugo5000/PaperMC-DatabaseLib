package at.hugob.plugin.library.database;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Common code that can be used to communicate with a Database, e.g. to transform a UUID to a byte[] or vise versa
 */
public class DatabaseUtils {
    private DatabaseUtils() {
    }

    /**
     * Converts a {@code UUID} to an {@code byte[]}
     *
     * @param uuid UUID to convert
     * @return a byte array corresponding to the uuid
     */
    public static byte[] convertUuidToBinary(@NotNull final UUID uuid) {
        final byte[] bytes = new byte[16];
        ByteBuffer.wrap(bytes).putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits());
        return bytes;
    }

    /**
     * Converts an {@code byte[]} to a {@code UUID}
     *
     * @param bytes bytes to convert
     * @return a UUID corresponding to the byte array
     */
    public static UUID convertBytesToUUID(final byte[] bytes) {
        final ByteBuffer buffer = ByteBuffer.allocate(16).put(bytes).flip();
        return new UUID(buffer.getLong(), buffer.getLong());
    }
}
