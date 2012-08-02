package agt0.dev;

import java.util.LinkedList;
import java.util.List;

public enum Global {
	AGENT_CLASS_NAME("bgu.dcr.az.api.Agent"),
	WHEN_RECEIVED_CLASS_NAME("bgu.dcr.az.api.ano.WhenReceived"),
	TUTORIAL_URL("https://docs.google.com/document/d/1B19TNQd8TaoAQVX6njo5v9uR3DBRPmFLhZuK0H9Wiks/view"),
	BUG_TRACKING_SYSTEM_URL("http://132.72.46.50/redmine/projects/agent-zero/issues/new");
	
	Object data;
	private Global(Object data) {
		this.data = data;
	}
	
	public <T> T data(){
		//lets discover T:
		List<T> t = new LinkedList<T>(){};
		
		return (T)data;
	}
	
	public String string(){
		return data.toString();
	}
}
