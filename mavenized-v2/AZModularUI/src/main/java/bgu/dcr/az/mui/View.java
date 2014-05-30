/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui;

import bgu.dcr.az.conf.modules.Module;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

/**
 * a view is a basic screen that is allowed to reside inside a view container.
 * views should be registered via the @RegisterView annotation, this annotation
 * will also force at compile time that the view has a
 * {@code public static boolean accept(bgu.dcr.az.mui.ViewContainer)} method
 *
 * once the view container is initialized all its views will be initialized -
 * from this stage on the assumption is that the module container is static and
 * therefore no requirements will be removed or added. this assumption is very
 * limiting though and may be removed in future releases.
 *
 * note that the view {@link #initialize(bgu.dcr.az.mui.ViewContainer)} method
 * (which came from {@link Module}) is delegated to the {@link #onJoined(bgu.dcr.az.mui.ViewContainer)
 * } method - just to keep the jargon a little bit more readable.
 *
 * once a view was joined to a view container its lifecycle is started:
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
 * we will support dynamic view containers (see documentation for {@link View})
 * </dd>
 *
 * <dl>
 *
 *
 * @author bennyl
 */
public interface View extends Module<ViewContainer> {

    @Override
    default void initialize(ViewContainer view) {
        onJoined(view);
    }

    /**
     * this will get called after the view was inserted into the given view
     * container. this is the place to make all your initialization related to
     * the container
     *
     * @param container
     */
    void onJoined(ViewContainer container);

    /**
     * this method will be called each time before the view is shown
     */
    default void onShow() {
    }

    /**
     * this method will be called each time before the the view container (or
     * one of its parents) choose to switch to a different view that make this
     * view hidden
     */
    default void onHide() {
    }

    /**
     * this method will get called once the view needed resources are loaded (if
     * this view is an fxml controller then - this method will be called after
     * the {@link FXMLLoader} has finish loading the view)
     */
    default void onLoad() {
    }

    /**
     * do nothing currently - see interface comments
     */
    default void onClose() {
    }
}
