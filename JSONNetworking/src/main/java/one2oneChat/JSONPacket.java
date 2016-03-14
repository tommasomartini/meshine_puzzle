package one2oneChat;

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