/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ManhattanDistance;
import weka.core.neighboursearch.LinearNNSearch;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

/**
 *
 * @author Myriam Bounhas
 */
public class Validation {

    private Instances instances;
    private int nbrClass;
    private int NbrAttr;
    private final int RAND_SEED = 4269;
    private double avgBeta = 0;
    private double avgPairTriple = 0;
    private double avgCompPair= 0;
    private double avgRF = 0;
    private float FinalTimeTrTs= 0;
    private float FinalTimeTs = 0;
    private double avgBestTriple= 0;
    private double avgSeuilBeta=0;
    public static Boolean IsNumericData = false;
    private double meanStd = 0;
    private double stdThisRun=0;
  
    public static double SeuilBeta = 0;
    public static double [] PropGoodPredictor_d ; 
    public static int [] TotalNbrPredictor_d ; 
     public static double [] PropGoodPredictor ; 
     private double avgOptk = 0;
      private double avgUnclassified = 0;
    
      public Validation(Instances instances) {
        this.instances = instances;
       
        Attribute classAttr = instances.attribute("class");
        instances.setClass(classAttr);
       this.nbrClass=classAttr.numValues();
       this.IsNumericData = instances.get(0).attribute(0).isNumeric();
       NbrAttr = instances.numAttributes()-1;
             
    }

    public double getStd() {
        return this.meanStd;
    }
    public double getAvgBeta() {
        return this.avgBeta;
    }
     public double getOptimalK() {
        return this.avgOptk;
    }
      public double getPropoUnclassified() {
        return this.avgUnclassified;
    }
      public double getAvgSeuilBeta() {
        return this.avgSeuilBeta;
    }
   
  public double getAvgRF() {
        return this.avgRF;
    }
     
    
// returns training + classification time
 public double getRunTimeTrTs() {
        return this.FinalTimeTrTs;
    }
// returns classification time
 public double getRunTimeTs() {
        return this.FinalTimeTs;
    }
    

//============================================ CV No PARAMETERs ===============================================
    private int[][] kfcv(int algo, int k) {
        instances.randomize(new Random(RAND_SEED));
        int nbCl = instances.classAttribute().numValues();
        int[][] confusionMatrix = new int[nbCl][nbCl];
        int rest = instances.size() % k;
        int elemsByFold = instances.size() / k;
        int iStart = 0, iEnd = elemsByFold ;
        double accuracy = 0; double unclassified = 0;
        double[] tabRel = new double[k];
        Instances validators = instances.stringFreeStructure();
        Instances trainers = instances.stringFreeStructure();
        avgUnclassified=0;
        for(int numFold=0; numFold <k; numFold++){
            System.out.println("fold " + numFold + " in process");
            if (rest > 0) {
                iEnd++;
            }
            for (int i = 0; i < instances.size(); i++) {
                if (iStart <= i && i < iEnd) {
                    validators.add(instances.get(i));
                } else {
                    trainers.add(instances.get(i));
                }
            }
            rest--;
            iStart = iEnd;
            iEnd += elemsByFold;
            double foldAccuracy = 0, foldRF =0;double foldUnclassified = 0;
            try {
                Instances scaledTrainers = trainers , scaledValidators= validators;
                if(IsNumericData){// if numeric data ---> rescale data
                Normalize norm = new Normalize();
                //norm Trainers
                norm.setInputFormat(trainers);
                scaledTrainers = Filter.useFilter(trainers, norm);
                 //norm Validator
                scaledValidators= instances.stringFreeStructure();
                 for (int i = 0; i < validators.size(); i++) {
                    norm.input(validators.get(i));
                    Instance scaledTest = norm.output();
                    scaledValidators.add(scaledTest);
                }
                }
                Classifier classifier = new Classifier(scaledTrainers);
                int classified = 0;
                //validation
                for (int i = 0; i < scaledValidators.size(); i++) {
                    System.out.print ("d"+(i+1)+":");
                   Instance scaledTest = scaledValidators.get(i);
                    int[] res = new int[2];
                    if(algo==0)
                        res = classifier.Baseline(scaledTest);
                    else if(algo==1)
                        res = classifier.Baseline_SSSSAttr(scaledTest);
                    
                   if(res[1]!=-1) {
                       confusionMatrix[res[0]][res[1]]++;
                       classified ++;
                   } 
                   else foldUnclassified += 1.0 / validators.size(); //unclassified case
                   
                    if (res[0] == res[1]) {
                        foldAccuracy += 1.0 ;
                    }
                   if(algo==1) for (int x=0; x< NbrAttr +1; x++) PropGoodPredictor[x] += PropGoodPredictor_d[x]  ; 
                    
                }
                foldAccuracy /= classified ;
              //  System.out.println("fold accuracy= " + foldAccuracy + " classified= "+ classified + " unclassified= "+ (validators.size()- classified) + " foldUnclassified= "+ foldUnclassified);
             
            } catch (Exception e) {
            }
            System.out.printf("fold %d accuracy= %.2f \n", numFold, foldAccuracy * 100);
            System.out.printf("fold %d unclassified = %.2f \n", numFold, foldUnclassified * 100);
            accuracy += foldAccuracy/k;
            unclassified += foldUnclassified/k;
            tabRel[numFold] = foldAccuracy;
            validators.clear();
            trainers.clear();
        }//end fold
        
//compute variance ----------------------
        double SumSq = 0;
        double Val;
        for (int i = 0; i < k; i++) {
            Val = Math.abs(tabRel[i] - accuracy);
            SumSq = SumSq + Val * Val;
        }
        stdThisRun = SumSq / k;
        stdThisRun = Math.sqrt(stdThisRun);
        avgUnclassified = unclassified;
        
        System.out.printf("run accuracy = %.2f+/-%.2f \n", accuracy * 100, stdThisRun*100 );
        System.out.printf("run unclassified = %.2f \n", unclassified * 100 );
       
        return confusionMatrix;
    }
    
//============================================ CV WITH  PARAMETERs to optimize ===============================================
    private double innerAPCV(int algo, Instances trainingSet, int fold, int param) {
        int rest = trainingSet.size() % fold;
        int elemsByFold = trainingSet.size() / fold;
        int iStart = 0, iEnd = elemsByFold, numFold = 0;
        double accuracy = 0;
        Instances trainers = instances.stringFreeStructure();
        Instances validators = instances.stringFreeStructure();
      
        while (numFold++ < fold) {
            if (rest > 0) {
                iEnd++;
            }
            for (int i = 0; i < trainingSet.size(); i++) {
                if (iStart <= i && i <= iEnd) {
                    validators.add(trainingSet.get(i));
                } else {
                    trainers.add(trainingSet.get(i));
                }
            }
            rest--;
            iStart = iEnd;
            iEnd += elemsByFold;
            double foldAcc = 0;
                      
            try {
                Instances scaledTrainers = trainers , scaledValidators= validators;
                
                // if numeric data ---> rescale data
                if(IsNumericData){
                Normalize norm = new Normalize();
                //norm Trainers
                norm.setInputFormat(trainers);
                scaledTrainers = Filter.useFilter(trainers, norm);
                 //norm Validator
                scaledValidators = instances.stringFreeStructure();
                 for (int i = 0; i < validators.size(); i++) {
                    norm.input(validators.get(i));
                    Instance scaledTest = norm.output();
                    scaledValidators.add(scaledTest);
                }
                }
             
                Classifier classifier = new Classifier(scaledTrainers);
              Instances [] nearest_d = Functions.getNearestNeighbors(scaledValidators, scaledTrainers, param);//param is k
                   //  System.out.println(" param " +  param );
                 // System.out.println(" NN size" +  nearest_d[0].size()+ " nn2  "+  nearest_d[1].size() +  " nn3  "+ nearest_d[2].size() );
                for (int i = 0; i < scaledValidators.size(); i++) {
                   Instance scaledTest = scaledValidators.get(i);
                    int[] res = new int[2];
                    res = classifier.kAC(scaledTest, nearest_d[i],param);
                    
                    if (res[0] == res[1]) {
                        foldAcc += 1.0 / validators.size();
                    }
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            
        System.out.println("inner fold accuracy: " + foldAcc);
            accuracy += foldAcc / fold;
            validators.clear();
            trainers.clear();
        }
     System.out.println("accuracy = " + accuracy);
        return accuracy;
    }
    
    //========================== CV WITH  PARAMETER k(k is the nbr of nearest neighbors) to optimize ===============================================
     public int[][] outerAPCV(int algo, int fold) {
        int [] parameters = {1, 3, 5, 7, 9, 11, 15,17,19,21}; //param k
        double[] innerAccuracies = new double[parameters.length];
        double accuracy = 0;
        double unclassified = 0;
        instances.randomize(new Random(RAND_SEED));
        int nbCl = instances.classAttribute().numValues();
        int[][] confusionMatrix = new int[nbCl][nbCl];
        int rest = instances.size() % fold;
        int elemsByFold = instances.size() / fold;
        int iStart = 0, iEnd = elemsByFold ;
        Instances validators = instances.stringFreeStructure();
        Instances trainers = instances.stringFreeStructure();
        double[] tabRel = new double[fold];
        avgOptk = 0; avgUnclassified=0;
        float elapsedTime = 0; 
        for(int numFold=0; numFold <fold; numFold++){
            System.out.println("fold " + numFold + " in process");
            if (rest > 0) {
                iEnd++;
            }
            for (int i = 0; i < instances.size(); i++) {
                if (iStart <= i && i < iEnd) {
                    validators.add(instances.get(i));
                } else {
                    trainers.add(instances.get(i));
                }
            }
                     
            rest--;
            iStart = iEnd;
            iEnd += elemsByFold;
             int opk;
            //loop to optimize param: k
            //Alternative 1: use the below code to optimize k using the inner CV
            //-----------------------------------------------------------------------
            /*
            for (int i = 0; i < parameters.length; i++) {
                System.out.println("Param: " + parameters[i]);
                innerAccuracies[i] = innerAPCV(algo, trainers, 5, parameters[i]);
            }
            System.out.println("innerAccuracies = " + Arrays.toString(innerAccuracies));
            int imax = Functions.maxIdx(innerAccuracies);
            opk = parameters[imax];
            */
          //-------------------------------------------------------------------------------
 
//Alternative 2: use previously optimized k --> see ApplicationGUI computed k values  ...
 opk = ApplicationGUI.getOptKnn();
       avgOptk += opk ;
       System.out.println(" fold optimal k: " + opk);
          
            double foldAcc = 0;
            double foldUnclassified = 0;
            try {
                Instances scaledTrainers = trainers , scaledValidators= validators;
                if(IsNumericData){// if numeric data ---> rescale data
                Normalize norm = new Normalize();
                //norm Trainers
                norm.setInputFormat(trainers);
                scaledTrainers = Filter.useFilter(trainers, norm);
 
                //norm Validator
                scaledValidators = instances.stringFreeStructure();
                 for (int i = 0; i < validators.size(); i++) {
                    norm.input(validators.get(i));
                    Instance scaledTest = norm.output();
                    scaledValidators.add(scaledTest);
                }
                }
                Classifier classifier = new Classifier(scaledTrainers);
                
               Instances [] nearest_d = Functions.getNearestNeighbors(scaledValidators,scaledTrainers, opk);//opk is the optimal k
                 //Classification
                // compute the prop of good triples
                long startTimeTs = System.currentTimeMillis();
                int classified = 0;
                for (int i = 0; i < validators.size(); i++) {
                    System.out.print ("d"+(i+1)+":");
                    Instance scaledTest = scaledValidators.get(i);
                    int[] res = new int[2];
                    if(algo==2)
                        res = classifier.kAC(scaledTest, nearest_d[i], opk);
                    else  if(algo==3)
                        res = classifier.kAC_SSSSAttr(scaledTest, nearest_d[i], opk);
                     if(res[1]!= -1) 
                    {confusionMatrix[res[0]][res[1]]++;
                     classified ++;
                    }
                    else  foldUnclassified += 1.0 / validators.size();//unclassified case
                    
                    if (res[0] == res[1]) {
                        foldAcc += 1.0 ;
                    }
                   if(algo==3) for (int x=0; x< NbrAttr +1; x++) PropGoodPredictor[x] += PropGoodPredictor_d[x]  ; 
                }
                foldAcc /= classified ;
              //  System.out.println("fold accuracy= " + foldAcc + " classified= "+ classified + " unclassified= "+ (validators.size()- classified) + " foldUnclassified= "+ foldUnclassified);
                //**********  compute run time
              long stopTimeTs = System.currentTimeMillis();
             elapsedTime = (stopTimeTs - startTimeTs); 
             
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
             System.out.printf("fold %d accuracy= %.2f \n", numFold, foldAcc * 100);
            System.out.printf("fold %d unclassified = %.2f \n", numFold, foldUnclassified * 100);
            accuracy += foldAcc / fold;
            unclassified += foldUnclassified / fold;
            tabRel[numFold] = foldAcc;
            FinalTimeTs +=(elapsedTime);
            validators.clear();
            trainers.clear();
        }
       //compute variance ----------------------
        double SumSq = 0;
        double Val;
        for (int i = 0; i < fold; i++) {
            Val = Math.abs(tabRel[i] - accuracy);
            SumSq = SumSq + Val * Val;
          
        }
        stdThisRun = SumSq / fold;
        stdThisRun = Math.sqrt(stdThisRun);
        avgOptk = avgOptk /fold ;
        avgUnclassified = unclassified;
        System.out.printf("run accuracy = %.2f+/-%.2f \n", accuracy * 100, stdThisRun*100 );
        System.out.printf("run unclassified = %.2f \n", unclassified * 100 );
        System.out.printf("run Optimal k = %.2f \n", avgOptk );
       
          
        return confusionMatrix;
    }

     
   public int[][] outerAPCV_multiple(int algo, int runs,  int k) {
       
       int nbCl = instances.classAttribute().numValues();
        int[][] confusion = new int[nbCl][nbCl];
        double meanOptk = 0;
        double meanPrTr=0, meanBestTriple=0 , meanSeuilBeta=0 ,meanRF=0, meanUnclassified =0;// meanCompPairs=0, 
        PropGoodPredictor = new double [NbrAttr+1]; //final
       
       for (int x=0; x< NbrAttr+1; x++) PropGoodPredictor[x]=0;
   
        // compute the run time
        long startTimeTrTs = System.currentTimeMillis();
        
        for (int i = 0; i < runs; i++) {
            stdThisRun = 0;
            System.out.println("run " + (i + 1));
            int[][] runConfusion = outerAPCV(algo, k);
            meanStd += stdThisRun;
            meanOptk += avgOptk ;
            meanUnclassified += avgUnclassified;
            meanRF += avgRF/ runs;
            for (int l = 0; l < nbCl; l++) {
                for (int c = 0; c < nbCl; c++) {
                    confusion[l][c] += runConfusion[l][c];
                }
            }
        }
        //**********  compute run time training + testing
        long stopTimeTrTs = System.currentTimeMillis();
        float elapsedTime = (stopTimeTrTs - startTimeTrTs)/(runs);// runtime for only one fold 
        FinalTimeTs/= runs;
        FinalTimeTs =   (FinalTimeTs ) ;
        FinalTimeTrTs =  (elapsedTime ) ;
      
        meanStd /= runs;
        avgUnclassified = meanUnclassified/runs;
         avgOptk = meanOptk/ runs;
         System.out.println("Optimal k= "+ avgOptk);
         for (int x=0; x < NbrAttr +1; x++) 
             PropGoodPredictor[x] = Math.rint(100*PropGoodPredictor[x]/(runs*instances.numInstances()))/100;
        if(algo==3) System.out.println("Final Proportion GoodPredictor "+ Arrays.toString(PropGoodPredictor));
        
        return confusion;
       }
   

    public int[][] kfcv_multiple(int algo, int runs, int k) {
      
        int nbCl = instances.classAttribute().numValues();
        int[][] confusion = new int[nbCl][nbCl];
        double meanBeta = 0;
        double meanPrTr=0, meanBestTriple=0 , meanSeuilBeta=0 ,meanRF=0, meanUnclassified =0;// meanCompPairs=0, 
        PropGoodPredictor = new double [NbrAttr+1]; //final
       
       for (int x=0; x< NbrAttr+1; x++)  PropGoodPredictor[x]=0;
        
// compute the execution time
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < runs; i++) {
            stdThisRun = 0;
            System.out.println("run " + (i + 1));
             int[][] runConfusion = kfcv(algo, k);
            meanStd += stdThisRun;
            meanUnclassified += avgUnclassified;
            meanBeta += avgBeta / runs;
            meanPrTr += avgPairTriple/runs;
            meanBestTriple += avgBestTriple / runs;
            meanSeuilBeta += avgSeuilBeta/runs;
            for (int l = 0; l < nbCl; l++) {
                for (int c = 0; c < nbCl; c++) {
                    confusion[l][c] += runConfusion[l][c];
                }
            }
        }
        //**********  compute run time
       long stopTime = System.currentTimeMillis();
        float elapsedTime = (stopTime - startTime)/runs; 
         FinalTimeTrTs = elapsedTime  ;
                        
        meanStd /= runs;
        avgUnclassified = meanUnclassified/runs;
        for (int x=0; x < NbrAttr +1; x++) 
            PropGoodPredictor[x] = Math.rint(100*PropGoodPredictor[x]/(runs*instances.numInstances()))/100;
       if(algo==1)  System.out.println("Final Proportion GoodPredictor "+ Arrays.toString(PropGoodPredictor));
         
    return confusion;
     
    }
    
 public  Instances [] getNearestNeighbors (Instances validators, Instances Trainers, int k){
    Instances [] nearest = new Instances [validators.size()];
   
    LinearNNSearch knn = new LinearNNSearch(Trainers);
    try {
      DistanceFunction manhattan = new ManhattanDistance(Trainers);
      knn.setDistanceFunction(manhattan);
               
        for (int i = 0; i < validators.size(); i++) {
          Instance scaledTest = validators.get(i);
          Instances nn = knn.kNearestNeighbours(scaledTest, k); 
          if(nn.contains(scaledTest)) 
          {  nn.remove(scaledTest);
           }
           nearest[i]=nn;
         }
    } catch (Exception e) {
            System.out.println(e.getMessage());
     }
    return nearest;
}
 
 public  Instances [][] getNearestNeighborsByClass (Instances validators, Instances [] instancesByClass, int nbrClass, int k){
    Instances [][] nearest = new Instances [validators.size()][nbrClass];

     
for (int cl = 0; cl <nbrClass; cl++) {
    int nbrInstInClass = instancesByClass[cl].size();
    
     LinearNNSearch knn = new LinearNNSearch(instancesByClass[cl]);
    try {
      DistanceFunction manhattan = new ManhattanDistance(instancesByClass[cl]);
      knn.setDistanceFunction(manhattan);
            for (int i = 0; i < validators.size(); i++) {
                 Instance scaledTest = validators.get(i);
                 if(k>nbrInstInClass) k=nbrInstInClass;// nbr NN bigger than inst in this class
                 nearest[i][cl] = knn.kNearestNeighbours(scaledTest, k);       
            }
        
    } catch (Exception e) {
            System.out.println(e.getMessage());
     }
 }
 return nearest;
}
 
public Instances[] getInstancesByClass(Instances instances){
     
     Instances [] instancesByClass = new Instances[nbrClass];
     
       for (int cl = 0; cl <nbrClass; cl++) {
            instancesByClass[cl] = instances.stringFreeStructure();
            for (int i = 0; i < instances.size(); i++) {
               Instance a = instances.get(i);
             
                 if((int)a.classValue()==cl){
                     instancesByClass[cl].add(a);
                }
            }
       }
    return instancesByClass;
}
 
 

}
