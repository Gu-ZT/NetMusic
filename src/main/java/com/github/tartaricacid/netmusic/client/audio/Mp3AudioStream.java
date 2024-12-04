package com.github.tartaricacid.netmusic.client.audio;

import com.github.tartaricacid.netmusic.config.GeneralConfig;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import net.minecraft.client.sounds.AudioStream;
import org.lwjgl.BufferUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

/**
 * @author SQwatermark
 */
public class Mp3AudioStream implements AudioStream {
    private final AudioInputStream stream;
    private boolean end = false;

    public Mp3AudioStream(URL url) throws UnsupportedAudioFileException, IOException {
        AudioInputStream originalInputStream = new MpegAudioFileReader().getAudioInputStream(url);
        AudioFormat originalFormat = originalInputStream.getFormat();
        AudioFormat targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, originalFormat.getSampleRate(), 16,
            originalFormat.getChannels(), originalFormat.getChannels() * 2, originalFormat.getSampleRate(), false);
        AudioInputStream targetInputStream = AudioSystem.getAudioInputStream(targetFormat, originalInputStream);
        if (GeneralConfig.ENABLE_STEREO.get()) {
            targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, originalFormat.getSampleRate(), 16,
                1, 2, originalFormat.getSampleRate(), false);
            this.stream = AudioSystem.getAudioInputStream(targetFormat, targetInputStream);
        } else {
            this.stream = targetInputStream;
        }
    }

    @Override
    public AudioFormat getFormat() {
        return stream.getFormat();
    }

    @Override
    public ByteBuffer read(int size) throws IOException {
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(size);
        byte[] array = new byte[size];
        int readSize = 0;
        if (!this.end) {
            while (readSize < size) {
                byte[] temp = new byte[4096];
                int read = this.stream.read(temp, 0, Math.min(4096, size - readSize));
                System.arraycopy(temp, 0, array, readSize, Math.min(read, size - readSize));
                readSize += read;
                if (readSize >= size) break;
                if (read == 0) {
                    this.end = true;
                    break;
                }
            }
        }
        byteBuffer.put(array);
        byteBuffer.flip();
        return byteBuffer;
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
