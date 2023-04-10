 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates


 * and open the template in the editor.
 */
package classification;

import classification.Functions;
 
  
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import listeners.AttrSelectedAction;
import listeners.ClassifyAction;
import listeners.OpenArffAction;
import listeners.OpenTestAction;
import listeners.Placeholder;
import listeners.SaveAsAction;
import listeners.ShowViewerAction;
import tableModels.AttributesModel;
import tableModels.ValuesModel;
import weka.core.Attribute;
import weka.core.Instances;

/**
 *
 * @author Myriam Bounhas
 */
public class ApplicationGUI extends JFrame {

    private Instances instances;
    private Instances test;
   
    
    public void setInstances(Instances instances) {
        this.instances = instances;
    }

    public void SetTest(Instances test) {
        if (test != null) {
            if (test.relationName().equals(instances.relationName())) {
                this.test = test;
                cboAlgo.setEnabled(true);
                classifyAction.setEnabled(true);
                lblTest.setText(this.test.relationName() + ".arff");
            } else {
                this.test = null;
                cboAlgo.setEnabled(false);
                classifyAction.setEnabled(false);
                lblTest.setText("error");
            }
        }
    }
    private int knn = 1;
     private static int Nbr_ssss ;
     private static String relationName;
    private JLabel lblDataName, lblNbAttrs, lblNbInstances, lblTest;
    private JTable tblAttrs;
    private JTextArea txtOutput;
    private JTextField txtFolds, txtRuns, txtThresh;
    private JComboBox cboAlgo;
    private JRadioButton rdoCV, rdoTest;
    private JCheckBox chkSave;
    private JButton openTest;

    private ValuesModel valuesModel;
    private AttributesModel attrsModel;
    private ClassifyAction classifyAction;

    public ApplicationGUI() {
        super("AP Classifiers");
        build();
    }

    private void build() {
        setSize(800, 600);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(buildMenuBar());
        setLayout(new GridLayout(1, 2, 8, 8));
        add(buildLeftSide());
        add(buildRightSide());
        setVisible(true);
    }

    private JPanel buildLeftSide() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(buildDataInfoPane(), BorderLayout.NORTH);
        panel.add(buildAttributesPane(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildDataInfoPane() {
        JPanel panel = new JPanel();
        TitledBorder border = new TitledBorder(new LineBorder(Color.DARK_GRAY), "Current Dataset");
        border.setTitleFont(new Font("Arial", NORMAL, 15));
        panel.setBorder(border);

        lblDataName = new JLabel("name: empty");
        lblNbAttrs = new JLabel("attributes: empty", JLabel.SOUTH_EAST);
        lblNbInstances = new JLabel("instance: empty");

        panel.setLayout(new GridLayout(2, 2, 0, 8));
        panel.add(lblDataName);
        panel.add(lblNbAttrs);
        panel.add(lblNbInstances);

        return panel;
    }

    private JPanel buildAttributesPane() {
        JPanel panel = new JPanel();

        attrsModel = new AttributesModel();
        tblAttrs = new JTable(attrsModel);
        tblAttrs.setShowVerticalLines(false);
        tblAttrs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel selectionModel = tblAttrs.getSelectionModel();
        selectionModel.addListSelectionListener(new AttrSelectedAction(this));
        JScrollPane scrllAttrs = new JScrollPane(tblAttrs);

        valuesModel = new ValuesModel();
        JTable tblValues = new JTable(valuesModel);
        JScrollPane scrllValues = new JScrollPane(tblValues);

        TitledBorder border = new TitledBorder(new LineBorder(Color.DARK_GRAY), "Attributes");
        border.setTitleFont(new Font("Arial", Font.PLAIN, 15));
        panel.setBorder(border);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel("all attributes"));
        panel.add(scrllAttrs);
        panel.add(new JLabel("selected attribute values"));
        panel.add(scrllValues);

        return panel;
    }

    private JPanel buildRightSide() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(buildClassifyPane());
        return panel;
    }

    private JPanel buildOptionsPane() {
        JPanel panel = new JPanel();
        panel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        rdoCV = new JRadioButton("Cross-validation");
        rdoCV.setEnabled(false);
        rdoCV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                classifyAction.setEnabled(instances != null);
                cboAlgo.setEnabled(instances != null);
                txtFolds.setEnabled(instances != null);
                txtRuns.setEnabled(instances != null);
                openTest.setEnabled(false);
            }
        });

        txtFolds = new JTextField("10");
        txtFolds.setEnabled(false);
        txtFolds.addFocusListener(new Placeholder("folds"));
        txtRuns = new JTextField("5");
        txtRuns.addFocusListener(new Placeholder("runs"));
        txtRuns.setEnabled(false);
        txtThresh = new JTextField("Nbr_ssss");
        txtThresh.addFocusListener(new Placeholder("Nbr_ssss"));
        txtThresh.setEnabled(true);

        rdoTest = new JRadioButton("Test set");
        rdoTest.setEnabled(false);
        rdoTest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                classifyAction.setEnabled(instances != null && test != null);
                cboAlgo.setEnabled(instances != null && test != null);
                txtFolds.setEnabled(false);
                txtRuns.setEnabled(false);
                txtThresh.setEnabled(true);
                openTest.setEnabled(false);
            }
        });

        ButtonGroup radios = new ButtonGroup();
        radios.add(rdoTest);
        radios.add(rdoCV);

        openTest = new JButton(new OpenTestAction(this));
        openTest.setEnabled(false);
        
        lblTest = new JLabel();
        lblTest.setFont(new Font("Courier", Font.ITALIC, 12));

        panel.setLayout(new GridLayout(3, 3, 6, 6));
        cboAlgo = new JComboBox(new String[]{
            "Baseline" ,"Baseline-Optimized" ,"kAC" , "kAC-Optimized"   });
        cboAlgo.setEnabled(false);
        
        classifyAction = new ClassifyAction(this);
        JButton start = new JButton(classifyAction);
        chkSave = new JCheckBox("save");
        
        

        panel.add(rdoCV);
        panel.add(txtFolds);
        panel.add(txtRuns);
        panel.add(rdoTest);
        panel.add(openTest);
        panel.add(txtThresh);
//        panel.add(lblTest);
        panel.add(cboAlgo);
        panel.add(start);
        panel.add(chkSave);
        return panel;
    }

    private JPanel buildClassifyPane() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        TitledBorder border = new TitledBorder(new LineBorder(Color.DARK_GRAY), "Classify");
        border.setTitleFont(new Font("Arial", NORMAL, 15));
        panel.setBorder(border);
        panel.add(buildOptionsPane());

        panel.add(new JLabel("output"));

        txtOutput = new JTextArea(25, 30);
        JScrollPane scrllOutput = new JScrollPane(txtOutput);
        panel.add(scrllOutput);
        return panel;
    }

    private JMenuBar buildMenuBar() {
        JMenuBar menubar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem open = new JMenuItem(new OpenArffAction(this));
        JMenuItem save = new JMenuItem(new SaveAsAction(this));
        file.add(open);
        file.add(save);

        JMenu filter = new JMenu("Viewer");
        JMenuItem arffViewer = new JMenuItem(new ShowViewerAction());
        filter.add(arffViewer);

        menubar.add(file);
        menubar.add(filter);
        return menubar;
    }

    public void updateData() {
        attrsModel.setRowCount(0);
        lblDataName.setText("relation: " + instances.relationName());
        lblNbAttrs.setText("attributes: " + instances.numAttributes());
        lblNbInstances.setText("instances: " + instances.numInstances());
        attrsModel.setInstances(instances);
        tblAttrs.repaint();
        valuesModel.setInstances(instances);
        rdoCV.setEnabled(true);
        rdoTest.setEnabled(true);

         relationName= instances.relationName();
   
    }

    public void updateValues() {
        Attribute attribute = instances.attribute(tblAttrs.getSelectedRow());
        valuesModel.setAttribute(attribute);
    }

    private void displayTest() {
        
    }
    public static int getNbr_ssss(){
        return Nbr_ssss;
    }
//differnt cross validation
    private void displayCV() {
        try {
            int k = Integer.parseInt(txtFolds.getText());
            int runs = Integer.parseInt(txtRuns.getText());
            Nbr_ssss = Integer.parseInt(txtThresh.getText());
            if (instances != null && k > 1 && runs > 0) {
                Validation validation = new  Validation(instances);
               
                Classifier myclassifier = new Classifier(instances);
                int[][] confusion;
                if((cboAlgo.getSelectedIndex()==0)||(cboAlgo.getSelectedIndex()==1)){// BAseline with/without optimization 
                    confusion = validation.kfcv_multiple(cboAlgo.getSelectedIndex(), runs, k);
                }
                else   {//AC with optimal k: k is the nearest neighbor of d
                     confusion = validation.outerAPCV_multiple(cboAlgo.getSelectedIndex(), runs, k);
                }
                
                StringBuilder output = new StringBuilder("Relation = " + instances.relationName());
                output.append(String.format("%nClassifier = %s ", cboAlgo.getSelectedItem()))
                        .append(String.format("%nValidation = %d run(s) of %d folds cross-validation", runs, k))
                         .append(String.format("%nAccuracy among classified =  %.2f $\\pm$ %.2f", Functions.accuracy(confusion) * 100,validation.getStd()*100))
                         .append(String.format("%nUnclassified =  %.2f ", validation.getPropoUnclassified()*100))
                       // .append(String.format("%n  %.0f " ,(validation.getRunTimeTrTs())))
                        .append(String.format("%nNbrAtt_SSSS = %d ", getNbr_ssss()));
                        
                       if((cboAlgo.getSelectedIndex()==2)||(cboAlgo.getSelectedIndex()==3)){ 
                            output.append(String.format("%nOptimal k = %.0f", validation.getOptimalK()));
                       }
 
                      
                        
                 output.append("\n");
                  output.append("Confusion Matrix:\n");
                for (int i = 0; i < confusion.length; i++) {
                    output.append("\n");
                    for (int j = 0; j < confusion[i].length; j++) {
                        output.append(String.format("%04d |", confusion[i][j]));
                    }
                }
                for (int i = 0; i < confusion.length; i++) {
                    output.append(String.format("%nPrecision(%d) = %.2f", i, Functions.precision(i, confusion)))
                            .append(String.format("%nRecall(%d) = %.2f", i, Functions.recall(i, confusion)))
                            .append(String.format("%nF1score(%d) = %.2f", i, Functions.f1score(i, confusion)))
                            .append("\n--------------");
                }
                txtOutput.setText(output.toString());
                quickSave(output.toString());
            } else {
                txtOutput.setText("error: verify instances is not null, folds > 1 and runs > 0 ");
            }

        } catch (NumberFormatException nfe) {
            txtOutput.setText("fatal error: " + nfe.getMessage());
        }
    }

    public void display() {
        System.out.println(instances.relationName() + " started");
        if (rdoCV.isSelected()) {
            displayCV();
        } else if (rdoTest.isSelected()) {
            displayTest();
        }
    }

    private void quickSave(String text) {
        if (chkSave.isSelected()) {
            PrintWriter printer = null;
            String fileName;
            try {
                if (rdoCV.isSelected()) {
                    fileName = cboAlgo.getSelectedItem() + txtRuns.getText() + "_" + txtFolds.getText() + ".txt";
                } else {
                    fileName = cboAlgo.getSelectedItem() + "_test.txt";
                }
                printer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
                printer.println("==================");
                printer.println(text);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                printer.close();
            }
        }
    }

    public void saveAs(File file) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(txtOutput.getText());
            writer.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
 
//returns the OPTIMAL value of param k optimized BEFORE in an inner CV and saved here
public static int getOptKnn(){
     int knn = 1;
    
     switch (relationName) {
          
                        //BOOLEAN datasets
                         case "AND2": {knn = 4;
                            break;}
                         
                        case "AND7": {knn =3 ;
                        break;}
                         
                        case "NOT":{ knn= 3;
                        break;}
                        
                         case "OR2": {knn = 4;
                            break;}
                         
                        case "OR7": {knn = 2;
                        break;}
                        
                        case "ANDOR": {knn = 6;
                        break;}
                        
                        case "SUM7": {knn = 18;
                        break;}
                                                
                        case "XOR": {knn =1;
                        break;}
                        
                        case "XORMIN": {knn =3;
                        break;}
                        
                        
                        //Nominal datasets
                        case "balance": {knn = 20;
                          break;}
                        
                        case "car": {knn =  15;
                            break;}
                         
                         case "tictactoe": {knn = 4;
                        break;}
                         
                        case "monk1":{ knn= 9;
                        break;}
                        
                        case "monk2":{ knn= 18;
                        break;}
                        
                        case "monk3":{ knn= 7;
                        break;}
                        
                        case "breast-cancer": {knn = 17;
                        break;}
                                                
                       case "voting": {knn = 14;
                        break;}
                        
                        case "hayes-roth": {knn =12;
                        break;}
                      
                    }
       
     return knn;
 }


public static String getrelationName(){
     return relationName;
}
}
