/**
 * 
 */
package bgu.dcr.az.dev.debug3;

import java.util.HashSet;

import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.tools.NestableTool;

/**
 * @author alongrub
 *
 */
public class NestableACNash extends NestableTool {
	ACNashAgent distACNash;
	
	/* (non-Javadoc)
	 * @see bgu.dcr.az.api.tools.NestableTool#createNestedAgent()
	 */
	@Override
	protected SimpleAgent createNestedAgent() {
		distACNash = new ACNashAgent();
		return distACNash;
	}

	public HashSet<Integer> getReducedDomain(){
		return distACNash.getpStarDomain();
	}
}
