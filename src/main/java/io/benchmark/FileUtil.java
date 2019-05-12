package io.benchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.UUID;

/**
 *
 * @author 莫那·鲁道
 * @date 2019-05-12-12:42
 */
public class FileUtil {

    static File getRandomFile() {
        String fileName = UUID.randomUUID().toString();

        File file = new File(fileName);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                file.delete();
            }
        }));
        return file;
    }

    static RandomAccessFile getRandomAccessFile() throws IOException {
        File file = FileUtil.getAlreadyFillFile();
        RandomAccessFile ra = new RandomAccessFile(file, "r");
        return ra;
    }

    static FileChannel getFileChannel() throws FileNotFoundException {
        File file = FileUtil.getRandomFile();
        FileChannel fc = new RandomAccessFile(file, "rw").getChannel();
        return fc;
    }

    static MappedByteBuffer getMappedByteBuffer() throws IOException {
        File file = FileUtil.getRandomFile();
        MappedByteBuffer mb = new RandomAccessFile(file, "rw").getChannel().
            map(FileChannel.MapMode.READ_WRITE, 0, 1024 * 1024 * 1024);
        return mb;
    }

    static File getAlreadyFillFile() throws IOException {
        File file = getRandomFile();
        FileOutputStream fo = new FileOutputStream(file);
        byte[] arr = new byte[1024 * 1024 * 1024];
        Arrays.fill(arr, (byte) 1);
        fo.write(arr);
        return file;
    }

}
