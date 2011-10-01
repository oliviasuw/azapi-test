/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.params.views;

import bc.swing.models.DataExtractor;
import bc.swing.models.DataInserter;
import bc.swing.pfrm.params.ParamModel;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author BLutati
 */
public class MapPV extends TablePV {

    public MapPV() {
        hideTableHeader();
    }

    @Override
    public DataExtractor getDataExtractor(ParamModel model) {
        return new DataExtractor("k", "v") {

            @Override
            public Object getData(String dataName, Object from) {
                if (dataName.equals("k")) {
                    return ((Map.Entry) from).getKey();
                } else {
                    return ((Map.Entry) from).getValue();
                }
            }
        };
    }

    @Override
    public DataInserter getDataInserter(ParamModel model) {
        return null;
    }

    @Override
    public List getItemList(ParamModel model) {
        Map m = (Map) model.getValue();
        return new LinkedList(m.entrySet());
    }

}
