package com.toennies.ci1429.app.network.message;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MessageTransformerTest
{

	@Test
	public void searchForPatternTest1()
	{
		// Simple test, if proper index for one single byte is returned.

		byte[] buffer = { 0, 10, 5, 2, 6, 1, -1, 10 };
		int start = 0;

		int foundIndex0 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 0 });
		int foundIndex10 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 10 });
		int foundIndex5 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 5 });
		int foundIndex2 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 2 });
		int foundIndex6 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 6 });
		int foundIndex1 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 1 });
		int foundIndexMinus1 = MessageTransformer.searchForPattern(buffer, start, new byte[] { -1 });
		int notFoundIndex11 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 11 });

		assertTrue(foundIndex0 == 1);
		assertTrue(foundIndex10 == 2);
		assertTrue(foundIndex5 == 3);
		assertTrue(foundIndex2 == 4);
		assertTrue(foundIndex6 == 5);
		assertTrue(foundIndex1 == 6);
		assertTrue(foundIndexMinus1 == 7);
		assertTrue(notFoundIndex11 == -1);

	}

	@Test
	public void searchForPatternTest2()
	{
		// Tests, if proper index for one single byte is returned; starts at 4
		byte[] buffer = { 0, 10, 5, 9, 6, 1, -1, 10 };
		int start = 4;

		int notFoundIndex0 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 0 });
		int foundIndex10 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 10 });
		int notFoundIndex5 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 5 });
		int notFoundIndex9 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 9 });

		int foundIndex6 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 6 });
		int foundIndex1 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 1 });
		int foundIndexMinus1 = MessageTransformer.searchForPattern(buffer, start, new byte[] { -1 });
		int notFoundIndex11 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 11 });

		assertTrue(notFoundIndex0 == -1);
		assertTrue(foundIndex10 == 8);
		assertTrue(notFoundIndex5 == -1);
		assertTrue(notFoundIndex9 == -1);

		assertTrue(foundIndex6 == 5);
		assertTrue(foundIndex1 == 6);
		assertTrue(foundIndexMinus1 == 7);
		assertTrue(notFoundIndex11 == -1);
	}

	@Test
	public void searchForPatternTest3()
	{
		// Test, if proper index for two bytes is returned
		byte[] buffer = { 0, 10, 6, 1, -1 };

		int start = 0;

		int foundIndex0And10 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 0, 10 });
		int foundIndex10And6 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 10, 6 });
		int foundIndex6And1 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 6, 1 });
		int foundIndex1AndMinus1 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 1, -1 });

		int notFoundIndex0And5 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 0, 5 });
		int notFoundIndex10And0 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 10, 0 });
		int notFoundIndex11And13 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 11, 13 });

		assertTrue(foundIndex0And10 == 2);
		assertTrue(foundIndex10And6 == 3);
		assertTrue(foundIndex6And1 == 4);
		assertTrue(foundIndex1AndMinus1 == 5);

		assertTrue(notFoundIndex0And5 == -1);
		assertTrue(notFoundIndex10And0 == -1);
		assertTrue(notFoundIndex11And13 == -1);
	}

	@Test
	public void searchForPatternTest4()
	{
		// Tests, if buffer has only one byte of size
		byte[] buffer = { 5 };

		int start = 0;

		int foundIndex5 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 5 });

		int notFoundIndex0 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 0 });
		int notFoundIndex4 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 4 });
		int notFoundIndex6 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 6 });
		int notFoundIndex0And5 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 0, 5 });
		int notFoundIndex5And0 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 5, 0 });
		int notFoundIndex0And0 = MessageTransformer.searchForPattern(buffer, start, new byte[] { 0, 0 });

		assertTrue(foundIndex5 == 1);

		assertTrue(notFoundIndex0 == -1);
		assertTrue(notFoundIndex4 == -1);
		assertTrue(notFoundIndex6 == -1);
		assertTrue(notFoundIndex0And5 == -1);
		assertTrue(notFoundIndex5And0 == -1);
		assertTrue(notFoundIndex0And0 == -1);
	}

	//TODO discuss with hendrik
//	@Test
//	public void searchForPatternTest5()
//	{
//		byte[] buffer = { 0, 5, 10, 15, 20 };
//
//		byte[][] pattern1 = { { 15, 20 }, { 15 } };
//		byte[][] pattern2 = { { 15 }, { 20 } };
//
//		int start = 0;
//
//		int found1 = MessageTransformer.searchForPattern(buffer, start, pattern1);
//		int found2 = MessageTransformer.searchForPattern(buffer, start, pattern2);
//
//		assertTrue(found1 == 5);
//		assertTrue(found2 == 4);
//	}
}
