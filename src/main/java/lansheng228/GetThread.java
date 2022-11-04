package lansheng228;


import com.google.common.base.Charsets;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetThread extends Thread {
    private final Client client;
    private final String key;

    //specify the kv revision
    private final Long rev = 0L;


    public GetThread(Client client, String key) {
        this.client = client;
        this.key = key;
    }

    public synchronized void run() {
        try {
            GetResponse getResponse = client.getKVClient()
                    .get(ByteSequence.from(key, Charsets.UTF_8),
                            GetOption.newBuilder().withRevision(rev).build())
                    .get();

            if (getResponse.getKvs().isEmpty()) {
                // key does not exist
                log.warn("key is not exist");
                return;
            }

            String value = getResponse.getKvs().get(0).getValue().toString(Charsets.UTF_8);
            log.info("GET!  key:\t" + key + "\tvalue:\t" + value);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }
}


