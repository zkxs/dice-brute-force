package com.github.zkxs.dicebruteforce;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class RollGenerator
{	
	private byte[] currentRoll;
	private int numberOfDice;
	private byte numberOfSides;
	private Map<Integer, Integer> scores = new HashMap<>(); // mapping of scores to number of occurences
	
	
	public static void main(String[] args)
	{
		//ArrayList<Map<Integer, Integer>> results = new ArrayList<>(6);
		Set<Integer> scoreValues = new TreeSet<>();
		
		for (int numberOfDice = 6; numberOfDice > 0; numberOfDice--)
		{
			RollGenerator rgen = new RollGenerator(numberOfDice, (byte) 6);
			long startTime = System.nanoTime();
			Map<Integer, Integer> currentResult = rgen.generate();
			long stopTime = System.nanoTime();
			
			//results.add(numberOfDice, currentResult);
			
			scoreValues.addAll(currentResult.keySet());
			
			System.out.printf("%d dice trial. Done in %fms\n", numberOfDice, (stopTime - startTime) / 1_000_000d);
			for (Integer scoreValue : scoreValues)
			{
				Integer scoreCount = currentResult.getOrDefault(scoreValue, 0);
				System.out.printf("%d: %d\n", scoreValue, scoreCount);
			}
			System.out.println();
		}
	}
	
	/**
	 * Construct a new RollGenerator
	 * @param numberOfDice Number of dice to roll
	 * @param numberOfSides Number of sides on a die
	 */
	public RollGenerator(int numberOfDice, byte numberOfSides)
	{
		this.currentRoll = new byte[numberOfDice];
		this.numberOfDice = numberOfDice;
		this.numberOfSides = numberOfSides;
	}
	
	/**
	 * Generate all possible rolls
	 * @return A mapping of scores to the number of occurences of said score
	 */
	public Map<Integer, Integer> generate()
	{
		generate(0);
		return scores;
	}
	
	/**
	 * Generate all possible rolls, recursively
	 * @param currentDie index of current die
	 */
	private void generate(int currentDie)
	{
		boolean baseCase = currentDie + 1 == numberOfDice;
		
		// branch based off number of sides
		for (byte i = 1; i <= numberOfSides; i++)
		{
			currentRoll[currentDie] = i;
			
			if (baseCase)
			{
				// return current roll
				Roll roll = new Roll(currentRoll, numberOfSides);
				scores.merge(roll.score(), 1, (a, b) -> a + b);
			}
			else
			{
				// we must go deeper
				generate(currentDie + 1);
			}
		}
	}
}
