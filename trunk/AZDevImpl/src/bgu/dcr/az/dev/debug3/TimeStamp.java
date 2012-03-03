package bgu.dcr.az.dev.debug3;

public interface TimeStamp<T> extends Comparable<TimeStamp<T>> {
	
	/**
	 * sets an agent's part of the timeStamp
	 * @param agent is agentId 
	 * @param time is this agent's current "time"
	 */
	public void setAgentTime(int agent, int time);
	
	/**
	 * if 'other' stamp is later than this stamp,
	 * update known stamp.
	 * 
	 * (this method should use the CompareTo method)
	 * 
	 * @param other - another stamp to update to (or disregard)
	 * @return 'true' if a change was made (false if stamp was discarded) 
	 */
	public boolean updateStamp(TimeStamp<T> other);
	
	/**
	 * 
	 * @return a clone of the stamp itself
	 */
	public T getStamp();

	/**
	 * If defined - returns owner's index in the stamp
	 * @return
	 */
	public int getOwnerIdx();
	
	/**
	 * 
	 * @param i agentId who's time component we wish to get
	 * @return a time component of agent i 
	 */
	public /*Integer*/Object getAgentTime(int i);
	
	/**
	 * increment the asked coordinate.
	 * ZEROES the next coordinate 
	 * @param agentId
	 */
	public void increment(int agentId);
}
