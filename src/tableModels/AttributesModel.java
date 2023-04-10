/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tableModels;

import javax.swing.table.DefaultTableModel;
import weka.core.Instances;

/**
 *
 * @author jive
 */
public final class AttributesModel extends DefaultTableModel {
    private Instances instances;
    private final String[] header = {"No.", "Name", "Type"};
    
    public void setInstances(Instances instances){
        this.instances = instances;
        fireTableDataChanged();
    }
    
    @Override
    public int getRowCount() {
        if (instances == null) {
            return 0;
        }
        return instances.numAttributes();
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
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(instances == null){
            return "empty";
        }
        switch (columnIndex) {
            case 0:
                return rowIndex;
            case 1:
                return instances.attribute(rowIndex).name();
            default:
                return instances.attribute(rowIndex).type() == 0 ? "numeric" : "nominal";
        }
    }

}
