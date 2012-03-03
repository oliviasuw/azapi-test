package bgu.dcr.az.dev.debug3;

import java.util.ArrayList;



/**
 * this simple implementation of a TimeStamp
 * is made of a Vector of agent counters.
 * comparisons are done with respect to lexicographic order.
 * 
 * @author operry5
 */
public class SimpleTimeStamp implements TimeStamp<ArrayList<Integer>> {

	private ArrayList<Integer> stamp;	//the stamp itself
	private int ownerId;	//who is owner agent: used for increaseLocalTime() and CompareTo()
	
	/**
	 * initializes a TimeStamp with all counters zeroed
	 * @param size is amount of agents (hence, stamp's length)
	 */
	public SimpleTimeStamp(int ownerIdx,int size){
		if(ownerIdx<size)
			ownerId=ownerIdx;
		else
			ownerIdx=size-1;
		stamp = new ArrayList<Integer>();
		for(int i=0; i< size; i++)
			stamp.add(0);
	}
	
	/**
	 * 
	 * @param other
	 */
	public SimpleTimeStamp(SimpleTimeStamp other){
		ownerId=other.getOwnerIdx();
		stamp= other.getStamp();
	}
	
	/**
	 * sets agent's time component to 'time'. 
	 * ignores if agent out of bounds 
	 */
	@Override
	public void setAgentTime(int agent, int time){
		if(agent < stamp.size() && agent>=0)
			stamp.set(agent,time);
	}
	

	@Override
	public Object getAgentTime(int i) {
		return new Integer(this.stamp.get(i));
	}
	
	public int getOwnerIdx(){ return ownerId;}
	
	public int getSize(){return this.stamp.size();}
	
	@Override
	public int compareTo(TimeStamp<ArrayList<Integer>> other) {
		for(int i=0; i<= this.getOwnerIdx() && i<=other.getOwnerIdx(); i++)
			if(this.stamp.get(i) > (Integer)other.getAgentTime(i))
				return 1;
			else if( this.stamp.get(i) < (Integer)other.getAgentTime(i))
				return -1;
		return 0;
	}

	/**
	 * increases agentId's time parameter by 1
	 * resets consequent agent's time parameter
	 * @param agentId
	 */
	public void increment(int agentId){
		int time = (Integer)(this.getAgentTime(agentId));
		this.setAgentTime(agentId, time+1);
		setAgentTime(agentId+1, 0);	//set agent makes sure agentId+1 is in bounds (ignores otherwise)
	}
	
	public void increaseLocalTime(){
		this.setAgentTime(ownerId, (Integer)getAgentTime(ownerId+1));
		setAgentTime(ownerId+1, 0);
	}
	
	/**
	 * DOES NOT CHANGE THE OWNER
	 */
	@Override
	public boolean updateStamp(TimeStamp<ArrayList<Integer>> other) {
		if(this.compareTo(other) < 0){	//do update
			this.stamp = other.getStamp();
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return a clone of this.stamp
	 */
	@Override
	public ArrayList<Integer> getStamp(){
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for(int i=0; i<this.stamp.size();i++)
			ret.add(this.stamp.get(i));
		return ret;
	}

	public String toString(){
		String ans = new String(this.stamp.get(0).toString());
		for(int i=1; i<this.getSize(); i++)
			ans+=","+this.stamp.get(i);
		return ans.trim();
	}

}
