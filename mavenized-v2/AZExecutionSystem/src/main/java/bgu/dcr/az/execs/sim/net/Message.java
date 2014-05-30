package bgu.dcr.az.execs.sim.net;

import bgu.dcr.az.common.deepcopy.DeepCopyUtil;
import bgu.dcr.az.common.deepcopy.DeepCopyable;
import bgu.dcr.az.common.tos.ToStringUtils;
import bgu.dcr.az.common.exceptions.UncheckedInterruptedException;
import bgu.dcr.az.execs.exceptions.PanicException;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A base class for SimpleMessage and AbstractMessage it defines the most basic
 * features that a message should have (a sender, a name and some more staff).
 * Messages can contain metadata that can be used for passing data about the
 * message like timestamp, statistics etc. – some metadata is filled
 * automatically by the simulator and some can be added by the algorithm.
 *
 * @author bennyl
 */
public class Message implements Serializable {

    // ID RESERVATION RELATED
    private static int RESERVATION_COUNT = 20;

    private static class IdReservation {

        long from = -1;
        int count = 0;
    }
    private static final ThreadLocal<IdReservation> reservations = new ThreadLocal<>();
    private static final AtomicLong idProvider = new AtomicLong(0);

    private static long generateUniqueId() {
        IdReservation reservation = reservations.get();
        if (reservation == null) {
            reservation = new IdReservation();
            reservations.set(reservation);
        }

        if (reservation.count <= 0) {
            reservation.from = idProvider.getAndAdd(RESERVATION_COUNT);
            reservation.count = RESERVATION_COUNT;
        }

        return reservation.from + (reservation.count--);
    }

    // MESSAGE FIEDLS 
    private final long messageId;
    private String name; //the message name (= type)
    private int sender; //the sender of the message
    private final int recepient;

    /**
     * collection of the message arguments the arguments are unnamed -> TODO:
     * FIGUREOUT A WAY TO NAME THEM FOR DEBUGGING PORPUSE -> MAYBE COMPILE TIME
     * PROCESSING USING APT..
     */
    private final Object[] args;

    /**
     * @param name the message name / type
     * @param from the agent sending this message
     * @param args
     * @param recepient
     * @param context
     */
    public Message(String name, int from, Object[] args, int recepient) {
        this.name = name;
        this.sender = from;
        this.args = args;
        this.recepient = recepient;
        messageId = generateUniqueId();
    }

    private Message(String name, int from, Object[] args, int recepient, long messageId) {
        this.name = name;
        this.sender = from;
        this.args = args;
        this.recepient = recepient;
        this.messageId = messageId;
    }

    public long getMessageId() {
        return messageId;
    }

    public int getRecepient() {
        return recepient;
    }

    /**
     * @return the arguments of this message the args are ordered at the same
     * way that the sender sent them / the receiver got them which means that if
     * you sent the mesasge using: send("message", a, b, c).to(x)
     * message.getArgs[0] = a message.getArgs[0] = b message.getArgs[0] = c
     *
     * aside from arguments message can also contain metadata accesible via
     * {@link getMetadata()}
     */
    public Object[] getArgs() {
        return args;
    }

    public Message copy() {
        Object[] cargs = new Object[this.args.length];

        for (int i = 0; i < args.length; i++) {
            Object a = args[i];
            if (a instanceof DeepCopyable) {
                cargs[i] = ((DeepCopyable) a).deepCopy();
            } else if (a == null || a instanceof Number || a instanceof Enum || a instanceof String || a instanceof Boolean) {
                cargs[i] = a;
            } else {
                try {
                    cargs[i] = DeepCopyUtil.deepCopy(a);
                } catch (Exception ex) {
                    if (ex instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                        throw new UncheckedInterruptedException((InterruptedException) ex);
                    } else {
                        throw new PanicException("Cannot figure out how to deep copy " + a.getClass().getName() + " class please implement the DeepCopyable interface on this class.", ex);
                    }
                }
            }
        }

        Message ret = new Message(getName(), getSender(), cargs, recepient, messageId);
        return ret;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Object a : args) {
            sb.append(ToStringUtils.toString(a)).append(", ");
        }
        return "[" + getName() + (args.length > 0 ? ": " + sb.substring(0, sb.length() - 2) + "]" : "]");
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
        return sender;
    }

    /**
     * set the sender
     *
     * @param from
     */
    protected void setSender(int from) {
        this.sender = from;
    }

    /**
     * sets the name of this message
     *
     * @param name
     */
    protected void setName(String name) {
        this.name = name;
    }
}
