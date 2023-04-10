/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classification;

import static classification.Validation.IsNumericData;

import static classification.Validation.PropGoodPredictor_d;
import static classification.Validation.TotalNbrPredictor_d;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import weka.core.Attribute;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.LinearNNSearch;
import weka.core.DistanceFunction;
import weka.core.ManhattanDistance;
/**
 *
 * @author Myriam Bounhas
 */
public class Classifier {

    private Instances instances;
    private double propCompPair=0;   
    private double RF =0;   //Reduction factor of nbr pair/triple = |S|*|S|/|C|
    //private double [] scoreAP ;
   // private double [] parameters ;
    private int  Nbr_ssss ;
 //local
   Boolean numericData = Validation.IsNumericData ;
  
   String relationName= ApplicationGUI.getrelationName();
   int NbrAttr ;
   int nbrClass;
  
   
public void printParameters(){
    String out;
    out =  " Nbr_ssss= "+Nbr_ssss ;
    System.out.println(out);
}

public void setParameters(){
 
// Proportion of att in a triplet with pure analogy : s s s s
Nbr_ssss = ApplicationGUI.getNbr_ssss(); 
  
}

public Classifier(Instances instances) {
        this.instances = instances;
         NbrAttr = instances.numAttributes()-1;
         Nbr_ssss = ApplicationGUI.getNbr_ssss();
         nbrClass = instances.numClasses();
          
}
 

/*public double Avg(double [] SeuilBeta_nn , Instances Trainers, Instances  nearest_d, int knn_cl){
     double avgSeuilBeta=0;
     for (int k=0;k<knn_cl; k++){
        int indNN = Functions.getInstanceIndex(Trainers,nearest_d.get(k)); 
        avgSeuilBeta+= SeuilBeta_nn[indNN];
     }
     avgSeuilBeta/=knn_cl;
    return avgSeuilBeta;
 }
 */

//BASELINE AP-Classifier: Bounhas et al. 2017
public int[] Baseline(Instance d ) {
 int nbClasses = instances.numClasses();
 double[] sumP = new double[nbClasses];
 double[] bestP = new double[nbClasses];
 double[] vote = new double[nbClasses];

System.out.println( d.toString());  
    for (int i = 0; i < instances.size() - 2; i++) {
            Instance a = instances.instance(i);
            for (int j = i + 1; j < instances.size()-1; j++) {
                Instance b = instances.instance(j);
                  for (int k = j + 1; k < instances.size(); k++) {
                        Instance c = instances.instance(k);
               
                int clValue = Functions.AnalogyInClass((int)a.classValue(), (int)b.classValue(),(int)c.classValue() );
                if ( clValue != -1){
                    double  ap = 0;
                    
                    Enumeration<Attribute> attributes = instances.enumerateAttributes();
                    while (attributes.hasMoreElements()) {
                        Attribute attribute = attributes.nextElement();
                        double attrP=0;
                        if (!IsNumericData)
                        {attrP = Functions.AnalogyNominal(a.stringValue(attribute), b.stringValue(attribute), c.stringValue(attribute), d.stringValue(attribute));
                            }
                        else     
                         attrP = Functions.Analogy(
                                a.value(attribute), b.value(attribute), c.value(attribute), d.value(attribute));
                       ap += attrP;
                    }

                    ap /= instances.numAttributes() - 1;
                    if(IsNumericData){//numerical case
                        sumP[clValue]+= ap ;
                    }
                    else{ //nominal case
                        if ( ap >= 1) { // beta =1 for nominal
                          sumP[clValue]++;
                        }  
                    }
                    if (bestP[clValue] < ap) {
                        bestP[clValue] = ap;
                        vote[clValue] = 1;
                   } else if (bestP[clValue] == ap) {
                        vote[clValue]++;
                    }
                }
            }
          }
        }
//classification               
System.out.print(" ScoreP: ");  
Functions.Display(sumP);
int prediction =-1;
if ((Functions.max(sumP)!=0)& Functions.unique(Functions.max(sumP),sumP)){ //add unique// classified with this beta
         prediction = Functions.superior(sumP, bestP, vote);
         System.out.println("  ---> reel "+ d.classValue() + " predicted "+ prediction);
        }
//not classified cases     
if(prediction==-1){
    System.out.println(" NOT CLASSIFIED!  : "+ Nbr_ssss  +" high ");
   System.out.println("  ---> reel "+ d.classValue() + " predicted "+ prediction);
}
else if (prediction!=(int) d.classValue())  System.out.println(" Wrong classification !");

return new int[]{(int) d.classValue(), prediction};
}


//Optimized BASELINE AP-Classifier: Bounhas et Prade, IJAR2023
 public int[] Baseline_SSSSAttr (Instance d ) {
 int nbClasses = instances.numClasses();
 double[] sumP = new double[nbClasses];
 double[] bestP = new double[nbClasses];
 double[] vote = new double[nbClasses];
 
System.out.println( d.toString());  
//Compute the proportion of successful voter for THIS d
ComputePropSuccTriplet_Baseline( d ) ;

double NbrAnal_ssss=0;//ssss
double  NbrAnal_sstt=0;// sstt
double  NbrAnal_stst=0;// stst
int prediction = -1;
Nbr_ssss = ApplicationGUI.getNbr_ssss(); 

while (prediction == -1 & Nbr_ssss >=0){
  System.out.println("Nbr_ssss= "+ Nbr_ssss  );

        for (int i = 0; i < instances.size() - 2; i++) {
            Instance a = instances.instance(i);
            for (int j = i + 1; j < instances.size()-1; j++) {
                Instance b = instances.instance(j);
                  for (int k = j + 1; k < instances.size(); k++) {
                        Instance c = instances.instance(k);
               
                int clValue = Functions.AnalogyInClass((int)a.classValue(), (int)b.classValue(),(int)c.classValue() );
                if ( clValue != -1){
                    double  ap = 0;
                    NbrAnal_ssss = 0; NbrAnal_sstt =0;  NbrAnal_stst =0;
                    Enumeration<Attribute> attributes = instances.enumerateAttributes();
                    while (attributes.hasMoreElements()) {
                        Attribute attribute = attributes.nextElement();
                        double attrP=0;
                        if (!IsNumericData)
                        {attrP = Functions.AnalogyNominal(a.stringValue(attribute), b.stringValue(attribute), c.stringValue(attribute), d.stringValue(attribute));
                             if (Functions.Analogy1(a.stringValue(attribute), b.stringValue(attribute), c.stringValue(attribute), d.stringValue(attribute)))
                                NbrAnal_ssss ++;
                             else if (Functions.Analogy2(a.stringValue(attribute), b.stringValue(attribute), c.stringValue(attribute), d.stringValue(attribute)))
                                NbrAnal_sstt ++;
                             else  if (Functions.Analogy3(a.stringValue(attribute), b.stringValue(attribute), c.stringValue(attribute), d.stringValue(attribute)))
                                NbrAnal_stst ++;
                        }
                        else     
                         attrP = Functions.Analogy( a.value(attribute), b.value(attribute), c.value(attribute), d.value(attribute));
                       ap += attrP;
                    }

                     ap /= instances.numAttributes() - 1;
                    
                    if(IsNumericData){//numerical case
                        sumP[clValue]+= ap ;
                    }
                    else{//nominal case
                        if ( ap >= 1) {// beta =1 for nominal
                          if(NbrAnal_ssss == Nbr_ssss )//use only triplet with  Nbr_ssss = N
                          { sumP[clValue]++;
                          }  
                        }  
                    }
                  
                   if (bestP[clValue] < ap) {
                        bestP[clValue] = ap;
                        vote[clValue] = 1;
                   } else if (bestP[clValue] == ap) {
                        vote[clValue]++;
                    }
                }
            }
          }
        }
              
System.out.print(" ScoreP: ");  
Functions.Display(sumP);
 
if ((Functions.max(sumP)!=0)& Functions.unique(Functions.max(sumP),sumP)){ //add unique// classified with this beta
         prediction = Functions.superior(sumP, bestP, vote);
         System.out.println("  ---> reel "+ d.classValue() + " predicted "+ prediction);
           System.out.println("  " );
}
//not classified cases     
if(prediction==-1){
    System.out.println("NOT CLASSIFIED!  : "+ Nbr_ssss  +" high ");
   System.out.println("  ---> reel "+ d.classValue() + " predicted "+ prediction);
   Nbr_ssss --;
}
else if (prediction!=(int) d.classValue())  System.out.println(" Wrong classification !");
}
return new int[]{(int) d.classValue(), prediction};
}
    

//kAC-Classifier: Bounhas et Prade, IJAR2023
//with OPTIMAl k and OPTIMAL SSSS_ATTR 

int[] kAC_SSSSAttr(Instance d, Instances nearest_d, int optk) {
int nbClasses = instances.numClasses();
double[] sumP = new double[nbClasses];
double[] bestP = new double[nbClasses];
double[] vote = new double[nbClasses];
double NbrAnal_ssss=0;//ssss
double  NbrAnal_sstt=0;// sstt
double  NbrAnal_stst=0;// stst

System.out.println( d.toString());  
//Compute the proportion of successful voter for THIS d
ComputePropSuccTriplet_kAC(d , nearest_d, optk) ;

int prediction = -1;
Nbr_ssss = ApplicationGUI.getNbr_ssss(); 

while (prediction == -1 & Nbr_ssss >=0){
  System.out.println("Nbr_ssss= "+ Nbr_ssss  );

  for (int nn = 0; nn < optk ; nn++) {

    Instance c = nearest_d.get(nn);

    for (int i = 0; i < instances.size()-1; i++) {

        Instance a = instances.instance(i);

        for (int j = i+ 1; j < instances.size(); j++) {

            Instance b = instances.instance(j);
            
            int clValue = Functions.AnalogyInClass((int)a.classValue(), (int)b.classValue(),(int)c.classValue() );

            if ( clValue != -1){

            double ap = 0;
            NbrAnal_ssss = 0; NbrAnal_sstt =0;  NbrAnal_stst =0;
            Enumeration<Attribute> attributes = instances.enumerateAttributes();
            while (attributes.hasMoreElements()) {
                Attribute attribute = attributes.nextElement();
                double attrP=0;
                if (!IsNumericData)
                        {attrP = Functions.AnalogyNominal(a.stringValue(attribute), b.stringValue(attribute), c.stringValue(attribute), d.stringValue(attribute));
                             if (Functions.Analogy1(a.stringValue(attribute), b.stringValue(attribute), c.stringValue(attribute), d.stringValue(attribute)))
                                NbrAnal_ssss ++;
                             else if (Functions.Analogy2(a.stringValue(attribute), b.stringValue(attribute), c.stringValue(attribute), d.stringValue(attribute)))
                                NbrAnal_sstt ++;
                             else  if (Functions.Analogy3(a.stringValue(attribute), b.stringValue(attribute), c.stringValue(attribute), d.stringValue(attribute)))
                                NbrAnal_stst ++;
                        }
                        else     
                         attrP = Functions.Analogy(
                                a.value(attribute), b.value(attribute), c.value(attribute), d.value(attribute));
                ap += attrP;
            }

            ap /= instances.numAttributes() - 1;
           
        if (!IsNumericData){ //nominal 
           if ( ap >= 1)
           {if(NbrAnal_ssss == Nbr_ssss ) //N=Nbr_ssss
                        { 
                            sumP[clValue]+= ap;//sum of all ap
                          }  
             }
        }
        else//numerical
        {
            sumP[clValue]+= ap; 
        } 

        if (bestP[clValue] < ap) {
        bestP[clValue] = ap;
        vote[clValue] = 1;
        } else if (bestP[clValue] == ap) {
        vote[clValue]++;
        }

        }

        }//inner for
    }//end outer for
}//end nn

//classification   
System.out.print(" ScoreP: ");  
Functions.Display(sumP);
if ((Functions.max(sumP)!=0)& Functions.unique(Functions.max(sumP),sumP)){ //add unique// classified with this beta
     prediction = Functions.superior(sumP , bestP , vote );
     System.out.println("  ---> reel "+ d.classValue() + " predicted "+ prediction);
}
if(prediction == -1){
    System.out.println("NOT CLASSIFIED!  : "+ Nbr_ssss  +" high ");
    System.out.println("  ---> reel "+ d.classValue() + " predicted "+ prediction);
   Nbr_ssss --;
}
else if (prediction!=(int) d.classValue())  System.out.println(" Wrong classification !");

}
System.out.println("  " );
return new int[]{(int) d.classValue(), prediction};
}  

//kAC-Classifier: Bounhas et al, IJAR2017
//with OPTIMAl k: 
int[] kAC(Instance d, Instances nearest_d, int optk) {
int nbClasses = instances.numClasses();
double[] sumP = new double[nbClasses];
double[] bestP = new double[nbClasses];
double[] vote = new double[nbClasses];
System.out.println( d.toString());  
           
for (int nn = 0; nn < optk ; nn++) {

 Instance c = nearest_d.get(nn);

    for (int i = 0; i < instances.size()-1; i++) {

        Instance a = instances.instance(i);

        for (int j = i+ 1; j < instances.size(); j++) {

            Instance b = instances.instance(j);
            
            int clValue = Functions.AnalogyInClass((int)a.classValue(), (int)b.classValue(),(int)c.classValue() );

            if ( clValue != -1){
            double ap = 0;
            Enumeration<Attribute> attributes = instances.enumerateAttributes();
            while (attributes.hasMoreElements()) {
                Attribute attribute = attributes.nextElement();
                double attrP=0;
                if (!IsNumericData)
                { attrP = Functions.AnalogyNominal(a.stringValue(attribute), b.stringValue(attribute), c.stringValue(attribute), d.stringValue(attribute));
                   }
                else     
                     attrP = Functions.Analogy(  a.value(attribute), b.value(attribute), c.value(attribute), d.value(attribute));
                ap += attrP;

            }

            ap /= instances.numAttributes() - 1;
            // Compute NbrGoodPredictor for each d 
          
        if (!IsNumericData){ //nominal
    
           if ( ap >= 1)
           { 
             sumP[clValue]+= ap;//sum of alla ap
                       
            }
        }
        else//numerical
        {
            sumP[clValue]+= ap;//sum of all ap
        } 

        if (bestP[clValue] < ap) {
        bestP[clValue] = ap;
        vote[clValue] = 1;
        } else if (bestP[clValue] == ap) {
        vote[clValue]++;
        }
        }

        }//end inner for

    }//end outer for
}//end nn

System.out.print(" ScoreP: ");  
Functions.Display(sumP);
int prediction=-1;
//classification    
if ((Functions.max(sumP)!=0)& Functions.unique(Functions.max(sumP),sumP)){ //add unique// classified with this beta
  
    prediction = Functions.superior(sumP , bestP , vote );
     System.out.println("  ---> reel "+ d.classValue() + " predicted "+ prediction);
   
}
else {  //classify with BestP
prediction = Functions.superior(sumP , bestP , vote );
System.out.println("Classify with BestP ---> reel "+ d.classValue() + " predicted "+ prediction);
}
if(prediction==-1){
    System.out.println("NOT CLASSIFIED !");
   System.out.println("  ---> reel "+ d.classValue() + " predicted "+ prediction);
}
else if (prediction!=(int) d.classValue())  System.out.println(" Wrong classification !");

 System.out.println("  " );
return new int[]{(int) d.classValue(), prediction};

}  


public void ComputePropSuccTriplet_kAC ( Instance d, Instances nearest_d, int optk) {
int nbClasses = instances.numClasses();
double NbrAnal_ssss=0;//ssss
double  NbrAnal_sstt=0;// sstt
double  NbrAnal_stst=0;// stst
PropGoodPredictor_d = new double [NbrAttr+1];
TotalNbrPredictor_d = new int [NbrAttr+1];

for (int x=0; x< NbrAttr +1; x++) PropGoodPredictor_d[x]=0;//init 
for (int x=0; x< NbrAttr +1; x++) TotalNbrPredictor_d[x]=0;//init   
 
Nbr_ssss = ApplicationGUI.getNbr_ssss(); 
System.out.println("Nbr_ssss= "+ Nbr_ssss  );

  for (int nn = 0; nn < optk ; nn++) {

    Instance c = nearest_d.get(nn);

    for (int i = 0; i < instances.size()-1; i++) {

        Instance a = instances.instance(i);

        for (int j = i+ 1; j < instances.size(); j++) {

            Instance b = instances.instance(j);
            int clValue = Functions.AnalogyInClass((int)a.classValue(), (int)b.classValue(),(int)c.classValue() );
            if ( clValue != -1){
            double ap = 0;
            NbrAnal_ssss = 0; NbrAnal_sstt =0;  NbrAnal_stst =0;
            Enumeration<Attribute> attributes = instances.enumerateAttributes();
            while (attributes.hasMoreElements()) {
                Attribute attribute = attributes.nextElement();
                if (!IsNumericData)
                             if (Functions.Analogy1(a.stringValue(attribute), b.stringValue(attribute), c.stringValue(attribute), d.stringValue(attribute)))
                                NbrAnal_ssss ++;
                             else if (Functions.Analogy2(a.stringValue(attribute), b.stringValue(attribute), c.stringValue(attribute), d.stringValue(attribute)))
                                NbrAnal_sstt ++;
                             else  if (Functions.Analogy3(a.stringValue(attribute), b.stringValue(attribute), c.stringValue(attribute), d.stringValue(attribute)))
                                NbrAnal_stst ++;
                }
            // Compute NbrGoodPredictor for each d 
                for (int att = 0; att <= NbrAttr; att++){
                   if(NbrAnal_ssss == att){
                       TotalNbrPredictor_d[att]++;
                       if(clValue == ((int) d.classValue())) {
                             PropGoodPredictor_d[att]++;
                        }
                     }
                }
            }
        }
    }
}
//normalization
       System.out.println("NbrGoodPredictor "+ Arrays.toString(PropGoodPredictor_d));
       System.out.println("TotalNbrPredictor "+ Arrays.toString( TotalNbrPredictor_d));
        
        for (int x=0; x< NbrAttr+1; x++) 
          if(TotalNbrPredictor_d[x]!=0) PropGoodPredictor_d[x]= Math.rint(100*PropGoodPredictor_d[x]/TotalNbrPredictor_d[x])/100;
          else PropGoodPredictor_d[x]=0;
        
        System.out.println("Proportion GoodPredictor_d "+ Arrays.toString(PropGoodPredictor_d));
 }

public void ComputePropSuccTriplet_Baseline (Instance d ) {
double NbrAnal_ssss=0;//ssss
double  NbrAnal_sstt=0;// sstt
double  NbrAnal_stst=0;// stst
PropGoodPredictor_d = new double [NbrAttr+1];
TotalNbrPredictor_d = new int [NbrAttr+1];

for (int x=0; x< NbrAttr +1; x++) PropGoodPredictor_d[x]=0;//init 
for (int x=0; x< NbrAttr +1; x++) TotalNbrPredictor_d[x]=0;//init    
Nbr_ssss = ApplicationGUI.getNbr_ssss(); 

  System.out.println("Nbr_ssss= "+ Nbr_ssss  );

        for (int i = 0; i < instances.size() - 2; i++) {
            Instance a = instances.instance(i);
            for (int j = i + 1; j < instances.size()-1; j++) {
                Instance b = instances.instance(j);
                  for (int k = j + 1; k < instances.size(); k++) {
                        Instance c = instances.instance(k);
               
                int clValue = Functions.AnalogyInClass((int)a.classValue(), (int)b.classValue(),(int)c.classValue() );
                if ( clValue != -1){
                    NbrAnal_ssss = 0; NbrAnal_sstt =0;  NbrAnal_stst =0;
                    Enumeration<Attribute> attributes = instances.enumerateAttributes();
                    while (attributes.hasMoreElements()) {
                        Attribute attribute = attributes.nextElement();
                        if (!IsNumericData)
                        {   if (Functions.Analogy1(a.stringValue(attribute), b.stringValue(attribute), c.stringValue(attribute), d.stringValue(attribute)))
                                NbrAnal_ssss ++;
                             else if (Functions.Analogy2(a.stringValue(attribute), b.stringValue(attribute), c.stringValue(attribute), d.stringValue(attribute)))
                                NbrAnal_sstt ++;
                             else  if (Functions.Analogy3(a.stringValue(attribute), b.stringValue(attribute), c.stringValue(attribute), d.stringValue(attribute)))
                                NbrAnal_stst ++;
                        }
                        
                    }
                // Compute NbrGoodPredictor for each d 
                for (int att = 0; att <= NbrAttr; att++){
                   if(NbrAnal_ssss == att){
                       TotalNbrPredictor_d[att]++;
                       if(clValue == ((int) d.classValue())) {
                             PropGoodPredictor_d[att]++;
                        }
                    }
                     
                }
                    
                }
            }//end for
          }//end for
        }//end for
        
        //normalization
       System.out.println("NbrGoodPredictor "+ Arrays.toString(PropGoodPredictor_d));
       System.out.println("TotalNbrPredictor "+ Arrays.toString( TotalNbrPredictor_d));
        
        for (int x=0; x< NbrAttr+1; x++) 
            if(TotalNbrPredictor_d[x]!=0) PropGoodPredictor_d [x]= Math.rint(100*PropGoodPredictor_d[x]/TotalNbrPredictor_d[x])/100;
            else PropGoodPredictor_d[x]=0;
        
        System.out.println("Proportion GoodPredictor_ d "+ Arrays.toString(PropGoodPredictor_d ));
}

}  


