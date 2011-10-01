/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.ui.pages;

import bam.utils.SwingUtils;
import bam.utils.ui.mvc.DataExtractor;
import bam.utils.ui.mvc.GenericTableModel;
import bam.utils.ui.mvc.GenericTreeModel;
import bam.utils.ui.mvc.GenericTreeModel.Node;
import bam.utils.ui.mvc.pages.Page;
import bgu.csp.az.dev.frm.TestExpirement;
import bgu.csp.az.dev.slog.AgentLogLog;
import bgu.csp.az.dev.slog.ScenarioLogger;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author bennyl
 */
public class CrushInqueryPage extends Page implements CrushInqueryView.Listener {

    CrushInqueryView view;
    GenericTreeModel<Class> dbTablesModel;
    JdbcConnectionSource conn;

    public CrushInqueryPage() {
        super("Crush Inquiry", SwingUtils.resIcon("resources/img/clipboard-search-result.png"));
        dbTablesModel = new GenericTreeModel<Class>(new DBTableNode("", null));
    }

    @Override
    public JPanel getView() {
        if (view == null) {
            view = new CrushInqueryView();
            view.setModel(this);
            view.addListener(this);
        }

        return view;
    }

    public GenericTreeModel<Class> getDbTablesModel() {
        return dbTablesModel;
    }

    @Override
    public void disposeView() {
        view = null;
    }

    @Override
    public void onSQLRunRequested(String query) {
        if (conn == null) {
            conn = ScenarioLogger.getNewDataBaseConnection(TestExpirement.TEMP_SCENARIO_LOG_DB_PATH);
        }
        try {
            Dao dao = DaoManager.createDao(conn, AgentLogLog.class);
            GenericRawResults data = dao.queryRaw(query);
            final HashMap<String, Integer> columnMap = new HashMap<String, Integer>();
            for (int i = 0; i < data.getNumberColumns(); i++) {
                columnMap.put(data.getColumnNames()[i], i);
            }

            GenericTableModel<String[]> resTab = new GenericTableModel<String[]>(new DataExtractor<String[]>(data.getColumnNames()) {

                @Override
                public Object getData(String dataName, String[] from) {
                    return from[columnMap.get(dataName)];
                }
            });
            
            resTab.fillWith(data.getResults());

            view.setSqlResults(resTab);
        } catch (SQLException ex) {
            Logger.getLogger(CrushInqueryPage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static class DBTableNode extends GenericTreeModel.Node {

        public DBTableNode(Object value, Node parent) {
            super(value, parent);
        }

        @Override
        protected Node createChildNode(Object t) {
            if (getValue() instanceof Class) {
                return new GenericTreeModel.SimpleLeafNode(t, this);
            } else {
                return new DBTableNode(t, this);
            }
        }

        @Override
        protected Object[] _getChilds() {
            DatabaseField dbfa;
            if (getValue() instanceof Class) {
                List ret = new LinkedList();
                Class val = (Class) getValue();
                Field[] fields = val.getDeclaredFields();
                for (Field field : fields) {
                    dbfa = field.getAnnotation(DatabaseField.class);
                    if (dbfa != null) {
                        ret.add(field.getName());
                    }
                }
                return ret.toArray();
            } else {
                return ScenarioLogger.DB_TABLES;
            }
        }

        @Override
        public String toString() {
            if (getValue() instanceof Class) {
                Class cls = (Class) getValue();
                DatabaseTable dbta = (DatabaseTable) cls.getAnnotation(DatabaseTable.class);
                if (dbta != null) {
                    return dbta.tableName();
                } else {
                    return "????????";
                }
            } else {
                return super.toString();
            }
        }
    }
}
