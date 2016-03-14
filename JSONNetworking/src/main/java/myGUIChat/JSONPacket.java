package myGUIChat;

import org.json.JSONObject;

public class JSONPacket {
	
	protected static final long serialVersionUID = 1112122200L;
	
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