package com.github.tartaricacid.netmusic.client.audio;

import com.github.tartaricacid.netmusic.NetMusic;
import com.github.tartaricacid.netmusic.api.NetWorker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public final class MusicPlayManager {
    private static final String ERROR_404 = "http://music.163.com/404";
    private static final String MUSIC_163_URL = "https://music.163.com/";
    private static final String LOCAL_FILE_PROTOCOL = "file";

    public static void play(String url, String songName, Function<URL, ISound> sound) {
        if (url.startsWith(MUSIC_163_URL)) {
            try {
                url = NetWorker.getRedirectUrl(url, NetMusic.NET_EASE_WEB_API.getRequestPropertyData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (url != null && !url.equals(ERROR_404)) {
            playMusic(url, songName, sound);
        }
    }

    private static void playMusic(String url, String songName, Function<URL, ISound> sound) {
        final URL urlFinal;
        try {
            urlFinal = new URL(url);
            // 如果是本地文件
            if (urlFinal.getProtocol().equals(LOCAL_FILE_PROTOCOL)) {
                File file = new File(urlFinal.toURI());
                if (!file.exists()) {
                    NetMusic.LOGGER.info("File not found: {}", url);
                    return;
                }
            }
            Minecraft.getInstance().submitAsync(() -> {
                Minecraft.getInstance().getSoundManager().play(sound.apply(urlFinal));
                Minecraft.getInstance().gui.setNowPlaying(new StringTextComponent(songName));
            });
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
