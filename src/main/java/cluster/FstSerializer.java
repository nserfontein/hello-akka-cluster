package cluster;

import akka.serialization.JSerializer;
import org.nustaq.serialization.FSTConfiguration;

// FIXME: Not used?
public class FstSerializer extends JSerializer {

    public static FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    @Override
    public Object fromBinaryJava(byte[] bytes, Class<?> manifest) {
        return conf.asObject(bytes);
    }

    @Override
    public int identifier() {
        return 428442;
    }

    @Override
    public byte[] toBinary(Object o) {
        return conf.asByteArray(o);
    }

    @Override
    public boolean includeManifest() {
        return false;
    }

}
