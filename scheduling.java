//package scheduling;
/**
 * Project 2: Scheduling Algorithm 
 * Author: Sonam Tailor 
 * Collaborated with Christina Liu 
 */
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;


public class scheduling {
	static int indexOfRandom = 0;
	static ArrayList<Integer> random = new ArrayList<Integer>();	

	public static void main(String[] args) throws URISyntaxException, FileNotFoundException {
		
		
		File rand = new File("random-numbers.txt");
		
		readRandomNumbers(rand, random);
		File inputFile;
		//storing the random number file

		//verify that command line argument exists
		if (args.length == 0) {
			System.err.println("Usage Error: the program expects an argument.");
			System.exit(1);
		}
		
		Boolean verbose = false;
		if(args[0].equals("--verbose")) {
			verbose = true;
			inputFile = new File(args[1]);
		}
		
		//case that the verbose flag is not there
		else {
			inputFile = new File(args[0]);
		}
		
		//scanner to iterate through our input file
		Scanner input = new Scanner(inputFile);

		int numProcesses = input.nextInt(); 
		
		//Queue to store the process objects - different processes for each algorithm 
		Queue<Process> processes1 = new LinkedList<Process>(); 
		Queue<Process> processes2 = new LinkedList<Process>(); 
		Queue<Process> processes3 = new LinkedList<Process>();
		Queue<Process> processes4 = new LinkedList<Process>();
		
		int[] startIndices = new int[numProcesses];
		
		for (int i = 0; i < numProcesses; i++) {
			int a = input.nextInt();
			int b = input.nextInt();
			int c = input.nextInt();
			int m = input.nextInt();
			
			startIndices[i]= a;

			Process temp1 = new Process(a,b,c,m, "unstarted",c, i);
			Process temp2  = new Process(a,b,c,m, "unstarted",c,i);
			Process temp3 = new Process(a,b,c,m, "unstarted", c, i);
			Process temp4 = new Process(a,b,c,m, "unstarted", c, i);
			

			//add to queue
			processes1.add(temp1);
			processes2.add(temp2);
			processes3.add(temp3);
			processes4.add(temp4);
			
			//call methods
		}
		
		
		Arrays.sort(startIndices);
		FCFS(processes1,numProcesses,verbose, startIndices);
		System.out.println("-------------------------------------------------");
		RR(processes2, numProcesses, verbose, startIndices);
		System.out.println("-------------------------------------------------");
		SJF(processes3, numProcesses, verbose, startIndices);
		System.out.println("-------------------------------------------------");
		HPRN(processes4, numProcesses, verbose, startIndices);
	}
	
	public static void FCFS(Queue<Process> processes, int numProcesses, Boolean verbose, int[] ordering) throws FileNotFoundException {
		System.out.print("The original input was: " + numProcesses);
		printInput(processes, numProcesses);
		System.out.print("The (sorted) input is: " + numProcesses);
		//sorted order for FCFS
		Queue<Process> temp = new LinkedList<Process>(); //temp sorted queue
		float CPUTime = 0;
		
		//Data structures to hold the processes with the respective states
		Queue<Process> finishedProc = new LinkedList<Process>();
		
		//sort the queue 
		
		int i = 0;
		for(int j = 0; j < ordering.length; j++) {
			for (Process c : processes) {
				if(c.a == ordering[j]) {
					c.processNum = i;
					temp.add(c);
					processes.remove(c);
					break;
				}
			}
			i++;
		}
		processes = temp;
		//print sorted input
		printInput(processes, numProcesses);
			
		int cycle = 0; //to store the cycle #
		Process addToEnd;
		
		System.out.println();
		if (verbose) {
			System.out.println("This detailed printout gives the state and remaining burst for each process");	
		}	
			//first process has not started
		
			while (cycle<=ordering[0]) {
				if(verbose) {
					System.out.print("\nBefore cycle \t"+ cycle+":");
				
				for(Process c: processes) {
					System.out.printf("\t"+c.state+"%2s",0);
				}
				}
				cycle++;
			}
			
			String[] states = new String[numProcesses];
			//will store the integer state of each process
			int[] statenum = new int[numProcesses];
			
			while(!processes.isEmpty()) {
				if (verbose) {
					System.out.print("\nBefore cycle \t"+cycle+":");
				}
				for (Process c: processes) {
					
					//case that it is first element of queue and in state running, ready, or unstarted
					if(c.state != "blocked" && processes.peek()==c && cycle>c.a) {
						if(c.CPUBurst == 0 && c.IOBurst == 0) {
							
							c.setCPUburst(c.b);
							indexOfRandom++;
						}
						c.state = "running";
						//store state before decrementing CPU burst
						statenum[c.processNum]=c.CPUBurst;
						c.CPUBurst--;
						CPUTime++;
						
						//decrease total remaining time
						c.C-=1;
						
						states[c.processNum]="running";
						
						if(c.CPUBurst== 0 && c.IOBurst!=0) {
							c.state = "blocked";
						}
					}
					
					//unstarted or ready state
					else if(c.state == "unstarted" || c.state == "ready") {
						if (c.state == "unstarted" && cycle<=c.a) {
							states[c.processNum] = "unstarted";
							//if it is unstarted integer state is 0
							statenum[c.processNum] = 0;
						}
						else {
							c.state = "ready";
							c.waitTime+=1;
							states[c.processNum]="ready";
							statenum[c.processNum]=0;
						}
					}
					
					//case that process is blocked
					else {
						//store integer state before decrementing
						statenum[c.processNum] = c.IOBurst;
						c.IOBurst-=1;
						c.ioTime+=1;
						states[c.processNum]="blocked";
						if(c.IOBurst==0) {
							c.state = "ready";
						}
					}
				}
				if (!processes.isEmpty() && (processes.peek().state == "blocked" || processes.peek().state == "unstarted")) {
					addToEnd = processes.poll();
					processes.add(addToEnd);
					
				
					while(processes.peek().state=="unstarted" && (cycle<=processes.peek().a)) {
							states[processes.peek().processNum]= "unstarted";
							addToEnd = processes.poll();
							processes.add(addToEnd);
					
						}
				}
				if (verbose) {
					printVerbose(states, statenum);
				}
				
				for(Process c: processes) {
					if (c.C == 0) {
						c.state = "terminated";
						states[c.processNum]="terminated";
						statenum[c.processNum]= 0;
						c.finishingTime = cycle;
						//add to list of completed
						finishedProc.add(c);
						processes.remove(c);
						
					}
				}
				cycle++;
				
			}
		
		System.out.println("\nThe scheduling algorithm used was First Come First Served ");
		System.out.println("");
		
		int index = 0;
		float totalturn = 0;
		float totalWait = 0;
		float totalIO = 0;
		
		//need to sort the finishedProc 
		finishedProc = sortFinished(ordering, finishedProc);
		
		for (Process c: finishedProc) {
			printOutput(c,index);
			totalturn += (c.finishingTime - c.a);
			totalWait += c.waitTime;
			totalIO += c.ioTime;
			index++;
		}
		
		printSummary(numProcesses, cycle-1, totalturn, totalWait, totalIO, CPUTime);
		
	}
	
	public static void RR(Queue<Process> processes, int numProcesses, Boolean verbose, int[] ordering) throws FileNotFoundException {
		System.out.print("The original input was: " + numProcesses);
		printInput(processes, numProcesses);
		//sorted order for RR is the same
		System.out.print("The (sorted) input is: " + numProcesses);
		printInput(processes, numProcesses);
		
		
		int cycle = 0; //to store the cycle #
		Process addToEnd;
		Queue<Process> finishedProc = new LinkedList<Process>();
		float CPUTime = 0;
		
		System.out.println();
		if (verbose) {
			System.out.println("This detailed printout gives the state and remaining burst for each process");	
		}	
			//first process has not started
			while (cycle<=ordering[0]) {
				if(verbose) {
					System.out.print("\nBefore cycle \t"+ cycle+":");
				
					for(Process c: processes) {
						System.out.printf("\t"+c.state+"%2s",0);
					}
				}
				cycle++;
			}
			
			String[] states = new String[numProcesses];
			//will store the integer state of each process
			int[] statenum = new int[numProcesses];
			while(!processes.isEmpty()) {
				
				if (verbose) {
					System.out.print("\nBefore cycle \t"+cycle+":");
				}
				
				for (Process c: processes) {
					//case that it is first element of queue and in state running, ready, or unstarted
					if(c.state != "blocked" && processes.peek()==c && cycle>c.a) {
						if(c.CPUBurst == 0 && c.IOBurst == 0) {
							
							c.setCPUburst(c.b);
							indexOfRandom++;
							//our quantum is 2 so we can only run for a max of 2 on the CPU
							if(c.CPUBurst > 2) {
								c.CPUBurst = 2;
								c.setIOburst(2);
							}
						}
						
						c.state = "running";
						//store state before decrementing CPU burst
						statenum[c.processNum]=c.CPUBurst;
						c.CPUBurst--;
						CPUTime++;
						
						//decrease total remaining time
						c.C-=1;
						
						states[c.processNum]="running";
						
						if(c.CPUBurst== 0 && c.IOBurst!=0) {
							c.state = "blocked";
						}
					}
					
					//unstarted or ready state
					else if(c.state == "unstarted" || c.state == "ready") {
						if (c.state == "unstarted" && cycle<=c.a) {
							states[c.processNum] = "unstarted";
							//if it is unstarted integer state is 0
							statenum[c.processNum] = 0;
						}
						else {
							c.state = "ready";
							c.waitTime+=1;
							states[c.processNum]="ready";
							statenum[c.processNum]=0;
						}
					}
					
					//case that process is blocked
					else {
						//store integer state before decrementing
						statenum[c.processNum] = c.IOBurst;
						c.IOBurst-=1;
						c.ioTime+=1;
						states[c.processNum]="blocked";
						if(c.IOBurst==0) {
							c.state = "ready";
						}
					}
				}
				if (!processes.isEmpty() && (processes.peek().state == "blocked" || processes.peek().state == "unstarted")) {
					addToEnd = processes.poll();
					processes.add(addToEnd);
					
				
					while(processes.peek().state=="unstarted" && (cycle<=processes.peek().a)) {
							states[processes.peek().processNum]= "unstarted";
							addToEnd = processes.poll();
							processes.add(addToEnd);
					
						}
				}
				if (verbose) {
					printVerbose(states,statenum);
				}
				
				for(Process c: processes) {
					if (c.C == 0) {
						c.state = "terminated";
						states[c.processNum]="terminated";
						statenum[c.processNum]= 0;
						c.finishingTime = cycle;
						//add to list of completed
						finishedProc.add(c);
						processes.remove(c);
						
					}
				}
				cycle++;
				
			}
		
		System.out.println("\nThe scheduling algorithm used was Round Robin");
		System.out.println("");
		
		int index = 0;
		float totalturn = 0;
		float totalWait = 0;
		float totalIO = 0;
		for (Process c: finishedProc) {
			printOutput(c,index);
			totalturn += (c.finishingTime - c.a);
			totalWait += c.waitTime;
			totalIO += c.ioTime;
			index++;
		}
		
		printSummary(numProcesses, cycle-1, totalturn, totalWait, totalIO, CPUTime);
		
	}
	//used a different approach of ready queue, unfinished queue and arrayList to store the blocked, and finished processes
	public static void HPRN(Queue<Process> processes, int numProcesses, Boolean verbose, int[] ordering) throws FileNotFoundException {
		System.out.print("The original input was: "+ numProcesses);
		printInput(processes, numProcesses);
		System.out.print("The (sorted) input is "+ numProcesses);
		//sorted input for HPRN is done by arrival time - use same code from FCFS
		
		float CPUTime = 0;
		
		Queue<Process> temp = new LinkedList<Process>(); //temp sorted queue
		
		//sort the queue 
		int i = 0; 
		for(int j = 0; j < ordering.length; j++) {
			for (Process c : processes) {
				if(c.a == ordering[j]) {
					c.processNum = i;
					temp.add(c);
					processes.remove(c);
					break;
				}
			}
			i++;
		}
		processes = temp;
		//print sorted input
		printInput(processes, numProcesses);
		System.out.println("");
		if (verbose) {
			System.out.println("This detailed printout gives the state and remaining burst for each process");	
		}
		System.out.println("");
		//Note that for HPRN the formula for the ratio is r= T/t where T is the wall clock time this process has been in the system and t is the running time of the process 
		Process addToEnd;
		Queue<Process> finishedProc = new LinkedList<Process>();
		
		Queue<Process> readyProc = new ConcurrentLinkedQueue<Process>();
		ArrayList<Process> blocked = new ArrayList<Process>();
		ArrayList<Process> unstartedProc = new ArrayList<Process>();
		Process currRun = null; //this will store the process that is running
		//will store the number of processes completed
		int numFinished = 0; 
		String[] states = new String[numProcesses];
		int[] statenum = new int[numProcesses];
		
		//an array of processes to store the sorted input ones --> note that with this algorithm the process order is always changing
		Process processInput[] = new Process[numProcesses];
		
		//store values in either the ready or unstarted based on their states 
		//either stored in the unstarted array or the ready array
		for (int j = 0; j< numProcesses; j++) {
			Process temp2 = processes.poll();
			processInput[j] = new Process(temp2.a, temp2.b, temp2.c, temp2.m, temp2.state, temp2.C, temp2.processNum);
			
			//check if the process started at time = 0 --> therefore it would be in the ready state
			if(processInput[j].a == 0) {
				readyProc.add(processInput[j]);
			}
			//otherwise add it to the unstarted processes
			else {
				unstartedProc.add(processInput[j]);
			}
		}
		
		int cycle = 0; //to store the cycle #
		while(numFinished < numProcesses) {
			//if the process start time = the cycle num add to ready instead of unstarted 
			for(int j = 0; j < unstartedProc.size(); j++) {
				if(unstartedProc.get(j).a == cycle) {
					Process toAdd = unstartedProc.get(j);
					toAdd.currCycle =  cycle;
					toAdd.state = "ready";
					readyProc.add(toAdd);
				}
			}
			cycle++;
		
		//now if we have no process running then we get it from ready
		if(currRun == null) {
			currRun = readyProc.poll(); //removes the process from head 
			//currRun.state = "running";
			//make sure isnt null
			if(currRun != null) {
				currRun.setCPUburst(currRun.b);
			}
		}
		
		//case that it is not null so we run
		if(currRun != null) {
			currRun.CPUBurst--;
			currRun.C--;
			CPUTime++;
			currRun.currCycle = cycle;
			//currRun.state = "running";
		}
		
		if (verbose) {
			System.out.print("Before cycle "+cycle+":");
			
			//updating array states to print - I will check if the process is stored in the specific array
			for(Process c: processInput) {
				if(blocked.contains(c)) {
					states[c.processNum] = "blocked";
					statenum[c.processNum] = c.IOBurst;
				}
				else if(readyProc.contains(c)) {
					states[c.processNum] = "ready";
					statenum[c.processNum]=0;
				}
				else if(finishedProc.contains(c)) {
					states[c.processNum] = "terminated";
					statenum[c.processNum] = 0;
				}
				else if(unstartedProc.contains(c) && cycle <= c.a) {
					states[c.processNum] = "unstarted";
					statenum[c.processNum] = 0;
				}
				else {
					states[c.processNum] = "running";
					statenum[c.processNum]=c.CPUBurst+1; //because we subtracted 1 before
				}
			}
			printVerbose(states, statenum);
			System.out.println();
		}
		
		//if a process is stored in the ready table --> increment the wait counter
		if(!readyProc.isEmpty()) {
			for(Process c: readyProc) {
				c.waitTime++;
				c.currCycle = cycle;
			}
		}
		
		//case of state = blocked
		if(!blocked.isEmpty()) {
			Process[] processArr = blocked.toArray(new Process[0]);
			ArrayList<Process> whatToAdd = new ArrayList<Process>();
			for(int j = 0; j < processArr.length; j++) {
				Process c = processArr[j];
				c.IOBurst--;
				c.ioTime++;
				c.currCycle = cycle;
				
				if(c.IOBurst == 0) {
					readyProc.add(c);
					blocked.remove(c);
				}
			}
			readyProc.addAll(whatToAdd);
		}
		
		//case of state = running
		if(currRun!=null) {
			//done running
			if(currRun.C == 0) {
				currRun.finishingTime = cycle;
				finishedProc.add(currRun);
				numFinished++;
				currRun = null;
			}
			//case that CPU burst done
			else if(currRun.CPUBurst <=0 && currRun.C!=0){
				//check
				blocked.add(currRun);
				currRun = null;
			}
			
		}
		
		//THIS IS THE main portion of the algorithm -- sorting by ratio
		if(!readyProc.isEmpty()) {
			//make a temporary array to sort
			ArrayList<Process> temp3 = new ArrayList<Process>();
			while(!readyProc.isEmpty()) {
				temp3.add(readyProc.poll());
			}
			Collections.sort(temp3, new ComparatorByRatio());
			readyProc.addAll(temp3);
		}
		}
		System.out.println("\nThe scheduling algorithm used was Highest Penalty Ratio Next");
		System.out.println("");
		
		int index = 0;
		float totalturn = 0;
		float totalWait = 0;
		float totalIO = 0;
		//need to sort the finishedProc 
		finishedProc = sortFinished(ordering, finishedProc);
		
		for(Process c: finishedProc) {
			printOutput(c,index);
			totalturn += (c.finishingTime - c.a);
			totalWait += c.waitTime;
			totalIO += c.ioTime;
			index++;
		}
		printSummary(numProcesses, cycle, totalturn, totalWait, totalIO, CPUTime);
		
		
	}
		
		
	public static void SJF(Queue<Process> processes, int numProcesses, Boolean verbose, int[] ordering) throws FileNotFoundException {
		//HashMap<Process, Integer> map = new  HashMap<Process, Integer>();
		System.out.print("The original input was: " + numProcesses);
		printInput(processes, numProcesses);
		System.out.print("The (sorted) input is: " + numProcesses);
		//sorted order for SJF
	     //temp sorted queue
		Queue<Process> temp = new LinkedList<Process>();
		float CPUTime = 0;
		
		//sort the queue 
		int i = 0;
		while(!processes.isEmpty()) {
			int front = ordering[i];
				for(Process c: processes) {
					if (c.a < front) {
					Process addtoEnd = processes.poll();
					temp.add(addtoEnd);
				}
			}
			Process add = processes.poll();
			temp.add(add);
			i++;
		}
		processes = temp;
		//print sorted input
		printInput(processes, numProcesses);
			
		int cycle = 0; //to store the cycle #
		Process addToEnd;
		Queue<Process> finishedProc = new LinkedList<Process>();
		
		System.out.println();
		if (verbose) {
			System.out.println("This detailed printout gives the state and remaining burst for each process");	
		}	
	
			//first process has not started
			while (cycle<=ordering[0]) {
				if(verbose) {
					System.out.print("\nBefore cycle \t"+ cycle+":");
				
				for(Process c: processes) {
					System.out.printf("\t"+c.state+"%2s",0);
				}
				}
				cycle++;
			}
			
			String[] states = new String[numProcesses];
			//will store the integer state of each process
			int[] statenum = new int[numProcesses];
			
			while(!processes.isEmpty()) {
				if(verbose) {
					System.out.print("\nBefore cycle \t"+cycle+":");
				}
				for (Process c: processes) {
					//case that it is first element of queue and in state running, ready, or unstarted
					if(c.state != "blocked" && processes.peek()==c && cycle>c.a) {
						if(c.CPUBurst == 0 && c.IOBurst == 0) {
							
							c.setCPUburst(c.b);
							indexOfRandom++;
						}
						c.state = "running";
						//store state before decrementing CPU burst
						statenum[c.processNum]=c.CPUBurst;
						c.CPUBurst--;
						CPUTime++;
						
						//decrease total remaining time
						c.C-=1;
						
						states[c.processNum]="running";
						
						if(c.CPUBurst== 0 && c.IOBurst!=0) {
							c.state = "blocked";
						}
					}
					
					//unstarted or ready state
					else if(c.state == "unstarted" || c.state == "ready") {
						if (c.state == "unstarted" && cycle<=c.a) {
							states[c.processNum] = "unstarted";
							//if it is unstarted integer state is 0
							statenum[c.processNum] = 0;
						}
						else {
							c.state = "ready";
							c.waitTime+=1;
							states[c.processNum]="ready";
							statenum[c.processNum]=0;
						}
					}
					
					//case that process is blocked
					else {
						//store integer state before decrementing
						statenum[c.processNum] = c.IOBurst;
						c.IOBurst-=1;
						c.ioTime+=1;
						states[c.processNum]="blocked";
						if(c.IOBurst==0) {
							c.state = "ready";
						}
					}
				}
				if (!processes.isEmpty() && (processes.peek().state == "blocked" || processes.peek().state == "unstarted")) {
					addToEnd = processes.poll();
					processes.add(addToEnd);
					
				
					while(processes.peek().state=="unstarted" && (cycle<=processes.peek().a)) {
							states[processes.peek().processNum]= "unstarted";
							addToEnd = processes.poll();
							processes.add(addToEnd);
					
					}
				}
				if (verbose) {
					printVerbose(states, statenum);
				}
				
				for(Process c: processes) {
					if (c.C == 0) {
						c.state = "terminated";
						states[c.processNum]="terminated";
						statenum[c.processNum]= 0;
						c.finishingTime = cycle;
						//add to list of completed
						finishedProc.add(c);
						processes.remove(c);
						
					}
				}
				cycle++;
				
		}
		
		System.out.println("\nThe scheduling algorithm used was Shortest Job First ");
		System.out.println("");
		
		int index = 0;
		float totalturn = 0;
		float totalWait = 0;
		float totalIO = 0;
		
		//need to sort the finishedProc 
		finishedProc = sortFinished(ordering, finishedProc);
		
		for (Process c: finishedProc) {
			printOutput(c,index);
			totalturn += (c.finishingTime - c.a);
			totalWait += c.waitTime;
			totalIO += c.ioTime;
			index++;
		}
		
		printSummary(numProcesses, cycle-1, totalturn, totalWait, totalIO, CPUTime);

	}
	
	public static void printInput(Queue<Process> proc, int numProcesses) {
		
		for (Process p: proc) {
			System.out.print(" ");
			System.out.print("(");
			System.out.print(p.a + " ");
			System.out.print(p.b + " ");
			System.out.print(p.c + " ");
			System.out.print(p.m);
			System.out.print(")");
		}
		System.out.println("");
		
		
	}
	
	public static void printOutput(Process c, int cycle) {
		System.out.println("Process "+cycle+":");
		System.out.print("(A,B,C,M) = ");
		System.out.print("("+c.a +","+c.b+","+c.c+","+c.m+")");
		System.out.println("\nFinishing time: "+c.finishingTime);
		int turnaround = c.finishingTime - c.a;
		System.out.println("Turnaround time: " + turnaround);
		System.out.println("I/O time: "+ c.ioTime);
		System.out.println("Waiting time: " + c.waitTime);
	
		System.out.println("");
	}
	
	//CPU UTILIZATION?????
	public static void printSummary(int numprocesses, int finish, float totalturn, float totalWait, float totalIO, float CPUTime) {
		System.out.println();
		System.out.println("Summary Data:");
		System.out.println("\tFinishing time: "+finish);
		float cpuUtilization = CPUTime/finish;
		float ioutilization = totalIO/finish;
		System.out.printf("\tCPU Utilization: %6f\n",cpuUtilization);
		System.out.printf("\tI/O Utilization: %6f\n", ioutilization);
		float throughput =  (float)((100*numprocesses)/(float)(finish));
		System.out.printf("\tThroughput: %6f processes per hundred cycles\n",throughput);
		float avgTurnaround = totalturn/(float)numprocesses;
		System.out.printf("\tAverage turnaround time: %6f\n", avgTurnaround);
		float avgWait = totalWait/numprocesses;
		System.out.printf("\tAverage waiting time: %6f\n", avgWait);
		
	}

	public static void printVerbose(String[] states, int[] statenum) {
		for(int i = 0; i < states.length; i++) {
			if(states[i].equals("ready")) {
				System.out.printf("\t%3s\t %2s", states[i],statenum[i]);
			}
			else if(states[i].equals("terminated")|| states[i].equals("unstarted")) {
				System.out.printf("\t%3s %s",states[i],statenum[i]);
			}
			else {
				System.out.printf("\t%3s %3s",states[i],statenum[i]);
			}
		}
	}
	
	public static void readRandomNumbers(File rand, ArrayList<Integer> random) throws FileNotFoundException {
		Scanner scanRand = new Scanner(new FileReader(rand));
		while (scanRand.hasNext()) {
			random.add(scanRand.nextInt());
		}
	}
	
	public static Queue<Process> sortFinished(int[] ordering, Queue<Process> finishedProc) {
		Queue<Process> temp = new LinkedList<Process>();
		//need to sort the finishedProc 
				for(int j = 0; j < ordering.length; j++) {
					for (Process c : finishedProc) {
						if(c.a == ordering[j]) {
							temp.add(c);
							finishedProc.remove(c);
							break;
						}
					}
				}
				
				return temp;
	}
	
	

}

class ComparatorByRatio implements Comparator<Object>{

	@Override
	public int compare(Object proc1, Object proc2) {
		//cast objects so we can compare
		Process pro1 = (Process)proc1;
		Process pro2 = (Process)proc2;
		
		if(pro1.getRatio()>pro2.getRatio()) {
			return -1;
		}
		else if(pro1.getRatio()<pro2.getRatio()) {
			return 1;
		}
		else {
			int priority1 = pro1.sortedPriority;
			int priority2 = pro2.sortedPriority;
			if(priority1>priority2) {
				return 1;
			}
			else {
				return -1;
			}
		}
	}
	
}

class comparePriority implements Comparator<Object>{

	@Override
	public int compare(Object proc1, Object proc2) {
		Process pro1 = (Process)proc1;
		Process pro2 = (Process)proc2;
		
		if(pro1.sortedPriority>pro2.sortedPriority) {
			return 1;
		}
		// TODO Auto-generated method stub
		else {
			return -1;
		}
	}
	
}
