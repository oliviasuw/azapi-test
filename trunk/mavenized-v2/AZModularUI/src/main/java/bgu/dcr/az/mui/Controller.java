/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui;

import bgu.dcr.az.common.exceptions.UnexpectedException;
import bgu.dcr.az.conf.modules.Module;
import bgu.dcr.az.conf.modules.ModuleContainer;
import bgu.dcr.az.conf.modules.info.InfoStream;
import bgu.dcr.az.conf.modules.info.PipeInfoStream;
import bgu.dcr.az.mui.info.TokenSetChangedInfo;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javafx.fxml.FXMLLoader;

/**
 * a controller is a class responsible for controlling and presenting views, it
 * can do so by itself or by an child controllers that it contains. the
 * controllers that a controller contain must accept to join it via their static
 * accept method.
 *
 * controller also contain a set of tokens, these tokens is there to supply
 * additional information for the internal controllers, this information will
 * help them decide if they wish to join the controller parent.
 *
 * controllers should be registered via the @{@link RegisterController}
 * annotation, this annotation will also force at compile time that the
 * controller has a
 * {@code public static boolean accept(bgu.dcr.az.mui.Controller)} method
 *
 * once the controller is initialized all its sub controllers will be
 * initialized - from this stage on the assumption is that the controller
 * underling module container is static and therefore no requirements will be
 * removed or added. this assumption is very limiting though and may be removed
 * in future releases.
 *
 * note that the controller {@link #initialize(bgu.dcr.az.mui.Controller)}
 * method (which came from {@link Module}) is delegated to the {@link #onJoined(bgu.dcr.az.mui.Controller)
 * } method - just to keep the jargon a little bit more readable.
 *
 * once a controller is joined to a group it has the following lifecycle:
 * <dl>
 *
 * <dt> {@link #onLoad() } </dt>
 * <dd> once the view needed resources are loaded (if this view is an fxml
 * controller then - this method will be called after the {@link FXMLLoader} has
 * finish loading the view) </dd>
 *
 * <dt> {@link #onShow()} </dt>
 * <dd> this method will be called before the view is shown, note that this
 * method can be called more than one time since the view container (or one of
 * its parents) can choose to switch to a different view - if such thing happen
 * then first the {@link #onHide()} method will be called and then when the view
 * will be reshown the {@link #onShow()} will be called again
 * </dd>
 *
 * <dt> {@link #onHide()} </dt>
 * <dd> this method will be called when the view container (or one of its
 * parents) choose to switch to a different view that make this view hidden
 * </dd>
 *
 * <dt> {@link #onClose()} </dt>
 * <dd> this method currently does nothing, its will became active if and when
 * we will support dynamic view containers (see documentation for
 * {@link Controller})
 * </dd>
 *
 * <dl>
 *
 *
 * @author bennyl
 * @param <V> view type
 */
public abstract class Controller<V> extends ModuleContainer implements Iterable<Controller> {

    private Set<String> tokens = new HashSet<>();
    private PipeInfoStream infoStream;

    public Controller() {
        supply(InfoStream.class, infoStream = new PipeInfoStream());
    }

    public abstract V getView();

    public InfoStream infoStream() {
        return infoStream;
    }

    /**
     * @return the tokens that are introduced by this container only - not take
     * into consideration its parents
     */
    public Iterable<String> localTokens() {
        return tokens;
    }

    /**
     * @return the tokens that are defined by this container or any of its
     * parents
     */
    public Iterable<String> tokens() {
        if (parent() == null) {
            return tokens;
        }

        HashSet<String> result = new HashSet<>(tokens);
        parent().tokens().forEach(result::add);
        return result;
    }

    /**
     * @param token
     * @return true if this container or any of its parents contain the given
     * token
     */
    public boolean hasToken(String token) {
        return tokens.contains(token) || (parent() != null && parent().hasToken(token));
    }

    /**
     * @param token
     * @return true if this container contain the given token, not taking into
     * consideration the tokens contained by its parents
     */
    public boolean hasLocalToken(String token) {
        return tokens.contains(token);
    }

    @Override
    public Iterator<Controller> iterator() {
        return requireAll(Controller.class).iterator();
    }

    @Override
    public Controller<?> parent() {
        return (Controller) super.parent();
    }


    @Override
    public void initialize(ModuleContainer mc) {
        super.initialize(mc);
        if (mc.hasRequirement(InfoStream.class)) {
            infoStream.setPipeTarget(mc.require(InfoStream.class));
        }
    }

    public void manageAll(Iterable<? extends Controller> children) {
        supplyAll(Controller.class, children, true);
    }

    public void manage(Controller children) {
        supply(Controller.class, children, true);
    }

    public void leave() {
        if (parent() != null) {
            parent().remove(this);
        }
    }

    /**
     * change the set of tokens in this container, will notify this change via
     * {@link TokenSetChangedInfo} info
     *
     * @param tokens
     */
    public void setTokens(String[] tokens) {
        this.tokens = new HashSet<>(Arrays.asList(tokens));
        infoStream().writeIfListening(() -> new TokenSetChangedInfo(this), TokenSetChangedInfo.class);
    }

    /**
     * this method will be called each time before the view is shown
     */
    protected void onShow() {
    }

    /**
     * this method will be called each time before the the view container (or
     * one of its parents) choose to switch to a different view that make this
     * view hidden
     */
    protected void onHide() {
    }

    /**
     * this method will get called when the need to load the view is arise
     * (mainly after done joining all the controllers)
     */
    public abstract void onLoadView();

    /**
     * do nothing currently - see interface comments
     */
    protected void onClose() {
    }

    public static Controller create(Class<? extends Controller> c) {
        try {
            return c.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new UnexpectedException(ex);
        }
    }

}
