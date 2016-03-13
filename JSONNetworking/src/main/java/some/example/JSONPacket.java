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

public class JSONPacket {
    private byte[] data;
    private JSONObject payload;

    public JSONPacket(JSONObject pPayload, byte pData[]) {
    	data = pData;
		payload = pPayload;
    }
	
    public JSONPacket(JSONObject pPayload) {
		data = null;
		payload = pPayload;
	}
	
	public JSONObject getPayload(){
		return payload;
	}
	
	public byte[] getExtraData(){
		return data;
	}
}