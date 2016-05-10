package com.github.zkxs.dicebruteforce;

import java.util.HashMap;
import java.util.Map;

public class RollGenerator
{	
	byte[] currentRoll;
	int numberOfDice;
	byte numberOfSides;
	Map<Roll.Score, Integer> scores = new HashMap<>();
	
	
	public static void main(String[] args)
	{
		RollGenerator rgen = new RollGenerator(6, (byte) 6);
		
		long startTime = System.nanoTime();
		Map<Roll.Score, Integer> results = rgen.generate();
		long stopTime = System.nanoTime();
		
		System.out.printf("Done in %fms\n\n", (stopTime - startTime) / 1_000_000d);
		
		
		for (Roll.Score score : Roll.Score.values())
		{
			Integer count = results.get(score);
			System.out.printf("%s: %d\n", Roll.getScoreName(score), count);
		}
	}
	
	public RollGenerator(int numberOfDice, byte numberOfSides)
	{
		this.currentRoll = new byte[numberOfDice];
		this.numberOfDice = numberOfDice;
		this.numberOfSides = numberOfSides;
	}
	
	public Map<Roll.Score, Integer> generate()
	{
		generate(0);
		return scores;
	}
	
	/**
	 * 
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
				Roll roll = new Roll(currentRoll);
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
