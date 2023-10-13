/*
	This file is part of FreeJ2ME.

	FreeJ2ME is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	FreeJ2ME is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with FreeJ2ME.  If not, see http://www.gnu.org/licenses/
*/
package org.recompile.mobile;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WavImaAdpcmDecoder
{

	/* Information about this audio format: https://wiki.multimedia.cx/index.php/IMA_ADPCM */

	/* 
	 * Variables to hold the previously decoded sample and step used, per channel (if needed) 
	 * "NOTE: Arrays that won't be reassigned (but its values can still change) and variables 
	 * that won't be changed are marked as final throughout the code to try and optimize the 
	 * execution as much as possible since FreeJ2ME has a habit of freezing when adpcm samples 
	 * are being decoded. So far only seems to happen in Java 8 and on my more limited devices."
	 *     - @AShiningRay
	 */
	private static final int LEFTCHANNEL = 0;
	private static final int RIGHTCHANNEL = 1;

	private static final int HEADERSIZE = 44;
	private static final int PCMPREAMBLESIZE = 16;

	private static final int[] prevSample = {0, 0};
	private static final int[] prevStep = {0, 0};
	
	private static final int[] ima_step_index_table = 
	{
		-1, -1, -1, -1, 2, 4, 6, 8, 
		-1, -1, -1, -1, 2, 4, 6, 8
	};
	
	private static final int[] ima_step_size_table = 
	{
		7, 8, 9, 10, 11, 12, 13, 14, 16, 17,
		19, 21, 23, 25, 28, 31, 34, 37, 41, 45,
		50, 55, 60, 66, 73, 80, 88, 97, 107, 118,
		130, 143, 157, 173, 190, 209, 230, 253, 279, 307,
		337, 371, 408, 449, 494, 544, 598, 658, 724, 796,
		876, 963, 1060, 1166, 1282, 1411, 1552, 1707, 1878, 2066,
		2272, 2499, 2749, 3024, 3327, 3660, 4026, 4428, 4871, 5358,
		5894, 6484, 7132, 7845, 8630, 9493, 10442, 11487, 12635, 13899,
		15289, 16818, 18500, 20350, 22385, 24623, 27086, 29794, 32767
	};

	/* 
	 * This method will decode IMA WAV ADPCM into linear PCM_S16LE.
	 */
	public static byte[] decodeADPCM(final byte[] input, int inputSize, final int numChannels, final int frameSize)
	{
		byte adpcmSample;
		byte curChannel;
		int inputIndex = 0, outputIndex = HEADERSIZE; /* Give some space for the header by starting from byte 44. */
		int decodedSample;

		/* 
		 * Make sure that the output size is 4 times as big as input's due to IMA ADPCM being able to pack 4 bytes of standard PCM 
		 * data into a single one, with 44 additional bytes in place to accomodate for the new header that will be created afterwards.
		 *
		 * Also, calculate the final expected output size right here instead of decreasing the output size on each preamble read,
		 * checking if the final size is doesn't match what was initially expected (if there's at least one preamble, it won't)
		 * and then copying everything into a new byte array, which is too costly and can be cut entirely by just passing the 
		 * correct output size right at the start. This is done because preamble data should not be added into the decoded stream,
		 * and each preamble is 4 bytes long on IMA ADPCM, which means it would take 16 bytes on the decoded stream for each preamble added.
		 */
		final int outputSize = (inputSize * 4) + HEADERSIZE - (PCMPREAMBLESIZE * (int) java.lang.Math.floor(inputSize / frameSize));
		final byte[] output = new byte[outputSize];

		/* Initialize the predictor's sample and step values. */
		prevSample[LEFTCHANNEL] = 0;
		prevStep[LEFTCHANNEL] = ima_step_size_table[2];

		if(numChannels == 2) 
		{
			prevSample[RIGHTCHANNEL] = 0;
			prevStep[RIGHTCHANNEL] = ima_step_size_table[2];
		}

		while (inputSize > 0) 
		{
			/* Check if the decoder reached the beginning of a new chunk to see if the preamble needs to be read. */
			if (inputSize % frameSize == 0)
			{
				/* Bytes 0 and 1 describe the chunk's initial predictor value (little-endian) */
				prevSample[LEFTCHANNEL] = ((input[inputIndex])) | ((input[inputIndex+1]) << 8);
				/* Byte 2 is the chunk's initial index on the step_size_table */
				prevStep[LEFTCHANNEL]   = (input[inputIndex+2]);

				if (prevStep[LEFTCHANNEL] < 0)       { prevStep[LEFTCHANNEL] = 0; }
				else if (prevStep[LEFTCHANNEL] > 88) { prevStep[LEFTCHANNEL] = 88; }

				/* 
				 * For each 4 bits used in IMA ADPCM, 16 must be used for PCM so adjust 
				 * indices and sizes accordingly. Byte 3 is reserved and has no practical 
				 * use for us.
				 */
				inputIndex += 4;
				inputSize -= 4;

				if (numChannels == 2) /* If we're dealing with stereo IMA ADPCM: */
				{
					/* Bytes 4 and 5 describe the chunk's initial predictor value (little-endian) for the second channel */
					prevSample[RIGHTCHANNEL] = ((input[inputIndex])) | ((input[inputIndex+1]) << 8);
					prevStep[RIGHTCHANNEL]   = (input[inputIndex + 2]);

					if (prevStep[RIGHTCHANNEL] < 0)       { prevStep[RIGHTCHANNEL] = 0; }
					else if (prevStep[RIGHTCHANNEL] > 88) { prevStep[RIGHTCHANNEL] = 88; }

					inputIndex += 4;
					inputSize -= 4;
				}
			}

			/* 
			 * If the decoder isn't at the beginning of a chunk, or the preamble has already been read, 
			 * decode ADPCM samples inside that same chunk. 
			 */

			/* 
			 * In the very rare (pretty much non-existent) cases where some j2me app 
			 * might use stereo IMA ADPCM, we should decode each audio channel. 
			 * 
			 * If the format is stereo, it is assumed to be interleaved, which means that
			 * the stream will have a left channel sample followed by a right channel sample,
			 * followed by a left... and so on. In ADPCM those samples appear to be setup in
			 * such a way that 4 bytes (or 8 nibbles) for the left channel are followed by 4 bytes 
			 * for the right, at least according to https://wiki.multimedia.cx/index.php/Microsoft_IMA_ADPCM.
			 */
			if (numChannels == 2) 
			{
				/* 
				 * So in the case it's a stereo stream, decode 8 nibbles from both left and right channels, interleaving
				 * them in the resulting PCM stream.
				 */
				for (short i = 0; i < 8; i++) 
				{
					if(i < 4) { curChannel = LEFTCHANNEL; }
					else      { curChannel = RIGHTCHANNEL; }

					adpcmSample = (byte) (input[inputIndex] & 0x0f);
					decodedSample = decodeSample(curChannel, adpcmSample);
					output[outputIndex + ((i & 3) << 3) + (curChannel << 1)] = (byte)(decodedSample & 0xff);
					output[outputIndex + ((i & 3) << 3) + (curChannel << 1) + 1] = (byte)(decodedSample >> 8);

					adpcmSample = (byte) ((input[inputIndex] >> 4) & 0x0f);
					decodedSample = decodeSample(curChannel, adpcmSample);
					output[outputIndex + ((i & 3) << 3) + (curChannel << 1) + 4] = (byte)(decodedSample & 0xff);
					output[outputIndex + ((i & 3) << 3) + (curChannel << 1) + 5] = (byte)(decodedSample >> 8);
					inputIndex++;
				}
				outputIndex += 32;
				inputSize -= 8;
			}
			else
			{
				/* 
				 * If it's mono, just decode nibbles from ADPCM into PCM data sequentially, there's no sample 
				 * interleaving to worry about, much less multiple channels, so we only use channel 0.
				 */
				adpcmSample = (byte)(input[inputIndex] & 0x0f);
				decodedSample = decodeSample(LEFTCHANNEL, adpcmSample);
				output[outputIndex++] = (byte)(decodedSample & 0xff);
				output[outputIndex++] = (byte)((decodedSample >> 8) & 0xff);

				adpcmSample = (byte)((input[inputIndex] >> 4) & 0x0f);
				decodedSample = decodeSample(LEFTCHANNEL, adpcmSample);
				output[outputIndex++] = (byte)(decodedSample & 0xff);
				output[outputIndex++] = (byte)((decodedSample >> 8) & 0xff);

				inputIndex++;
				inputSize--;
			}
		}

		return output;
	}

	/* This method will decode a single IMA ADPCM sample to linear PCM_S16LE sample. */
	static short decodeSample(final int channel, byte adpcmSample)
	{
		/* 
		 * This decode procedure is based on the following document:
		 * https://www.cs.columbia.edu/~hgs/audio/dvi/IMA_ADPCM.pdf
		 */

		/* Get the step size to be used when decoding the given sample. */
		int stepSize = ima_step_size_table[prevStep[channel]] & 0x0000FFFF;

		/* This variable acts as 'difference' and then 'newSample' on columbia's doc */
		int decodedSample = (stepSize >> 3) & 0x1fff;
		
		/* 
		 * Similar to cs.columbia doc's first code block on Page 32, multiplication through 
		 * repetitive addition. Nowadays using multiplication is probably faster on most archs, 
		 * but let's follow the reference implementation for the sake of improving documentation.
		 */
		if ((adpcmSample & 4) != 0) { decodedSample += stepSize; }
		if ((adpcmSample & 2) != 0) { decodedSample += (stepSize >> 1) & 0x7fff; }
		if ((adpcmSample & 1) != 0) { decodedSample += (stepSize >> 2) & 0x3fff; }
		if ((adpcmSample & 8) != 0) { decodedSample  = -decodedSample; }
		
		decodedSample += prevSample[channel];

		/* 
		 * Clamps the value of decodedSample to that of a short data type. At this point, the decoded 
		 * sample already should already fit nicely into a short type value range as per columbia's doc.
		 */
		if (decodedSample < -32768)     { decodedSample = -32768; }
		else if (decodedSample > 32767) { decodedSample = 32767; }

		prevSample[channel] = decodedSample;

		/* Basically columbia doc's "calculate stepsize" snippet */
		prevStep[channel] += ima_step_index_table[(int)(adpcmSample & 0x0FF)];
		if (prevStep[channel] < 0)       { prevStep[channel] = 0; }
		else if (prevStep[channel] > 88) { prevStep[channel] = 88; }

		/* Return the decoded sample casted to short (16-bit) */
		return (short) (decodedSample & 0xFFFF);
	}
	
	/*
	 * Since the header is always expected to be positioned right at the start
	 * of a byte array, read it to determine the WAV type.
	 * 
	 * Optionally it also returns some information about the audio format to help build a 
	 * new header for the decoded stream.
	*/
	public int[] readHeader(InputStream input) throws IOException 
	{
		/*
			The header of a WAV (RIFF) file is 44 bytes long and has the following format:

			CHAR[4] "RIFF" header
			UINT32  Size of the file (chunkSize).
			  CHAR[4] "WAVE" format
				CHAR[4] "fmt " header
				UINT32  SubChunkSize (examples: 12 for PCM unsigned 8-bit )
				  UINT16 AudioFormat (ex: 1 [PCM], 17 [IMA ADPCM] )
				  UINT16 NumChannels
				  UINT32 SampleRate
				  UINT32 BytesPerSec (samplerate*frame size)
				  UINT16 frameSize or blockAlign (256 on some gameloft games)
				  UINT16 BitsPerSample (gameloft games appear to use 4)
				CHAR[4] "data" header
				UINT32 Length of sample data.
				<Sample data>
		*/

		String riff = readInputStreamASCII(input, 4);
		int dataSize = readInputStreamInt32(input);
		String format = readInputStreamASCII(input, 4);
		String fmt = readInputStreamASCII(input, 4);
		int chunkSize = readInputStreamInt32(input);
		short audioFormat = (short) readInputStreamInt16(input);
		short audioChannels = (short) readInputStreamInt16(input);
		int sampleRate = readInputStreamInt32(input);
		int bytesPerSec = readInputStreamInt32(input);
		short frameSize = (short) readInputStreamInt16(input);
		short bitsPerSample = (short) readInputStreamInt16(input);
		String data = readInputStreamASCII(input, 4);
		int sampleDataLength = readInputStreamInt32(input);

		/* Those are only meant for debugging. */
		/*
		System.out.println("WAV HEADER_START");

		System.out.println(riff);
		System.out.println("FileSize:" + dataSize);
		System.out.println("Format: " + format);

		System.out.println("---'fmt' header---\n");
		System.out.println("Header ChunkSize:" + Integer.toString(chunkSize));
		System.out.println("AudioFormat: " + Integer.toString(audioFormat));
		System.out.println("AudioChannels:" + Integer.toString(audioChannels));
		System.out.println("SampleRate:" + Integer.toString(sampleRate));
		System.out.println("BytesPerSec:" + Integer.toString(bytesPerSec));
		System.out.println("FrameSize:" + Integer.toString(frameSize));
		System.out.println("BitsPerSample:" + Integer.toHexString(bitsPerSample));

		System.out.println("\n---'data' header---\n");
		System.out.println("SampleData Length:" + Integer.toString(sampleDataLength));

		System.out.println("WAV HEADER_END\n\n\n");
		*/
		
		/* 
		 * We need the audio format to check if it's ADPCM or PCM, and the file's 
		 * dataSize, SampleRate and audioChannels to decode ADPCM and build a new header. 
		 */
		return new int[] {audioFormat, sampleRate, audioChannels, frameSize};
	}

	/* Read a 16-bit little-endian unsigned integer from input.*/
	public static int readInputStreamInt16(InputStream input) throws IOException 
	{ return ( input.read() & 0xFF ) | ( ( input.read() & 0xFF ) << 8 ); }

	/* Read a 32-bit little-endian signed integer from input.*/
	public static int readInputStreamInt32(InputStream input) throws IOException 
	{
		return ( input.read() & 0xFF ) | ( ( input.read() & 0xFF ) << 8 )
			| ( ( input.read() & 0xFF ) << 16 ) | ( ( input.read() & 0xFF ) << 24 );
	}

	/* Return a String containing 'n' Characters of ASCII/ISO-8859-1 text from input. */
	public static String readInputStreamASCII(InputStream input, int nChars) throws IOException 
	{
		byte[] chars = new byte[nChars];
		readInputStreamData(input, chars, 0, nChars);
		return new String(chars, "ISO-8859-1");
	}

	/* Read 'n' Bytes from the InputStream starting from the specified offset into the output array. */
	public static void readInputStreamData(InputStream input, byte[] output, int offset, int nBytes) throws IOException 
	{
		int end = offset + nBytes;
		while(offset < end) 
		{
			int read = input.read(output, offset, end - offset);
			if(read < 0) throw new java.io.EOFException();
			offset += read;
		}
	}

	/* 
	 * Builds a WAV header that describes the decoded ADPCM file on the first 44 bytes. 
	 * Data: little-endian, 16-bit, signed, same sample rate and channels as source IMA ADPCM.
	 */
	private void buildHeader(final byte[] buffer, final short numChannels, final int sampleRate) 
	{ 
		final short bitsPerSample = 16;   /* 16-bit PCM */
		final short audioFormat = 1;      /* WAV linear PCM */
		final int subChunkSize = 16;      /* Fixed size for Wav Linear PCM */
		final int chunk = 0x52494646;     /* 'RIFF' */ 
		final int format = 0x57415645;    /* 'WAVE' */ 
		final int subChunk1 = 0x666d7420; /* 'fmt ' */ 
		final int subChunk2 = 0x64617461; /* 'data' */ 

		/* 
		 * We'll have 16 bits per sample, so each sample has 2 bytes, with that we just divide
		 * the size of the byte buffer (minus the header) by (bitsPerSample/8), then multiply by the amount 
		 * of channels... assuming i didn't mess anything up on the calculus.
		*/
		final int samplesPerChannel = buffer.length-44 / ((bitsPerSample/8) * numChannels);

		/* 
		 * Frame size is fairly standard, and PCM's fixed sample size makes it so the frameSize is either 2 bytes 
		 * for mono, or 4 bytes for stereo.
		 */
		final short frameSize = (short) (numChannels * (bitsPerSample / 8));

		/* 
		 * Previously only took into account mono streams. And since we know the framesize and
		 * the amount of samples per channel, in a format that has a fixed amount of bits per sample,
		 * we can account for multiple audio channels on sampleDataLength with a simpler calculus:
		 */
		final int sampleDataLength = (samplesPerChannel * numChannels) * frameSize;

		/* 
		 * Represents how many bytes are streamed per second. With all of the data above, it's trivial to
		 * calculate by getting the sample rate, the amount of channels and bytes per sample (bitsPerSample / 8)
		 */
		final int bytesPerSec = sampleRate * numChannels * (bitsPerSample / 8);
		
		/* NOTE: ChunkSize includes the header, so sampleDataLength + 44, which is the byte size of our header */

		/* ChunkID */
		ByteBuffer.wrap(buffer, 0, 4).order(ByteOrder.BIG_ENDIAN).putInt(chunk);
		/* ChunkSize (or File size) */
		ByteBuffer.wrap(buffer, 4, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(sampleDataLength + 44);
		/* Format (WAVE) */
		ByteBuffer.wrap(buffer, 8, 4).order(ByteOrder.BIG_ENDIAN).putInt(format);
		/* SubchunkID (fmt) */
		ByteBuffer.wrap(buffer, 12, 4).order(ByteOrder.BIG_ENDIAN).putInt(subChunk1);
		/* SubchunkSize (or format chunk size) */
		ByteBuffer.wrap(buffer, 16, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(subChunkSize);
		/* Audioformat */
		ByteBuffer.wrap(buffer, 20, 2).order(ByteOrder.LITTLE_ENDIAN).putShort(audioFormat);
		/* NumChannels (will be the same as source ADPCM) */
		ByteBuffer.wrap(buffer, 22, 2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) numChannels);
		/* SampleRate (will be the same as source ADPCM) */
		ByteBuffer.wrap(buffer, 24, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(sampleRate);
		/* ByteRate (BytesPerSec) */
		ByteBuffer.wrap(buffer, 28, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(bytesPerSec);
		/* BlockAlign (Frame Size) */
		ByteBuffer.wrap(buffer, 32, 2).order(ByteOrder.LITTLE_ENDIAN).putShort(frameSize);
		/* BitsPerSample */
		ByteBuffer.wrap(buffer, 34, 2).order(ByteOrder.LITTLE_ENDIAN).putShort(bitsPerSample);
		/* Subchunk2ID (data) */
		ByteBuffer.wrap(buffer, 36, 4).order(ByteOrder.BIG_ENDIAN).putInt(subChunk2);
		/* Subchunk2 Size (sampledata length) */
		ByteBuffer.wrap(buffer, 40, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(sampleDataLength);
	}

	/* Decode the received IMA WAV ADPCM stream into a signed PCM16LE byte array, then return it to PlatformPlayer. */
	public ByteArrayInputStream decodeImaAdpcm(InputStream stream, int[] wavHeaderData) throws IOException
	{
		/* Remove the header from the stream, we shouldn't "decode" it as if it was a sample */
		readHeader(stream);

		final byte[] input = new byte[stream.available()];
		readInputStreamData(stream, input, 0, stream.available());

		byte[] output = decodeADPCM(input, input.length, wavHeaderData[2], wavHeaderData[3]);
		buildHeader(output, (short) wavHeaderData[2], wavHeaderData[1]); /* Builds a new header for the decoded stream. */

		return new ByteArrayInputStream(output);
	}

}
