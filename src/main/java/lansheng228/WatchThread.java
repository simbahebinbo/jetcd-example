package lansheng228;


import com.google.common.base.Charsets;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.Watch.Watcher;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class WatchThread extends Thread {
    private final Client client;
    private final String key;

    //    Revision to start watching
    private final Long rev = 0L;

    //   the maximum number of events to receive
    private final Integer maxEvents = Integer.MAX_VALUE;


    public WatchThread(Client client, String key) {
        this.client = client;
        this.key = key;
    }

    public synchronized void run() {
        CountDownLatch latch = new CountDownLatch(maxEvents);
        Watcher watcher = null;

        try {
            ByteSequence watchKey = ByteSequence.from(key, Charsets.UTF_8);
            WatchOption watchOpts = WatchOption.newBuilder().withRevision(rev).build();

            watcher = client.getWatchClient().watch(watchKey, watchOpts, response -> {
                for (WatchEvent event : response.getEvents()) {
                    String type = event.getEventType().toString();
                    String key = Optional.ofNullable(event.getKeyValue().getKey()).map(bs -> bs.toString(Charsets.UTF_8)).orElse("");
                    String value = Optional.ofNullable(event.getKeyValue().getValue()).map(bs -> bs.toString(Charsets.UTF_8)).orElse("");
                    log.info("WATCH! type={}, key={}, value={}", type, key, value);
                }

                latch.countDown();
            });

            latch.await();
        } catch (Exception e) {
            if (watcher != null) {
                watcher.close();
            }
            log.warn(e.getMessage());
        }
    }
}


