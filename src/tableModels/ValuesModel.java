/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tableModels;

import javax.swing.table.AbstractTableModel;
import weka.core.Attribute;
import weka.core.Instances;

/**
 *
 * @author jive
 */
public class ValuesModel extends AbstractTableModel {
    private Instances instances;
    private Attribute attribute;
    private final String[] header = {"No.", "Label", "Count"};
    

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
        fireTableDataChanged();
    }
    
    public void setInstances(Instances instances){
        this.instances = instances;
        setAttribute(instances.attribute(0));
    }

    @Override
    public int getRowCount() {
        if (attribute == null) {
            return 0;
        }
        return attribute.numValues();
    }

    @Override
    public int getColumnCount() {
        return header.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return header[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (attribute == null) {
            return "empty";
        }
        switch (columnIndex) {
            case 0:
                return rowIndex;
            case 1:
                return attribute.value(rowIndex);
            default:
                return instances.attributeStats(attribute.index()).nominalCounts[rowIndex];
        }
    }

}
