package cn.upc.entity;

public enum Mark {
	CLIENT("Client"), SERVER("Server"), READY("Ready"), END("End"), YES("Yes"), NO("No"), FORWARD("Forward"), SELECT(
			"Select"), SET("Set");
	private String name;

	private Mark(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
