package lansheng228;


import cn.hutool.core.lang.Pair;
import com.google.common.base.Charsets;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PutThread extends Thread {
    private final Client client;
    private final Pair<String, String> keyValuePair;

    public PutThread(Client client, Pair<String, String> keyValuePair) {
        this.client = client;
        this.keyValuePair = keyValuePair;
    }

    public synchronized void run() {
        try {
            String key = keyValuePair.getKey();
            String value = keyValuePair.getValue();
            client.getKVClient()
                    .put(ByteSequence.from(key, Charsets.UTF_8),
                            ByteSequence.from(value, Charsets.UTF_8))
                    .get();
            log.info("PUT!  key:\t" + key + "\tvalue:\t" + value);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }
}


