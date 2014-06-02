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
import bgu.dcr.az.mui.info.TokenSetChangedInfo;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javafx.fxml.FXMLLoader;

/**
 * a controller is a class responsible for controlling and presenting views, it
 * can do so by itself or by an child controllers that it contains. the
 controllers that a controller contain must accept to join it via their static
 accept method.

 controller also contain a set of tokens, these tokens is there to installInto
 additional information for the internal controllers, this information will
 help them decide if they wish to join the controller parent.

 controllers should be registered via the @{@link RegisterController}
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
public abstract class BaseController<V> extends ModuleContainer {

    private Set<String> tokens = new HashSet<>();
    private boolean viewLoaded = false;

    BaseController() {
        super(true);
    }

    public abstract V getView();

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
    public BaseController<?> parent() {
        return (BaseController) super.parent();
    }

    /**
     * search for the first controller that is willing to accept this one as its
     * leader, create and manage it. the found controller will be returned
     *
     * @param <T> the controller class that should be returned
     * @param children
     * @return
     */
    public <T extends Controller> T findAndInstall(String children) {
        return (T) ControllerRegistery.get().createController(children, (BaseController) this);
    }

    /**
     * search for all the controller that are willing to accept this one as
     * their leader, create and manage them. the found controller will be
     * returned
     *
     * @param group
     * @return
     */
    public Iterable<Controller> findAndInstallAll(String group) {
        return ControllerRegistery.get().createControllers(group, (BaseController) this);
    }

    /**
     * @return iterable of all the currently managed controllers
     */
    public Iterable<Controller> installedControllers() {
        return requireAllLocal(Controller.class);
    }

    /**
     * @return the amount of managed controllers
     */
    public int amountInstalledControllers() {
        return amountInstalledLocally(Controller.class);
    }

    /**
     * return true if the given child is managed by this controller
     *
     * @param child
     * @return
     */
    boolean isInstalled(Controller child) {
        return isInstalled(child, Controller.class);
    }

    @Override
    public void uninstallLocally(Module module) {
        super.uninstallLocally(module);
        if (module instanceof Controller) {
            ((Controller) module).onRemovedFromParent();
        }
    }

    public void uninstall() {
        if (parent() != null) {
            parent().uninstallLocally(this);
        }
    }

    public final void loadView() {
        if (viewLoaded) {
            return;
        }
        viewLoaded = true;
        onLoadView();
        for (Controller c : installedControllers()) {
            c.loadView();
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

        require(InfoStream.class)
                .writeIfListening(() -> new TokenSetChangedInfo((Controller) this), TokenSetChangedInfo.class);
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
    protected abstract void onLoadView();

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
