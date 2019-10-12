//package scheduling;
/*
 * This class implements the Process class. It stores each process' a, b, c, m, remaining CPU Time (C), IO Bursts, CPU Bursts, 
 * process states and process numbers. 
 * 
 */
import java.io.*;
import java.util.*;


public class Process{
	int a;
	int b; 
	int c;
	int C; //how much is remaining of the process
	int m;
	int finishingTime = 0; 
	int ioTime = 0;
	int waitTime = 0;
	int CPUBurst;
	int IOBurst;
	String state;
	int processNum; 
	
	//Values used for HPRN - ratio and sorting calculations
	int sortedPriority;
	int currCycle;
	

	
	public Process(int a, int b, int c, int m, String state, int C, int processNum) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.C = C;
		this.m = m;
		this.state = state;
		this.processNum = processNum;
		
		
	}
	
	//DO JAVADOC HERE FOR THE RANDOM FUNCTION
	public int randomOS(int U) throws FileNotFoundException {
		int num = getRandFromIndex();
		//System.out.println("This was the random number: "+num);
		int val = 1+(num%U);
		setIOburst(val);
		return val;
		
	}
	
	public void setCPUburst(int U) throws FileNotFoundException {
		int val = randomOS(U);
		if (val>this.C) {
			this.CPUBurst = C;
		}
		else {
			this.CPUBurst = val;
		}
		setIOburst(this.CPUBurst);
	}
	
	public void setIOburst(int burst) {
		this.IOBurst = burst*this.m;
	}
	
	public void setioTime(int time) {
		this.ioTime = time;
	}
	
	public void setWaitTime(int time) {
		this.waitTime = time;
	}
	

	
	public int getRandFromIndex() {
		return scheduling.random.get(scheduling.indexOfRandom);
	}
	
	//function used for HPRN
	public double getRatio() {
		if((this.c - this.C) == 0) {
			return (double)currCycle - this.a;
		}
		else {
			return (double)(currCycle - this.a)/(this.c-this.C);
		}
	}
	
	public int compareTo(Process a){
		if(a.a>this.a)return -1;
		else if(a.a<this.a)return 1;
		else if(a.b>this.b)return -1;
		else if(a.b<this.b)return 1;
		else if(a.c>this.c)return -1;
		else if(a.C<this.C)return 1;
		else if(a.m>this.m)return -1;
		else if(a.m<this.m)return 1;
		else return 0;
	} 
	
}