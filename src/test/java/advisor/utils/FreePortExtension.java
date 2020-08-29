package advisor.utils;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.net.ServerSocket;

public class FreePortExtension implements BeforeEachCallback {

    private int port;

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            port = socket.getLocalPort();
        }
    }

    public int getPort() {
        return port;
    }
}
