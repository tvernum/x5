package org.adjective.x5.types.value;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.IO;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Type;
import org.adjective.x5.types.X5Value;
import org.adjective.x5.util.Values;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Optional;

public class IPAddress extends AbstractValueType<String> {

    public IPAddress(InetAddress address, X5StreamInfo source) {
        super(address.getHostAddress(), source);
    }

    @Override
    public X5Type getType() {
        return X5Type.IP_ADDRESS;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, X5Exception {
        IO.writeUtf8(String.valueOf(value), out);
    }

    @Override
    public boolean isEqualTo(String str) {
        return this.value.equals(str);
    }
}
