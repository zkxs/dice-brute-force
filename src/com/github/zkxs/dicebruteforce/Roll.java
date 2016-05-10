package com.github.zkxs.dicebruteforce;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Roll
{	
	private byte[] diceValues;
	
	public enum Score
	{
		FIVE,
		ONE,
		TRIPLE1,
		TRIPLE2,
		TRIPLE3,
		TRIPLE4,
		TRIPLE5,
		TRIPLE6,
		FOUR_OF_A_KIND,
		FIVE_OF_A_KIND,
		SIX_OF_A_KIND,
		STRAIGHT,
		THREE_PAIR,
		FOUR_AND_TWO,
		TWO_TRIPLES,
		FARKLE
	}
	
	private static Map<Score, String> scoreNames;
	private static final byte[]
		REFERENCE_TWO_TRIPLES  = {1,1,1,2,2,2},
		REFERENCE_FOUR_AND_TWO = {1,1,1,1,2,2},
		REFERENCE_THREE_PAIR   = {1,1,2,2,3,3};
	
	public static String getScoreName(Score score)
	{
		switch (score) {
			case FIVE:            return "A five";
			case ONE:             return "A one";
			case TRIPLE1:         return "1,1,1";
			case TRIPLE2:         return "2,2,2";
			case TRIPLE3:         return "3,3,3";
			case TRIPLE4:         return "4,4,4";
			case TRIPLE5:         return "5,5,5";
			case TRIPLE6:         return "6,6,6";
			case FOUR_OF_A_KIND:  return "Four-of-a-kind";
			case FIVE_OF_A_KIND:  return "Five-of-a-kind";
			case SIX_OF_A_KIND:   return "Six-of-a-kind";
			case STRAIGHT:        return "Straight 1-6";
			case THREE_PAIR:      return "Three pairs";
			case FOUR_AND_TWO:    return "Four-of-a-kind and a pair";
			case TWO_TRIPLES:     return "Two three-of-a-kinds";
			case FARKLE:          return "Farkle";
			default:              throw new IllegalArgumentException("Invalid Score");
		}
	}
	
	public static int getScoreValue(Score score)
	{
		switch (score) {
			case FIVE:            return 50;
			case ONE:             return 100;
			case TRIPLE1:         return 300;
			case TRIPLE2:         return 200;
			case TRIPLE3:         return 300;
			case TRIPLE4:         return 400;
			case TRIPLE5:         return 500;
			case TRIPLE6:         return 600;
			case FOUR_OF_A_KIND:  return 1000;
			case FIVE_OF_A_KIND:  return 2000;
			case SIX_OF_A_KIND:   return 3000;
			case STRAIGHT:        return 1500;
			case THREE_PAIR:      return 1500;
			case FOUR_AND_TWO:    return 1500;
			case TWO_TRIPLES:     return 2500;
			case FARKLE:          return 0;
			default:              throw new IllegalArgumentException("Invalid Score");
		}
	}
	
	public int score()
	{
		int totalScore = 0;
		Roll generalizedRoll = null;
		
		// some combinations require all 6 dice
		if (diceValues.length == 6)
		{
			// check for a straight
			{
				boolean isStraight = true;
				for (int i = 0; i < 6; i++)
				{
					if (diceValues[i] != i + 1)
					{
						isStraight = false;
						break;
					}
				}
				if (isStraight)
				{
					return getScoreValue(Score.STRAIGHT);
				}
			}
			
			generalizedRoll = this.generalize();
			if (Arrays.equals(generalizedRoll.diceValues, REFERENCE_TWO_TRIPLES))
			{
				return getScoreValue(Score.TWO_TRIPLES);
			}
			if (Arrays.equals(generalizedRoll.diceValues, REFERENCE_FOUR_AND_TWO))
			{
				return getScoreValue(Score.FOUR_AND_TWO);
			}
			if (Arrays.equals(generalizedRoll.diceValues, REFERENCE_THREE_PAIR))
			{
				return getScoreValue(Score.THREE_PAIR);
			}
			
			// check for 6 of a kind
			{
				boolean allSame = true;
				for (int i = 0; i < 6; i++)
				{
					if (generalizedRoll.diceValues[i] != 1)
					{
						allSame = false;
						break;
					}
				}
				if (allSame)
				{
					return getScoreValue(Score.SIX_OF_A_KIND);
				}
			}
		}
		
		if (diceValues.length >= 5)
		{
			if (generalizedRoll == null)
			{
				generalizedRoll = this.generalize();
			}
			
			boolean allSame = true;
			for (int i = 0; i < 5; i++)
			{
				if (generalizedRoll.diceValues[i] != 1)
				{
					allSame = false;
					break;
				}
			}
			if (allSame)
			{
				totalScore += getScoreValue(Score.FIVE_OF_A_KIND);
				//TODO: check last die to see if it's a one or a five
			}
		}
		
		if (diceValues.length >= 4)
		{
			if (generalizedRoll == null)
			{
				generalizedRoll = this.generalize();
			}
			
			boolean allSame = true;
			for (int i = 0; i < 4; i++)
			{
				if (generalizedRoll.diceValues[i] != 1)
				{
					allSame = false;
					break;
				}
			}
			if (allSame)
			{
				totalScore += getScoreValue(Score.FOUR_OF_A_KIND);
				//TODO: check last two dice to see if they are ones or fives
			}
		}
		
		// three of a kinds
		{
			int currentCount = 0;
			byte currentValue = -1;
			
			for (int idx = 0; idx < diceValues.length; idx++)
			{
				if (diceValues[idx] != currentValue)
				{
					currentValue = diceValues[idx];
					currentCount = 0;
				}
				currentCount += 1;
				if (currentCount == 3)
				{
					switch (currentValue)
					{
						case 1: totalScore += getScoreValue(Score.TRIPLE1); break;
						case 2: totalScore += getScoreValue(Score.TRIPLE2); break;
						case 3: totalScore += getScoreValue(Score.TRIPLE3); break;
						case 4: totalScore += getScoreValue(Score.TRIPLE4); break;
						case 5: totalScore += getScoreValue(Score.TRIPLE5); break;
						case 6: totalScore += getScoreValue(Score.TRIPLE6); break;
					}
				}
			}
		}
		
		// any ones?
		for (int idx = 0; idx < diceValues.length; idx++)
		{
			if (diceValues[idx] == 1)
			{
				return Score.ONE;
			}
		}
		
		// any fives?
		for (int idx = 0; idx < diceValues.length; idx++)
		{
			if (diceValues[idx] == 5)
			{
				return Score.FIVE;
			}
		}
		
		return totalScore;
	}
	
	public Roll(byte[] diceValues)
	{
		this.diceValues = Arrays.copyOf(diceValues, diceValues.length);
		Arrays.sort(this.diceValues);
	}
	
	public Roll(Roll roll)
	{
		this.diceValues = Arrays.copyOf(roll.diceValues, roll.diceValues.length);
		// no sorting is required because existing rolls are pre-sorted
	}
	
	public Roll generalize()
	{
		// copy this roll
		Roll generalizedRoll = new Roll(this);
		
		// generaize the copy
		generalizedRoll.generalizeSelf();
		
		return generalizedRoll;
	}
	
	private void generalizeSelf()
	{
		byte currentToValue = 1;
		byte rollIdx = 0;
		int[] partitions = getPartitions();
		for (int idx = partitions.length - 1; idx >= 0 && partitions[idx] != 0; idx--) {
			for(int i = 0; i < partitions[idx]; i++)
			{
				diceValues[rollIdx++] = currentToValue;
			}
			currentToValue += 1;
		}
	}
	
	private int[] getPartitions()
	{
		int[] partitions = new int[diceValues.length];
		int partitionIdx = -1;
		byte currentValue = -1;
		
		for (int idx = 0; idx < diceValues.length; idx++)
		{
			if (diceValues[idx] != currentValue)
			{
				currentValue = diceValues[idx];
				partitionIdx += 1;
			}
			partitions[partitionIdx] += 1;
		}
		
		Arrays.sort(partitions);
		return partitions;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(diceValues);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Roll other = (Roll) obj;
		if (!Arrays.equals(diceValues, other.diceValues)) return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return Arrays.toString(diceValues);
	}
}
