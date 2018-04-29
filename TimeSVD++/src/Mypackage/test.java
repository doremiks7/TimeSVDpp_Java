package Mypackage;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

public class test {

	public static void main(String []args) {
		Random rand = new Random();
		int r = rand.nextInt(100) + 1;
		System.out.println(r);
	    System.out.println(r == 0);
	    
	    Vector<HashMap<Integer, Double>> Dev = new Vector<HashMap<Integer, Double>>();
	    
	    for(int i=0;i<100;i++){
	        HashMap<Integer,Double> tmp = new HashMap<Integer, Double>();
	        Dev.add(i, tmp);
	    }
	    
	    int timeArg = 3;
	    double tmp = 4.6;
	    
	    Dev.get(0).put(timeArg, tmp);
	    
	    int [] bu = null;
//	    bu[1] = 2;
	    
	    double [][]sumMW = new double[10][];
	    
	    sumMW[0] = new double[2];
	    
	    sumMW[0][0] = 1;
	    sumMW[0][1] = 2;
	   
	    System.out.println(sumMW[0][1]);
	    
	    
	    System.out.println("bu : " + (bu == null));
	    
	    System.out.println(Dev.get(10).get(timeArg));
	    
	    System.out.println(Math.pow(2, 3));
	    
	    
	}
	
}
