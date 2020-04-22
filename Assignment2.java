package assignment2;

import java.util.Scanner;

public class Assignment2 {

	private final static int MAX = 10000;
	private static int largestNum = 1;
	private static int maxDivisorCount = 0;
	
	//Main Program
	public static void main(String[] args) {
		int numberOfThreads = 0;
		//Get number of threads to use from user
		Scanner input = new Scanner(System.in);
		while (numberOfThreads < 1 || numberOfThreads > 9) {
			System.out.print("How many Threads to use (1 - 8)?: ");
			numberOfThreads = input.nextInt();
			//Ensure user input between 1 and 8
			if (numberOfThreads < 1 || numberOfThreads > 9) {
				System.out.print("\nPlease pick a number between 1 and 8 for number of threads to use.\n");
			}
			
		}
		//Split program into threads to find the integer with the largest number of divisors
		countDivisorsWithThreads(numberOfThreads);
		input.close();
	}	
	
	//Finds the number of divisors of the integer. Each integer calls on this method
	public static int countDivisors(int n) {
		int count = 0;
		for (int i = 1; i <= n; i++) {
			if (n%i == 0) {
				count++;
			}	
		}
		return count;
	}
		
	//Each thread calls this method to check if their maxDivisor is the largest and update the variables if true
	private static void compareMaxFromThread(int maxDivisorCountThread, int maxDivisorThread) {
		if (maxDivisorCountThread > maxDivisorCount) {
			maxDivisorCount = maxDivisorCountThread;
			largestNum = maxDivisorThread;
		}
	}
	
	//A thread class which finds the number of divisors for all integers in the range it is assigned to process
	private static class FindMaxDivisor extends Thread {
		//min and max numbers for each thread to process
		int min, max;
		//Constructor
		public FindMaxDivisor(int min, int max) {
			this.min = min;
			this.max = max;
		}
		public void run() {
			//max divisor count and its corresponding integer for the thread
			int maxCount = 0;
			int num = 0;
			//for each integer in the range for the thread, find the number of divisors and store the largest
			for (int i = min; i < max; i++) {
				int numOfDivisors = countDivisors(i);
				if (numOfDivisors > maxCount) {
					maxCount = numOfDivisors;
					num = i;
				}
			}
			//Compare this thread's largest to the other threads
			compareMaxFromThread(maxCount, num);
		}
	}
	
	//Method to create threads and perform the task
	private static void countDivisorsWithThreads(int numberOfThreads) {
		long startTime = System.currentTimeMillis();
		FindMaxDivisor[] worker = new FindMaxDivisor[numberOfThreads];
		int intsPerThread = MAX/numberOfThreads;
		int start = 1;
		int end = start + intsPerThread - 1;
		for (int i = 0; i < numberOfThreads; i++) {
			//ensures the thread loops through MAX integers, in case of rounding error
			if (i == numberOfThreads - 1) {
				end = MAX;
			}
			worker[i] = new FindMaxDivisor(start, end);
			
			//Determine new range for the next thread
			start = end + 1;
			end = start + intsPerThread - 1;
			
			//start thread
			worker[i].start();
		}
		
		//wait for each thread to finish
		for (int i = 0; i < numberOfThreads; i++) {
			while(worker[i].isAlive()) {
				try {
					worker[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		
		//Print results
		System.out.println("\nNumber of threads used: " + numberOfThreads);
		System.out.print("\nThe largest integer between 1 and " + MAX + " is " + largestNum + " with " + maxDivisorCount + " divisors.");
		System.out.print("\nTotal elapsed time: " + elapsedTime/1000.0 + " seconds.");
	}
}
