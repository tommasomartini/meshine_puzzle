package some.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

import org.json.JSONObject;

public class JSONClient {

    private boolean connected;
    private Socket client;

    private static final byte[] VERSION1 = new byte[] { 0x17, 0x78 };

    public static JSONPacket getResponse(String host, int port,
            JSONPacket request) throws IOException {
        return null;
    }

    public JSONClient() {
		
    }

    protected JSONClient(Socket baseClient) {

    }

    public boolean isConnected() {
        return connected;
    }

    public void connect(SocketAddress endpoint) throws IOException {
  
    }

    public void close() {

    }

    /**
     * Writes <code>packet</code> to the <code>Socket</code>.
     * 
     * @param packet
     *            The <code>JSONPacket</code> to write to <code>Socket</code>.
     * @throws IOException
     *             If an IO/Error has occurred.
     */
    public void writePacket(JSONPacket packet) throws IOException {

    }

    /**
     * Reads and creates a <code>JSONPacket</code> from the <code>Socket</code><br>
     * <br>
     * If <code>null</code> is returned, the other side of the
     * <code>Socket</code> should be treated as incompatible, and the
     * <code>Socket</code> be closed immediately.
     * 
     * @param str
     *            The stream to read the <code>JSONPacket</code> from.
     * @return A <code>JSONPacket</code> representing the data from the stream,
     *         or <code>null</code> if there were no bytes to read or the data
     *         does not follow the format.
     * @throws IOException
     *             If an IO/Error has occurred.
     */
    public JSONPacket readPacket() throws IOException {
		return null;
    }

    public void setSoTimeout(int timeout) throws SocketException {

    }

    private static int byteArrayToInt(byte[] buffer, int offset) {
        return (buffer[offset] & 0xFF) << 24
                | (buffer[offset + 1] & 0xFF) << 16
                | (buffer[offset + 2] & 0xFF) << 8 | buffer[offset + 3] & 0xFF;
    }

    private static byte[] intToByteArray(int buffer) {
        return new byte[] { (byte) ((buffer >> 24) & 0xFF),
                (byte) ((buffer >> 16) & 0xFF), (byte) ((buffer >> 8) & 0xFF),
                (byte) (buffer & 0xFF) };
    }

}