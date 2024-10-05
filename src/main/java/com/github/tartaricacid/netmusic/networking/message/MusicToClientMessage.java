package com.github.tartaricacid.netmusic.networking.message;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.math.BlockPos;

/**
 * @author : IMG
 * @create : 2024/10/3
 */
public class MusicToClientMessage {
    private final BlockPos pos;
    private final String url;
    private final int timeSecond;
    private final String songName;

    public MusicToClientMessage(BlockPos pos, String url, int timeSecond, String songName) {
        this.pos = pos;
        this.url = url;
        this.timeSecond = timeSecond;
        this.songName = songName;
    }

    public static PacketByteBuf toBuffer(MusicToClientMessage message) {
        if (message == null) return PacketByteBufs.empty();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(message.pos);
        buf.writeString(message.url);
        buf.writeInt(message.timeSecond);
        buf.writeString(message.songName);
        return buf;
    }

    public static MusicToClientMessage getMessageFromBuffer(PacketByteBuf buf) {
        return new MusicToClientMessage(
                buf.readBlockPos(),
                buf.readString(),
                buf.readInt(),
                buf.readString()
        );
    }

    public BlockPos getPos() {
        return pos;
    }

    public String getUrl() {
        return url;
    }

    public int getTimeSecond() {
        return timeSecond;
    }

    public String getSongName() {
        return songName;
    }
}
