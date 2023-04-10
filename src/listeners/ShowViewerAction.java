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
import javax.swing.JFrame;
import javax.swing.JPanel;
import weka.core.converters.ConverterUtils;
import weka.gui.arffviewer.ArffViewer;
import weka.gui.arffviewer.ArffViewerMainPanel;

/**
 *
 * @author jive
 */
public class ShowViewerAction extends AbstractAction {

    public ShowViewerAction() {
        super("arff viewer");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        ArffViewer arffView = new ArffViewer();
        ArffViewerMainPanel viewerPanel = arffView.getMainPanel();
        JFrame frame = new JFrame();
        JFileChooser fileBrowser = new JFileChooser();
        int test = fileBrowser.showOpenDialog(frame);
        if (test == JFileChooser.APPROVE_OPTION) {
            File file = fileBrowser.getSelectedFile();
            viewerPanel.loadFile(file.getPath());
            frame.setContentPane(viewerPanel);
            frame.setSize(viewerPanel.getSize());
            frame.setResizable(true);
            frame.setVisible(true);
            try {
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}