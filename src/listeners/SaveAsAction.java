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

/**
 *
 * @author jive
 */
public class SaveAsAction extends AbstractAction{
    ApplicationGUI gui;
    public SaveAsAction(ApplicationGUI gui){
        super("save as");
        this.gui = gui;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileBrowser = new JFileChooser();
        int test = fileBrowser.showSaveDialog(gui);
        if(test == JFileChooser.APPROVE_OPTION){
            File file = fileBrowser.getSelectedFile();
            gui.saveAs(file);
        }
    }
    
}
