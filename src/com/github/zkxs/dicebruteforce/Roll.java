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
	
	static
	{
		scoreNames = new HashMap<>();
		scoreNames.put(Score.FIVE,            "A five");
		scoreNames.put(Score.ONE,             "A one");
		scoreNames.put(Score.TRIPLE1,         "1,1,1");
		scoreNames.put(Score.TRIPLE2,         "2,2,2");
		scoreNames.put(Score.TRIPLE3,         "3,3,3");
		scoreNames.put(Score.TRIPLE4,         "4,4,4");
		scoreNames.put(Score.TRIPLE5,         "5,5,5");
		scoreNames.put(Score.TRIPLE6,         "6,6,6");
		scoreNames.put(Score.FOUR_OF_A_KIND,  "Four-of-a-kind");
		scoreNames.put(Score.FIVE_OF_A_KIND,  "Five-of-a-kind");
		scoreNames.put(Score.SIX_OF_A_KIND,   "Six-of-a-kind");
		scoreNames.put(Score.STRAIGHT,        "Straight 1-6");
		scoreNames.put(Score.THREE_PAIR,      "Three pairs");
		scoreNames.put(Score.FOUR_AND_TWO,    "Four-of-a-kind and a pair");
		scoreNames.put(Score.TWO_TRIPLES,     "Two three-of-a-kinds");
		scoreNames.put(Score.FARKLE,          "Farkle");
	}
	
	public static String getScoreName(Score score)
	{
		return scoreNames.get(score);
	}
	
	public Score score()
	{
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
					return Score.STRAIGHT;
				}
			}
			
			generalizedRoll = this.generalize();
			if (Arrays.equals(generalizedRoll.diceValues, REFERENCE_TWO_TRIPLES))
			{
				return Score.TWO_TRIPLES;
			}
			if (Arrays.equals(generalizedRoll.diceValues, REFERENCE_FOUR_AND_TWO))
			{
				return Score.FOUR_AND_TWO;
			}
			if (Arrays.equals(generalizedRoll.diceValues, REFERENCE_THREE_PAIR))
			{
				return Score.THREE_PAIR;
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
					return Score.SIX_OF_A_KIND;
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
				return Score.FIVE_OF_A_KIND;
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
				return Score.FOUR_OF_A_KIND;
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
						case 1: return Score.TRIPLE1;
						case 2: return Score.TRIPLE2;
						case 3: return Score.TRIPLE3;
						case 4: return Score.TRIPLE4;
						case 5: return Score.TRIPLE5;
						case 6: return Score.TRIPLE6;
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
		
		return Score.FARKLE;
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
//		byte currentFromValue = diceValues[0];
//		byte currentToValue = 1;
//		diceValues[0] = currentToValue;
//		
//		for (int idx = 1; idx < diceValues.length; idx++)
//		{
//			if (diceValues[idx] != currentFromValue)
//			{
//				currentFromValue = diceValues[idx];
//				currentToValue += 1;
//			}
//			diceValues[idx] = currentToValue;
//		}
		
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
