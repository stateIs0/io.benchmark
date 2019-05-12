package io.benchmark;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author 莫那·鲁道
 * @date 2019-05-12-16:09
 */
public class AsyncForceBenchmark {

    static long fileSize = 1024 * 1024 * 1024;

    static int arrLen = 0;

    static int page4 = 1024 * 4 * 4;

    static int[] lenArr = {32, 64, 128, 512, 1024, 2048, 4096, 8192, 16384, 134217728, 1073741824};

    public static void main(String[] args) throws Exception {
        for (int i : lenArr) {
            arrLen = i;
            MmapBM.write();
            FileChannelBM.write();
            System.out.println("============================== length : " + i);
        }
    }

    static class MmapBM {

        static volatile int length = 0;

        static void write() throws IOException {

            MappedByteBuffer mb = FileUtil.getMappedByteBuffer();

            byte[] arr = new byte[arrLen];
            Arrays.fill(arr, (byte) 2);

            CountDownLatch latch = new CountDownLatch(1);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (; ; ) {
                        if (length > page4 && length % page4 == 0) {
                            mb.force();
                        }
                        if (latch.getCount() == 0) {
                            break;
                        }
                    }
                }
            }).start();

            long s = System.currentTimeMillis();
            while (length < mb.capacity()) {
                length += arr.length;
                mb.put(arr);
            }
            latch.countDown();
            length = 0;
            long e = System.currentTimeMillis();
            System.out.println("MappedByteBuffer cost : " + (e - s));
        }
    }

    static class FileChannelBM {

        static volatile int length = 0;

        static void write() throws IOException {
            FileChannel fc = FileUtil.getFileChannel();

            byte[] arr = new byte[arrLen];
            Arrays.fill(arr, (byte) 2);
            CountDownLatch latch = new CountDownLatch(1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (; ; ) {
                        if (length < page4 && length % page4 == 0) {
                            try {
                                fc.force(false);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (latch.getCount() == 0) {
                            break;
                        }
                    }
                }
            }).start();

            long s = System.currentTimeMillis();
            ByteBuffer b = ByteBuffer.wrap(arr);
            while (length < fileSize) {
                length += arr.length;
                fc.write(b);
            }
            latch.countDown();
            length = 0;
            long e = System.currentTimeMillis();
            System.out.println("FileChannel cost : " + (e - s));
        }
    }


}
