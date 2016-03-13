package some.example;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.json.JSONObject;

public class JSONServer {

    private int connectedSocketLimit = 10000;
    private RequestCallback onRequest;
    private ErrorCallback onError;

    private ServerSocket server;
    private boolean connected;

    public final ArrayList<JSONClient> clients = new ArrayList<JSONClient>();

    public void setOnRequest(RequestCallback handler) {
        onRequest = handler;
    }

    public void setOnError(ErrorCallback handler) {
        onError = handler;
    }

    public boolean isConnected() {
        return connected;
    }

    public void start(int port) throws IOException {

    }

    public void close() {

    }

    @Override
    protected void finalize() throws Throwable {

    }

    private void handleClient(final JSONClient client) {

    }

    public interface RequestCallback {
        public JSONPacket onRequest(JSONPacket request);
    }

    public interface ErrorCallback {
        public void onError(Exception e);
    }

}