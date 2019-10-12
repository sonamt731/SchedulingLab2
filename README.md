# SchedulingLab2
For this code the input.txt file and random-numbers.txt file must be within the same directory. The input.txt file must contain input without paranthesis and as a space separated integer list. 

For example, input can be space separated like --> 1 0 1 5 1

This code accepts the input.txt file as a command line argument. 

To Run on Crackle:

1) cd SchedulingLab2
2) javac Process.java scheduling.java

Now two possible paths...
1) For quick summarized output enter: 
        java scheduling input.txt
        
2) For a detailed printout that gives the state and remaining burst for each processs. 
    java scheduling --verbose input.txt
    
The code produces output of the algorithms in the following order. 
 
1) First Come First Serve (FCFS)
2) Round Robin (RR)
3) Shortest Job First (SJF)
4) Highest Priority Ration Next (HPRN)

The input.txt file stores.. the number of processes as the first integer
The next groups each of 4 integers stores..
1) the process start time
2) the b value used to calculuate CPU Burst
3) the c value that the process is on the CPU
4) the m value used to calculate IO Burst 
