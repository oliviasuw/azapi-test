package bgu.dcr.az.dev.debug;

import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.tools.NesteableTool;

public class TestingNestedTool extends NesteableTool{

	private boolean alreadyRun = false;
	
	@Override
	protected SimpleAgent createNestedAgent() {
		return new MyNesteadAgent();
	}
	
	public boolean isAlreadyRun() {
		return alreadyRun;
	}
	
	public class MyNesteadAgent extends SimpleAgent{

		@Override
		public void start() {
			log("this is the nestead agent!");
			log("going to perform long calculation!");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			alreadyRun = true;
			finish();
		}
		
	}
	
}
