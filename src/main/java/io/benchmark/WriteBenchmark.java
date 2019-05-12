package io.benchmark;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 *
 * @author 莫那·鲁道
 * @date 2019-05-12-16:09
 */
public class WriteBenchmark {

    static long fileSize = 1024 * 1024 * 1024;

    static int arrLen = 0;

    static int[] lenArr = {32, 64, 128, 512, 1024, 2048, 4096, 8192, 16384, 134217728, 1073741824};

    public static void main(String[] args) throws Exception {
        for (int i : lenArr) {
            arrLen = i;
            MmapBM.write();
            FileChannelBM.write();
            RandomAccessFileBM.write();
            FileOutputStreamBM.write();
            System.out.println("============================== length : " + i);
        }
    }

    static class MmapBM {

        static void write() throws IOException {

            MappedByteBuffer mb = FileUtil.getMappedByteBuffer();

            byte[] arr = new byte[arrLen];
            Arrays.fill(arr, (byte) 2);
            int length = 0;
            long s = System.currentTimeMillis();
            while (length < mb.capacity()) {
                length += arr.length;
                mb.put(arr);
            }
            // 测试 force, 纯粹写测试时,应该关闭,对性能影响很大
//            long s1 = System.currentTimeMillis();
//            mb.force();
//            long s2 = System.currentTimeMillis();
//            System.out.println("force : " + (s2 - s1));
            long e = System.currentTimeMillis();
            System.out.println("MappedByteBuffer cost : " + (e - s));
        }
    }

    static class FileChannelBM {

        static void write() throws IOException {
            FileChannel fc = FileUtil.getFileChannel();

            byte[] arr = new byte[arrLen];
            Arrays.fill(arr, (byte) 2);

            int length = 0;
            long s = System.currentTimeMillis();
            ByteBuffer b = ByteBuffer.wrap(arr);
            while (length < fileSize) {
                length += arr.length;
                fc.write(b);
            }
            long e = System.currentTimeMillis();
            System.out.println("FileChannel cost : " + (e - s));
        }
    }

    static class RandomAccessFileBM {

        static void write() throws IOException {

            RandomAccessFile ra = FileUtil.getRandomAccessFile();

            byte[] arr = new byte[arrLen];
            Arrays.fill(arr, (byte) 2);
            long s = System.currentTimeMillis();
            int length = 0;
            while (length < fileSize) {
                length += arr.length;
                ra.write(arr);
            }

            long e = System.currentTimeMillis();
            System.out.println("RandomAccessFile cost : " + (e - s));
        }
    }

    static class FileOutputStreamBM {

        static void write() throws IOException {
            File file = FileUtil.getRandomFile();

            FileOutputStream fo = new FileOutputStream(file);

            byte[] arr = new byte[arrLen];
            Arrays.fill(arr, (byte) 2);
            long s = System.currentTimeMillis();
            int length = 0;
            while (length < fileSize) {
                length += arr.length;
                fo.write(arr);
            }
            long e = System.currentTimeMillis();
            System.out.println("FileOutputStream cost : " + (e - s));
        }
    }
}
