package myGUIChat;

import java.io.Serializable;

import org.json.JSONObject;

public class JSONPacket implements Serializable {
	
	protected static final long serialVersionUID = 1112122200L;
	
	public static final String LOGIN_STRING = "login";
	public static final String LOGOUT_STRING = "logout";
	public static final String MESSAGE_STRING = "message";
	public static final String ACK_STRING = "ack";
	
    private byte[] data;
//    private JSONObject payload;
    private String jsonPayload;

    public JSONPacket(String _jsonPayload, byte _data[]) {
    	data = _data;
    	jsonPayload = _jsonPayload;
    }
	
    public JSONPacket(String _jsonPayload) {
		data = null;
		jsonPayload = _jsonPayload;
	}
	
	public String getPayload(){
		return jsonPayload;
	}
	
	public byte[] getExtraData(){
		return data;
	}
}