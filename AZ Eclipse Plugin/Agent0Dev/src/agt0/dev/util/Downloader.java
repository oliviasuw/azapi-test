package agt0.dev.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Downloader {

//    private ExecutorService pool;

//    public Downloader(ExecutorService pool) {
//        this.pool = pool;
//    }

//    public void download(List<String> urls, final File intoFolder) throws InterruptedException {
//        intoFolder.mkdirs();
//        List<Future> futures = new LinkedList<Future>();
//
//        for (final String url : urls) {
//            Future f = pool.submit(new Runnable() {
//                @Override
//                public void run() {
//                    download(url, intoFolder);
//                }
//            });
//            futures.add(f);
//        }
//
//        for (Future f : futures) {
//            try {
//                f.get();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//        }
//
//        System.out.println("downloader done");
//    }

    /**
     * on error will return null
     *
     * @param url
     * @param intoFolder
     * @return
     */
    public static void download(String url, File intoFile) throws IOException {
        File intoFolder = intoFile.getParentFile();
        intoFolder.mkdirs();

        URL down;
        down = new URL(url);
        ReadableByteChannel rbc = Channels.newChannel(down.openStream());
//      String fileName = intoFolder.getAbsolutePath() + "/" + new File(down.getFile()).getName();
        FileOutputStream fos = new FileOutputStream(intoFile);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }
    
}
