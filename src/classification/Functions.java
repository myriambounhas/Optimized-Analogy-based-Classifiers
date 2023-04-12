/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classification;

import java.util.Arrays;
import java.util.Enumeration;
import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ManhattanDistance;
import weka.core.neighboursearch.LinearNNSearch;

/**
 *
 * @author Myriam Bounhas
 */
public class Functions {

   public static int superior(double[] sump, double[] bestp, double[] vote) {
        boolean[] sumpArgmax = new boolean[sump.length];
        boolean[] bestpArgmax = new boolean[bestp.length];
        int supi = -1, occ = 0;
        double sup = -1;
        for (int i = 0; i < sump.length; i++) {
            // System.out.print("  sump: "+ sump[i]);
            if (sump[i] > sup) {
                sup = sump[i];
                supi = i;
                Arrays.fill(sumpArgmax, false);
                sumpArgmax[i] = true;
                occ = 1;
            } else if (sump[i] == sup) {
                sumpArgmax[i] = true;
                occ++;
            }
        }
        // System.out.println();
         
        if (occ > 1) {
            supi = -1;
            sup = -1;
            occ = 0;
            for (int i = 0; i < bestp.length; i++) {
                if (sumpArgmax[i] && bestp[i] > sup) {
                    sup = bestp[i];
                    supi = i;
                    Arrays.fill(bestpArgmax, false);
                    bestpArgmax[i] = true;
                    occ = 1;
                } else if (sumpArgmax[i] && bestp[i] == sup) {
                    bestpArgmax[i] = true;
                    occ++;
                }
            }
        }
        if (occ > 1) {
            supi = -1;
            sup = -1;
            occ = 0;
            for (int i = 0; i < vote.length; i++) {
                if (bestpArgmax[i] && vote[i] > sup) {
                    sup = vote[i];
                    supi = i;
                }
            }
        }
        return supi;
    }

    public static int maxIdx(int[] tab) {
        int sup = tab[0], supi = 0;
        for (int i = 1; i < tab.length; i++) {
            if (tab[i] > sup) {
                sup = tab[i];
                supi = i;
            }
        }
        return supi;
    }
     
    public static int maxIdx(double[] tab) {
        double sup = tab[0];
        int supi = 0;
        for (int i = 1; i < tab.length; i++) {
            if (tab[i] > sup) {
                sup = tab[i];
                supi = i;
            }
        }
        return supi;
    }
    
     public static double max(double[] tab) {
        double sup = tab[0];
        
        for (int i = 1; i < tab.length; i++) {
            if (tab[i] > sup) {
                sup = tab[i];
                
            }
        }
        return sup;
    }
      public static double min(double[] tab) {
        double inf = tab[0];
        
        for (int i = 1; i < tab.length; i++) {
            if (tab[i] < inf) {
                inf = tab[i];
                
            }
        }
        return inf;
    }
    public static int LastMaxIdx(double[] tab) {
        double sup = tab[0];
        int supi = 0;
        for (int i = 1; i < tab.length; i++) {
            if (tab[i] >= sup) {
                sup = tab[i];
                supi = i;
            }
        }
        return supi;
    }

      
    public static boolean unique(double max, double[] tab) {
        int occ = 0;
        for (int i = 0; i < tab.length; i++) {
            if (max == tab[i]) {
                occ++;
            }
        }
        return (occ == 1);
    }

 
       
    public static double accuracy(int[][] confusionMatrix) {
        int total = 0, success = 0;
        for (int i = 0; i < confusionMatrix.length; i++) {
            for (int j = 0; j < confusionMatrix[0].length; j++) {
                total += confusionMatrix[i][j];
                if (i == j) {
                    success += confusionMatrix[i][j];
                }
            }
        }
        return 1.0 * success / total;
    }

    
   
    public static double recall(int cl_value, int[][] confusionMatrix) {
        int truePositive = confusionMatrix[cl_value][cl_value];
        int falseNegative = 0;
        for (int i = 0; i < confusionMatrix[cl_value].length; i++) {
            if (i != cl_value) {
                falseNegative += confusionMatrix[cl_value][i];
            }
        }
        return 1.0 * truePositive / (truePositive + falseNegative);
    }

    public static double precision(int cl_value, int[][] confusionMatrix) {
        int truePositive = confusionMatrix[cl_value][cl_value];
        int falsePositive = 0;
        for (int i = 0; i < confusionMatrix[0].length; i++) {
            if (i != cl_value) {
                falsePositive += confusionMatrix[i][cl_value];
            }
        }
        return 1.0 * truePositive / (truePositive + falsePositive);
    }

    public static double f1score(int cl_value, int[][] confusionMatrix) {
        double recall = recall(cl_value, confusionMatrix);
        double precision = precision(cl_value, confusionMatrix);

        return 2 * precision * recall / (precision + recall);
    }

       
   
   //type s:s::s:s   
  public static Boolean Analogy1(String a ,String b,String c, String d)
  {

   if ((a.compareTo(b)==0) & (b.compareTo(c)==0)& (c.compareTo(d)==0))   return true;
   else  
    return false;

  }  
  //type s:s::t:t
  public static Boolean Analogy2(String a ,String b,String c, String d)
  {

   if ((a.compareTo(b)==0) & (c.compareTo(d)==0))   return true;
   else  
    return false;

  } 
    //type s:t::s:t
  public static Boolean Analogy3(String a ,String b,String c, String d)
  {

   if ( ((a.compareTo(c)==0) & (b.compareTo(d)==0) ))   return true;
   else  
    return false;

  } 
  public static double AnalogyNominal(String a ,String b,String c, String d)
  {

   double A = 0;
   if (((a.compareTo(b)==0) & (b.compareTo(c)==0)& (c.compareTo(d)==0)) ||
       ((a.compareTo(c)==0) & (b.compareTo(d)==0) )||
       ((a.compareTo(b)==0) & (c.compareTo(d)==0) )) A=1;
   else A=0;
   if(A<0||A>1)  System.out.println("OUT OF BOUND EXCEPTION  A: "+ A);
    return A;

  }
  
  public static double AnalogyNominalExtend(double a ,double b,double c, double d)
  {
   double A = 0;
   
   if((a-b)==(c-d)) A=1;
   else A=0;
   
   if(A<0||A>1)  System.out.println("OUT OF BOUND EXCEPTION  A: "+ A);
   return A;
  }
   public static int AnalogyInClass(int a , int c )
  {
   int  b = -1;
   if (a==c) b=a;
   else if (((a+c)%2)==0)
       b=(a+c)/2 ;
    return b;

  }
   
    public static int AnalogyInClass(int a , int b, int c )
  {
   if ( a==b)  return c;
   else if (a==c) return  b;
   
    else
      return -1;

  }

 public  double  AnalogyEtoile(double a , double b, double c, double d)
  {

   double AStar= 1;

//missing attributes are coded -1 in voting dataset
if((a==-1) || (b==-1)||(c==-1)||(d==-1)) AStar =0 ;
 else
 {
   AStar= min(1-Math.abs(max(a,d)-max(b,c)),1-Math.abs(min(a,d)-min(b,c)));

 }
    return AStar;

  }
 public static double  Analogy (double a , double b, double c, double d)
  {

 double A = 1;

//missing attributes are coded -1 in voting dataset
if((a==-1) || (b==-1)||(c==-1)||(d==-1)) A =0 ;
 else
 {

   if ( ( (a >= b) & (c >= d)) || ( (a <= b) & (c <= d))) {
     A = 1 - Math.abs( (a - b) - (c - d));

   }

   else

   if ( ( (a <= b) & (c >= d)) || ( (a >= b) & (c <= d))) {
     double v1 = Math.abs(a - b);
     double v2 = Math.abs(c - d);
     double max;
     if (v1 >= v2) {
       max = v1;
     }
     else {
       max = v2;
     }

    A = 1 - max;

   }
 }

 if(A<0||A>1) 
{ //System.out.println("OUT OF BOUND EXCEPTION  A: "+ A);
 //WrongA=true;
} 
    return A;

  }
    
    public static double  max (double a , double b)
    {
      double max;

      if (a > b)
        max = a;
        else max= b;

        return max;
    }
    

 public static double  min (double a , double b)
  {
    double min;

    if (a < b)
      min = a;
      else min= b;
      return min;
  }
 

   public static void Display (double[] Tab) {

    for (int i = 0; i < Tab.length; i++) {
      System.out.printf("   %.3f" , Tab[i] );
    
    }
   }
 public static double[] reverse (double[] Tab) {
    double[] rTab= new double[Tab.length];
    for (int i = 0; i < Tab.length; i++) {
     rTab[i]= Tab[Tab.length-1-i];
    
    }

   // System.out.println("\n");
return rTab;
  }
  
    public static int GetIdxBest(double[][] sumP){
        int Idxbest=0;
        double maxmax=0;
        for (int cl=0; cl < sumP.length; cl++) {
             int max = maxIdx(sumP[cl]);
             if(sumP[cl][max]> maxmax ) 
             { maxmax = sumP[cl][max];
              Idxbest= cl;
             }
         }
        return Idxbest;
    }

public  static Instances [] getNearestNeighbors (Instances validators, Instances Trainers, int k){
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

    
}

