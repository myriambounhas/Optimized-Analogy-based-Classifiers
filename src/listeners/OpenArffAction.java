/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

import classification.ApplicationGUI;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import weka.core.converters.ConverterUtils.DataSource;

/**
 *
 * @author jive
 */
public class OpenArffAction extends AbstractAction{
    private ApplicationGUI gui;
    public OpenArffAction(ApplicationGUI gui){
        super("open new arff");
        this.gui = gui;
    }
    
    @Override
    public void actionPerformed(ActionEvent event){
        JFileChooser fileBrowser = new JFileChooser();
        int test = fileBrowser.showOpenDialog(gui);
        if(test == JFileChooser.APPROVE_OPTION){
            File file = fileBrowser.getSelectedFile();
            try {
                DataSource source = new DataSource(file.getPath());
                gui.setInstances(source.getDataSet());
                gui.updateData();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
}
