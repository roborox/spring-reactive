package ru.roborox.reactive.mail;

public class Attachment {
	private String cid;
	private String name;
	private String type = "plain/text";
	private byte[] data;
	
	public Attachment() {
	}
	
	public Attachment(String name, String type, byte[] data) {
		this.name = name;
		this.type = type;
		this.data = data;
	}
	
	public Attachment(String cid, String name, String type, byte[] data) {
		this.cid = cid; 
		this.name = name;
		this.type = type;
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getCid() {
		return cid;
	}
	
	public void setCid(String cid) {
		this.cid = cid;
	}
}
