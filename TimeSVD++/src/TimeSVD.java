import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import Mypackage.Data_Line;


public class TimeSVD {
	
	public static final int RAND_MAX = 32767;
	public static final int userNum = 6040;  //number of users
	public static final int itemNum = 9064;   //number of items
	public static final int timeNum = 5115;    //number of days(time)
	public static final int binNum = 30;       //number of time bins
	public static final double AVG = 3.60073;  //average score
	public static double G_alpha = 0.00001;        //gamma for alpha
	public static final double L_alpha = 0.0004;   //learning rate for alpha
	public static final double L_pq = 0.015;       //learning rate for Pu & Qi
	static double G = 0.007;                //general gamma
	public static final double Decay = 0.9;        //learning rate decay factor
	public static final double L = 0.005;          //general learning rate
	public static final int factor = 50; 
	
	public double []Tu;         //variable for mean time of user
	public double []Alpha_u;
	public double []Bi;
	public double [][] Bi_Bin;
	public double []Bu;
	public Vector<HashMap<Integer, Double>> Bu_t = new Vector<HashMap<Integer, Double>>();
	public Vector<HashMap<Integer, Double>> Dev = new Vector<HashMap<Integer, Double>>();
	public double [][] Qi;
	public double [][] Pu;
	public double [][] y;
	public double [][] sumMW;   
	public String trainFile;
	public String crossFile;
	public String testFile;
	public String outFile;

    public Data_Line []train_data = new Data_Line [7000]; 
    public Data_Line []test_data = new Data_Line [6040]; 
     
	public int sign(double d)
	{
		if(d<0)
			return -1;
		else if(d>0)
			return 1;
		else
			return 0;
	}
	
	public TimeSVD(String train_file, String cross_file, String test_file, String out_file) throws IOException
	{
			Random rand = new Random();	
		    
	        Bi = new double[itemNum];
	        for(int i=0;i<itemNum;i++){
	            Bi[i] = 0.0;
	        }

	        Bu = new double[userNum];
	        for(int i=0;i<userNum;i++){
	            Bu[i] = 0.0;
	        }

		    Alpha_u = new double[userNum];
		    for(int i=0;i<userNum;i++){
		        Alpha_u[i] = 0;
		    }

		    Bi_Bin = new double[itemNum][];
		    for(int i=0;i<itemNum;i++){
		        Bi_Bin[i] = new double[binNum];
		    }

		    for(int i=0;i<itemNum;i++){
		        for(int j=0;j<binNum;j++){
		            Bi_Bin[i][j] = 0.0;
		        }
		    }

		   
	        Qi = new double [itemNum][];
	        y = new double [itemNum][];
	        for(int i=0;i<itemNum;i++){
	            Qi[i] = new double[factor];
	            y[i] = new double[factor];
	        }

	        for(int i=0;i<itemNum;i++){
	            for(int j=0;j<factor;j++){
	                Qi[i][j] = 0.1 * ( ((double)(rand.nextInt(RAND_MAX))/(RAND_MAX + 1)) / Math.sqrt(factor));
	                y[i][j] = 0;
	            }
	        }
		   
	        sumMW = new double [userNum][];
	        Pu = new double [userNum][];
	        for(int i=0;i<userNum;i++){
	            Pu[i] = new double[factor];
	            sumMW[i] = new double[factor];
	        }

	        for(int i=0;i<userNum;i++){
	            for(int j=0;j<factor;j++){
	                sumMW[i][j] = 0.1 * (  ((double)(rand.nextInt(RAND_MAX))/(RAND_MAX + 1))   / Math.sqrt(factor));
	                Pu[i][j] = 0.1 * ( ((double)(rand.nextInt(RAND_MAX))/(RAND_MAX + 1)) / Math.sqrt(factor));
	            }
	        }
		   
		    
		    FileInputStream f1 = new FileInputStream("dataset/train.txt");
			System.setIn(new FileInputStream("dataset/train.txt"));
			Scanner sc1 = new Scanner(f1, "UTF-8");
			
			FileInputStream f2 = new FileInputStream("dataset/cross.txt");
			System.setIn(new FileInputStream("dataset/cross.txt"));
			Scanner sc2 = new Scanner(f2, "UTF-8");
		    
		    int userId,itemId,rating,t;
		    int count=0;
		    
		    train_data[count] = new Data_Line();
		    
		    while(sc1.hasNextLine())
		    {
		    	String line = sc1.nextLine();
		    	String []data = line.split(",");
		    	
		    	userId = Integer.parseInt(data[0]);
		    	itemId = Integer.parseInt(data[1]);
		    	rating = Integer.parseInt(data[3]);
		    	t = Integer.parseInt(data[2]);;
		    	
		    	if(count == userId)
				{
		    		train_data[count].setVal(userId, itemId, rating, t);
				}
				else {
					count++;
					train_data[count] = new Data_Line();
		    		train_data[count].setVal(userId, itemId, rating, t);
				}
		    	
		    }
		    
		    f1.close();
		    sc1.close();
		    
		    count = 0;
		    test_data[count] = new Data_Line();
		    
		    while(sc2.hasNextLine())
		    {
		    	String line = sc2.nextLine();
		    	String []data = line.split(",");
		    	
		    	userId = Integer.parseInt(data[0]);
		    	itemId = Integer.parseInt(data[1]);
		    	rating = Integer.parseInt(data[3]);
		    	t = Integer.parseInt(data[2]);;
		    	
		    	if(count == userId)
				{
		    		test_data[count].setVal(userId, itemId, rating, t);
				}
				else {
					count++;
					test_data[count] = new Data_Line();
		    		test_data[count].setVal(userId, itemId, rating, t);
				}
		    	
		    }
		    
		    f2.close();
		    sc2.close();
		   
		    Tu = new double[userNum];
		    for(int i=0;i<userNum;i++){
		        double tmp = 0;
		        if(train_data[i].getLength()==0)
		        {
		            Tu[i] = 0;
		            continue;
		        }
		        for(int j=0;j<train_data[i].getLength();j++){
		            tmp += train_data[i].getTime(j);
		        }
		        Tu[i] = tmp/train_data[i].getLength();
		    }

		    for(int i=0;i<userNum;i++){
		    	HashMap<Integer, Double> tmp = new HashMap<Integer, Double>();
		        for(int j=0;j<train_data[i].getLength();j++){       	
		        	if(tmp.get(train_data[i].getTime(j)) == null)
		        	{
		        		tmp.put(train_data[i].getTime(j), 0.0000001);
		        		
		        	}
		            else continue;
		        }
		        
		        Bu_t.add(i, tmp);
		        
		    }

		    for(int i=0;i<userNum;i++){
		        HashMap<Integer,Double> tmp = new HashMap<Integer, Double>();
		        Dev.add(i, tmp);
		    }
		   
	}

	
	////calculate dev_u(t) = sign(t-tu)*|t-tu|^0.4 and save the result for saving the time
	//	OK ROI
	public double CalDev(int user, int timeArg)
	{
		if(Dev.get(user).get(timeArg) != null)	
			return Dev.get(user).get(timeArg);
		
		double tmp = sign(timeArg - Tu[user]) * Math.pow( (double)(Math.abs(timeArg - Tu[user])), 0.4);
		Dev.get(user).put(timeArg, tmp);
		return tmp;
		
	}
	
	
	//Oke Luon
	public int CalBin(int timeArg)
	{
		int binsize = timeNum/binNum + 1;
	    return timeArg/binsize;
	}
	
	
	public double MyTrain() throws IOException
	{
		double preRmse = 1000;
		
		FileInputStream f1 = new FileInputStream("dataset/test.txt");
		System.setIn(new FileInputStream("dataset/test.txt"));
		Scanner sc1 = new Scanner(f1, "UTF-8");
		
		FileOutputStream output = new FileOutputStream(("dataset/5120309085_5120309016_5120309005.txt"), true);
		PrintWriter pw_out = new PrintWriter(output);
		
		
	    int user, item, date;
	    double curRmse = 0;
	    double curMae = 0;

	    for(int i=0;i<1000;i++) {
	        Train();
	        curRmse = RMSE(AVG,Bu,Bi,Pu,Qi);
	        curMae = MAE(AVG,Bu,Bi,Pu,Qi);

	        System.out.println("Test _RMSE in step " + i + ": " + curRmse);
	        System.out.println("Test _MAE in step " + i + ": " + curMae);
	        if(curRmse >= preRmse-0.00005){
	            break;
	        }
	        else{
	            preRmse = curRmse;
	        }
	    }
	    
	    while(sc1.hasNextLine())
	    {
	    	String line = sc1.nextLine();
	    	String data[] = line.split(",");
	    	double pred_score = predictScore(AVG, Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
	    	
	    	pred_score = (double) Math.ceil(pred_score * 100) / 100;
	    	
	    	String pred =  Integer.parseInt(data[0]) + "," + Integer.parseInt(data[1]) + "," + Integer.parseInt(data[2]) + "," + pred_score + "\n";
	    	
	    	pw_out.write(pred);
	    }
	    
	    output.close();
	    pw_out.close();
	    f1.close();
	    sc1.close();
	    return curRmse;
	}
	
	//function for cross validation
	public double RMSE(double avg,double[] bu,double[] bi,double[][] pu,double[][] qi){
	    int userId,itemId,rating,t;
	    int n = 0;
	    double rmse = 0;
	    
	    for(int i=0;i<test_data.length;i++)
	    {
	    	if(test_data[i].getLength() != 0) {
		    	for(int j=0;j<test_data[i].getLength();j++)
		    	{
		    		userId = test_data[i].getUser(j);
		    		itemId = test_data[i].getItem(j);
		    		t = test_data[i].getTime(j);
		    		rating = test_data[i].getTime(j);
		    		n++;
			        double pScore = predictScore(avg,userId,itemId,t);
			        rmse += (rating - pScore) * (rating - pScore);
		    	}
	    	}
	    }
	    return Math.sqrt(rmse/n);
	}


	//calculate Mean Absolute Error
	public double MAE(double avg,double[] bu,double[] bi,double[][] pu,double[][] qi){
	    int userId,itemId,rating,t;
	    int n = 0;
	    double mae = 0;
	    
	    for(int i=0;i<test_data.length;i++)
	    {
	    	if(test_data[i].getLength() != 0) {
		    	for(int j=0;j<test_data[i].getLength();j++)
		    	{
		    		userId = test_data[i].getUser(j);
		    		itemId = test_data[i].getItem(j);
		    		t = test_data[i].getTime(i);
		    		rating = test_data[i].getTime(j);
		    		n++;
		    		double pScore = predictScore(avg,userId,itemId,t);
		    		mae += Math.abs(rating - pScore);
		    	}
	    	}
	    }
	    return (mae/n);
	}
	
	
		//function for prediction
	//  prediction formula:
	//  avg + Bu + Bi
	//  + Bi_Bin,t + Alpha_u*Dev + Bu_t
	//  + Qi^T(Pu + |R(u)|^-1/2 \sum yi
	
	public double predictScore(double avg,int userId, int itemId,int time){
	   double tmp = 0.0;
	   int sz = train_data[userId].getLength();
	   double sqrtNum = 0;
	   if (sz>1) sqrtNum = 1/(Math.sqrt(sz));
	   for(int i=0;i<factor;i++){
	       tmp += (Pu[userId][i] +sumMW[userId][i]*sqrtNum) * Qi[itemId][i];		
	   }
	   
	   double score = 0.0;
	   if(Bu_t.get(userId).get(time) != null) {
		   score = avg + Bu[userId] + Bi[itemId] + Bi_Bin[itemId][CalBin(time)] + Alpha_u[userId]*CalDev(userId,time) + Bu_t.get(userId).get(time) + tmp;
	   }
	   if(score > 5){
	       score = 5;
	   }
	   if(score < 1){
	       score = 1;
	   }
	   return score;
	}
	
	public void Train(){
	    int userId,itemId,rating,time;
	    for (userId = 0; userId < userNum; ++userId) {
	        
	    	int sz = train_data[userId].getLength();
	    	
//	    	int sz = train_data[userId].length;
	        double sqrtNum = 0;
	        double[] tmpSum = new double [factor];
//	        vector <double> tmpSum(factor,0);
	        if (sz>1) sqrtNum = 1/(Math.sqrt(sz));
	        for (int k = 0; k < factor; ++k) {
	            double sumy = 0;
	            for (int i = 0; i < sz; ++i) {
	                int itemI = train_data[userId].getItem(i);
	                sumy += y[itemI][k];
	            }
	            sumMW[userId][k] = sumy;
	        }
	        for (int i = 0; i < sz; ++i) {
	            itemId = train_data[userId].getItem(i);
	            rating = train_data[userId].getRate(i);
	            time = train_data[userId].getTime(i);
	            double predict = predictScore(AVG,userId,itemId,time);
	            double error = rating - predict;
	            double temp = Bu_t.get(userId).get(time);
	            
	            Bu[userId] += G * (error - L * Bu[userId]);
	            Bi[itemId] += G * (error - L * Bi[itemId]);
	            Bi_Bin[itemId][CalBin(time)] += G * (error - L * Bi_Bin[itemId][CalBin(time)]);
	            Alpha_u[userId] += G_alpha * (error * CalDev(userId,time)  - L_alpha * Alpha_u[userId]);
	            
	            temp += G * (error - L * Bu_t.get(userId).get(time));
	            Bu_t.get(userId).put(time, temp);

	            for(int k=0;k<factor;k++){
	                double uf = Pu[userId][k];
	                double mf = Qi[itemId][k];
	                Pu[userId][k] += G * (error * mf - L_pq * uf);
	                Qi[itemId][k] += G * (error * (uf+sqrtNum*sumMW[userId][k]) - L_pq * mf);
	                tmpSum[k] += error*sqrtNum*mf;
	            }
	        }
	        for (int j = 0; j < sz; ++j) {
	            itemId = train_data[userId].getItem(j);
	            for (int k = 0; k < factor; ++k) {
	                double tmpMW = y[itemId][k];
	                y[itemId][k] += G*(tmpSum[k]- L_pq *tmpMW);
	                sumMW[userId][k] += y[itemId][k] - tmpMW;
	            }
	        }
	    }
	    for (userId = 0; userId < userNum; ++userId) {
	        int sz = train_data[userId].getLength();
	        double sqrtNum = 0;
	        if (sz>1) {
	        	sqrtNum = 1.0/Math.sqrt(sz);
	        }
	        for (int k = 0; k < factor; ++k) {
	            double sumy = 0;
	            for (int i = 0; i < sz; ++i) {
	                int itemI = train_data[userId].getItem(i);
	                sumy += y[itemI][k];
	            }
	            sumMW[userId][k] = sumy;
	        }
	    }
	    G *= Decay;
	    G_alpha *= Decay;
	}

	public static void main(String []argv) throws IOException
	{
		
		String trainFile = "dataset/train.txt";  //set train data
	    String crossFile = "dataset/cross.txt";  //set cross validation data
	    String testFile = "dataset/test.txt";  //set test data
	    String outFile = "dataset/5120309085_5120309016_5120309005.txt";  //set output data
	    
	    FileInputStream f = new FileInputStream("dataset/training.txt");
		System.setIn(new FileInputStream("dataset/training.txt"));
		Scanner sc = new Scanner(f, "UTF-8");
		
		FileOutputStream outputTrain = new FileOutputStream(("dataset/train.txt"), true);
		PrintWriter pw_train = new PrintWriter(outputTrain);
		FileOutputStream outputTest = new FileOutputStream(("dataset/cross.txt"), true);
		PrintWriter pw_cross = new PrintWriter(outputTest);
	    
		
		while(sc.hasNextLine())
		{
			Random rand = new Random();
		    if(rand.nextInt(100) == 0)
		    {
		    	String data = sc.nextLine();
		    	data = data + "\n";
		    	pw_cross.write(data);
		    }
		    else
		    {
		    	String data = sc.nextLine();
		    	data = data + "\n";
		    	pw_train.write(data);
		    }
		}
		
		sc.close();
		f.close();
		pw_train.close();
		pw_cross.close();
		
		TimeSVD timesvd = new TimeSVD(trainFile, crossFile, testFile, outFile);
		
	    double rmse = timesvd.MyTrain();     //train
			
		
	}
	
}
