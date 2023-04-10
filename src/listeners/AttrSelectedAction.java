/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

import classification.ApplicationGUI;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author jive
 */
public class AttrSelectedAction implements ListSelectionListener {

    //not used (replaced by a lambda in visualizerGUI)
    ApplicationGUI gui;

    public AttrSelectedAction(ApplicationGUI gui) {
        this.gui = gui;
    }

    @Override
    public void valueChanged(ListSelectionEvent lse) {
        if(lse.getValueIsAdjusting()){
            gui.updateValues();
        }
    }

}
