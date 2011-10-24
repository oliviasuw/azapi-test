package bgu.csp.az.api;

import bgu.csp.az.api.ds.NonBlockingCounter;
import bgu.csp.az.utils.DeepCopyUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A base class for SimpleMessage and AbstractMessage it defines the most basic features that a message should have (a sender, a name and some more staff). 
 * Messages can contain metadata that can be used for passing data about the message like timestamp, statistics etc. – some metadata is filled automatically by the simulator and some can be added by the algorithm.
 * @author bennyl
 */
public class Message implements Serializable {

    /**
     * a temporary flag this flag will get deprecated after all the algorithms will be transformed to work with deep copy
     */
    public static boolean USE_DEEP_COPY = true;
    /**
     * optional flag -> means that this message is descided to be discarded (the agent should ignore it..)
     */
    public static final int DISCARDED = 1;
    /**
     * optional flag -> means that this message is irelevant (most of the time because its time stamp is too old)
     */
    public static final int OLD = 2;
    private String name; //the message name (= type)
    private int from; //the sender of the message
    /**
     * the attached metadata for the message 
     * you can use it to send any kind of data that is not part of the message fields 
     * think about it at the content on the envelop of the message - you may want to write the timestamp there etc.
     */
    protected Map<String, Object> metadata;
    /**
     * the collection of flags this message flagged with (bitmap)
     */
    protected int flag;

    /**
     * @param name the message name / type
     * @param from the agent sending this message
     */
    public Message(String name, int from) {
        this.name = name;
        this.from = from;
        this.metadata = new HashMap<String, Object>();
    }

    /**
     * remark: if the use deep copy static variable is false then this function return this message unchanged.
     * @return a deep copy of this message.
     */
    public Message copy() {
        if (!USE_DEEP_COPY) {
            return this;
        }

        Message athis = DeepCopyUtil.deepCopy(this);
        return athis;
    }

    /**
     * @return the name of this message (can be reffered as type)
     */
    public String getName() {
        return name;
    }

    /**
     * @return who is sending this message
     */
    public int getSender() {
        return from;
    }

    /**
     * set the sender
     * @param from
     */
    protected void setFrom(int from) {
        this.from = from;
    }

    /**
     * @return the metadata attached to this object (as a key value map)
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * sets the name of this message
     * @param name
     */
    protected void setName(String name) {
        this.name = name;
    }

    /**
     * flag this message with the given flag - available flags are static variables of this class.
     * @param flag 
     */
    public void flag(int flag) {
        this.flag |= flag;
    }

    /**
     * remove the flag from the message flag collection
     * @param flag 
     */
    public void unFlag(int flag) {
        this.flag &= ~flag;
    }

    /**
     * @param flag
     * @return true if the message is flagged with the given flag.
     */
    public boolean isFlaged(int flag) {
        return (this.flag & flag) != 0;
    }
}
