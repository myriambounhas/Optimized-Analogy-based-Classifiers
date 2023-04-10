/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;
import classification.ApplicationGUI;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author jive
 */
public class ClassifyAction extends AbstractAction{
    ApplicationGUI gui;
    public ClassifyAction(ApplicationGUI gui){
        super("start");
        this.gui = gui;
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        gui.display();
    }
}
