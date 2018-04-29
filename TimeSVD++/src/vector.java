import java.util.*;

import Mypackage.Data_Line;

public class vector {

	    public static void main(String[] args) {
	    	
	    	int RAND_MAX = 32767;
	    	Random rand = new Random();
	    	
	    	System.out.println("RANDOM :" + (double)(rand.nextInt(RAND_MAX))/RAND_MAX);

	    	Data_Line[] data = new Data_Line[6000];
	    	
	    	ArrayList<Data_Line>[] data_line = new ArrayList[1000];
	    	
	    	
	     data[0] = new Data_Line();
	     data[0].setVal(12, 23, 34, 56);
	     data[0].setVal(12, 23, 34, 56);
	     data[0].setVal(12, 545445454, 34, 56);
	     data[0].setVal(12, 23, 34, 56);
	     data[0].setVal(12, 23, 34, 56);
	     
	     data[1] = new Data_Line();
	     data[1].setVal(12, 23, 34, 56);
	     data[1].setVal(12, 23, 34, 56);
	     data[1].setVal(12, 23212321, 34, 56);
	     data[1].setVal(12, 23, 34, 56);
	     data[1].setVal(12, 23, 34, 56);
	     
	     System.out.println(data[0].getLength());
	     
	     System.out.println(data[1].getLength());
	     
	     System.out.println(data[0].getItem(2));
	     
	     System.out.println(data[1].getItem(2));
	     
	      
	      Vector<HashMap<Integer, Double>> theVector= new Vector<HashMap<Integer, Double>>();
	      
	      theVector.addElement(new HashMap<Integer, Double>());
	      
	      
	      
	      theVector.get(0).put(4, (double) 7);
	      theVector.get(0).put(5, (double) 4);
	      theVector.get(0).put(1, (double) 8);
	      theVector.get(0).put(2, (double) 2);
	      theVector.get(0).put(6, (double) 5);
	      theVector.get(0).put(8, (double) 6);
	      
	     
	      
	      
	      
	      HashMap<Integer, Double> tmp = new HashMap<Integer, Double>();
	      
	      tmp.put(1, 4.545454);
	      
	      theVector.add(5, tmp);
	      
	      System.out.println(theVector.get(5).get(1));
	      
	      
	      tmp.put(2, 4.5);
	      tmp.put(3, 4.2);
	      tmp.put(4, 4.6);
	      
	      theVector.add(tmp);
	      
	      System.out.println("aa" + (theVector.get(1).get(123) == null));
	      
	      System.out.println(tmp.get(0) == null);
	      System.out.println(tmp.isEmpty());
	      
	      
	      System.out.println(theVector.get(0).size());
	      
	      System.out.println(theVector.get(0).get(4));
	      System.out.println(theVector.size());
	      
	    	
	    	
	    }
	}
