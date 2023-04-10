/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;

/**
 *
 * @author jive
 */
public class Placeholder implements FocusListener {
    String text;
    public Placeholder(String text){
        this.text = text;
    }

    @Override
    public void focusGained(FocusEvent e) {
        JTextField txt = (JTextField) e.getSource();
        txt.setText("");
    }

    @Override
    public void focusLost(FocusEvent e) {
        JTextField txt = (JTextField) e.getSource();
        if(txt.getText().length() == 0){
            txt.setText(text);
        }
    }

}
