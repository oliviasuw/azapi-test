package agt0.dev;

public enum Constants {
	AGENT_CLASS_NAME("bgu.csp.az.api.Agent"),
	WHEN_RECEIVED_CLASS_NAME("bgu.csp.az.api.ano.WhenReceived");
	
	Object data;
	private Constants(Object data) {
		this.data = data;
	}
	
	public String stringData(){
		return data.toString();
	}
}
