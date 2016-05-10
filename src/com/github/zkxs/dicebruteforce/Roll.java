package com.github.zkxs.dicebruteforce;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Roll
{	
	/** Counts of individual dice values. This must NOT be mutated. */
	private int[] partitions;
	
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
		int[] partition = getPartitions().clone();
		
		assert(partition.length <= 6) : "Farkle only uses 6 dice";
		
		// check for a straight
		{
			boolean isStraight = true;
			for (int idx = 0; idx < partition.length; idx++)
			{
				if (partition[idx] == 0)
				{
					isStraight = false;
					break;
				}
			}
			if (isStraight)
			{
				// further checking is not required as all six dice have been consumed
				return getScoreValue(Score.STRAIGHT);
			}
		}
		
		// check for three-pairs
		{
			int pairs = 0;
			for (int idx = 0; idx < partition.length; idx++)
			{
				if (partition[idx] == 2)
				{
					pairs += 1;
				}
			}
			assert (pairs <= 3) : "How can you have more than three pairs with only six dice?";
			if (pairs == 3)
			{
				// further checking is not required as all six dice have been consumed
				return getScoreValue(Score.THREE_PAIR);
			}
		}
		
		// six of a kinds
		for (int idx = 0; idx < partition.length; idx++)
		{
			if (partition[idx] == 6)
			{
				// further checking is not required as all six dice have been consumed
				return getScoreValue(Score.SIX_OF_A_KIND);
			}
		}
		
		// five of a kinds
		for (int idx = 0; idx < partition.length; idx++)
		{
			if (partition[idx] == 5)
			{
				totalScore += getScoreValue(Score.FIVE_OF_A_KIND);
				partition[idx] -= 5;
				assert(partition[idx] == 0) : "A more-than-five-of-a-kind leaked through";
			}
		}
		
		// four of a kinds
		for (int idx = 0; idx < partition.length; idx++)
		{
			if (partition[idx] == 4)
			{
				partition[idx] -= 4;
				
				// check to see if there is an additional pair
				for (int pairIdx = 0; pairIdx < partition.length; pairIdx++)
				{
					if (partition[idx] == 2)
					{
						// further checking is not required as all six dice have been consumed
						return getScoreValue(Score.FOUR_AND_TWO);
					}
				}
				
				totalScore += getScoreValue(Score.FOUR_OF_A_KIND);
				assert(partition[idx] == 0) : "A more-than-four-of-a-kind leaked through";
			}
		}
		
		// three of a kinds
		{
			int tripleIdx = -1; // not -1 if a triple has been found
			for (int idx = 0; idx < partition.length; idx++)
			{
				if (partition[idx] == 3)
				{
					if (tripleIdx != -1)
					{
						// a second triple was just found
						return getScoreValue(Score.TWO_TRIPLES);
					}
					else
					{
						tripleIdx = idx;
					}
				}
			}
			
			if (tripleIdx != -1)
			{
				switch (tripleIdx + 1)
				{
					case 1: totalScore += getScoreValue(Score.TRIPLE1); break;
					case 2: totalScore += getScoreValue(Score.TRIPLE2); break;
					case 3: totalScore += getScoreValue(Score.TRIPLE3); break;
					case 4: totalScore += getScoreValue(Score.TRIPLE4); break;
					case 5: totalScore += getScoreValue(Score.TRIPLE5); break;
					case 6: totalScore += getScoreValue(Score.TRIPLE6); break;
				}
				partition[tripleIdx] -= 3;
				
				assert(partition[tripleIdx] == 0) : "A more-than-three-of-a-kind leaked through";
			}
		}
		
		// any ones?
		totalScore += partition[1 - 1] * getScoreValue(Score.ONE);
		
		// any fives?
		totalScore += partition[5 - 1] * getScoreValue(Score.FIVE);
		
		return totalScore;
	}
	
	public Roll(byte[] diceValues, byte numberOfSides)
	{
		partitions = computePartitions(diceValues, numberOfSides);
	}
	
	public Roll(Roll roll)
	{
		// no copying is required because partition is never mutated
		partitions = roll.partitions;
	}
	
	/**
	 * Get the counts of dice values.
	 * @return the partition array. This array must NOT be mutated.
	 */
	private int[] getPartitions()
	{
		return partitions;
	}
	
	private int[] computePartitions(byte[] diceValues, byte numberOfSides)
	{
		int[] partitions = new int[numberOfSides];
		
		for (int idx = 0; idx < diceValues.length; idx++)
		{
			partitions[diceValues[idx] - 1] += 1;
		}
		return partitions;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(partitions);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Roll other = (Roll) obj;
		if (!Arrays.equals(partitions, other.partitions)) return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return Arrays.toString(partitions);
	}
}
