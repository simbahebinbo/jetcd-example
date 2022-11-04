package lansheng228;


import com.google.common.base.Charsets;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.kv.DeleteResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeleteThread extends Thread {
    private final Client client;
    private final String key;

    public DeleteThread(Client client, String key) {
        this.client = client;
        this.key = key;
    }

    public synchronized void run() {
        try {
            DeleteResponse deleteResponse = client.getKVClient()
                    .delete(ByteSequence.from(key, Charsets.UTF_8))
                    .get();
            long num = deleteResponse.getDeleted();
            log.info("DELETE!  key:\t" + key + "\tnum:\t" + num);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }
}


