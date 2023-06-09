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
import java.util.Arrays;

public class WavImaAdpcmDecoder
{
	/* Information about this audio format: https://wiki.multimedia.cx/index.php/IMA_ADPCM */

	/* Variables to hold the previously decoded sample and step used, per channel (if needed) */
	private static int[] prevSample;
	private static int[] prevStep;

	/* 
	 * This one acts as the simplest low-pass filter ever, dividing each decoded sample by
	 * the given amount. Anything from '2' to '4' vastly improves the resulting audio quality 
	 * by cutting off more of the high range which crackles a lot. '3' is a good middle-ground
	 * between 2's clearer but still prone to crackling and 4's more consistent but muffled sound.
	 */
	private static final int smoothing = 3;
	
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
	 * Note: Largely based on ffmpeg's implementation.
	 */
	public static byte[] decodeADPCM(byte[] input, int inputSize, int numChannels, int block_size)
	{
		byte[] output;
		byte adpcmSample;
		int inputIndex = 0;
		int outputIndex = 44; /* Give some space for the header */
		int outputSize, decodedSample; 
		short curChannel;

		prevSample = new int[2];
		prevStep = new int[2];
		
		outputSize = 44 + (inputSize * 4);
		output = new byte[outputSize];

		while (inputSize > 0) 
		{
			if (inputSize % block_size == 0) 
			{
				prevSample[0] = (int) (((input[inputIndex]& 0xFF)) | ((input[inputIndex+1]) << 8) & 0xFF00);
				prevStep[0] = (int) (input[inputIndex+2]);
				inputIndex += 4;
				inputSize -= 4;
				outputSize -= 16;

				if (numChannels > 1) 
				{
					prevSample[1] = (int) (((input[inputIndex]& 0xFF)) | ((input[inputIndex+1]) << 8) & 0xFF00);
					prevStep[1] = (int) (input[inputIndex + 2]);
					inputIndex += 4;
					inputSize -= 4;
					outputSize -= 16;
				}
			}
			
			/* In the very rare (pretty much non-existent) cases where some j2me app 
			 * might use stereo ima adpcm, we should decode each audio channel. 
			 * Again largely based on ffmpeg. */
			if (numChannels > 1) 
			{
				for (short i = 0; i < 8; i++) 
				{
					if(i < 4) { curChannel = 0; }
					else      { curChannel = 1; }

					adpcmSample = (byte)(input[inputIndex] & 0x0f);
					decodedSample = decodeSample(curChannel, adpcmSample);
					output[outputIndex + ((i & 3) << 3) + (curChannel << 1)] = (byte)(decodedSample & 0xff);
					output[outputIndex + ((i & 3) << 3) + (curChannel << 1) + 1] = (byte)(decodedSample >> 8);

					adpcmSample = (byte)((input[inputIndex] >> 4) & 0x0f);
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
				adpcmSample = (byte)(input[inputIndex] & 0x0f);
				decodedSample = decodeSample(0, adpcmSample);
				output[outputIndex++] = (byte)(decodedSample & 0xff);
				output[outputIndex++] = (byte)((decodedSample >> 8) & 0xff);

				adpcmSample = (byte)((input[inputIndex] >> 4) & 0x0f);
				decodedSample = decodeSample(0, adpcmSample);
				output[outputIndex++] = (byte)(decodedSample & 0xff);
				output[outputIndex++] = (byte)((decodedSample >> 8) & 0xff);

				inputIndex++;
				inputSize--;
			}
		}
		
		return output;
	}

	/* This method will decode a single IMA ADPCM sample to linear PCM_S16LE sample. */
	static short decodeSample(int channel, int adpcmSample)
	{
		int stepSize;
		int decoded;
		stepSize = ima_step_size_table[prevStep[channel]] & 0x0000FFFF;
		decoded = (stepSize >> 3) & 0x1fff;
		
		if ((adpcmSample & 1) != 0) { decoded += (stepSize >> 2) & 0x3fff; }
		if ((adpcmSample & 2) != 0) { decoded += (stepSize >> 1) & 0x7fff; }
		if ((adpcmSample & 4) != 0) { decoded += stepSize; }
		if ((adpcmSample & 8) != 0) { decoded  = -(short) decoded; }
		
		decoded += (short) prevSample[channel];
		decoded = decoded / smoothing;
		
		if ((short) decoded < -32768)     { decoded = -32768; }
		else if ((short) decoded > 32767) { decoded = 32767; }
		
		prevSample[channel] = (short) decoded;
		prevStep[channel] += ima_step_index_table[(int)(adpcmSample & 0x0FF)];

		if (prevStep[channel] < 0)       { prevStep[channel] = 0; }
		else if (prevStep[channel] > 88) { prevStep[channel] = 88; }

		return (short) (decoded & 0xFFFF);
	}
	
	/*
	 * Since the InputStream is always expected to be positioned right at the start
	 * of the byte data, read WAV file's header to determine its type.
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
				  UINT16 frame Size (256 on some gameloft games)
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
		 * dataSize + SampleRate + audioChannels to decode ADPCM and build the new header. 
		 */
		return new int[] {audioFormat, dataSize, sampleRate, audioChannels};
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
	private void buildHeader(byte[] buffer, int numChannels, int sampleRate) 
	{ 
		final short bitsPerSample = 16;   /* 16-bit PCM */
		final short audioFormat = 1;      /* WAV linear PCM */
		final int subChunkSize = 16;      /* Fixed size for Wav Linear PCM*/
		final int chunk = 0x46464952;     /* 'RIFF' */
		final int format = 0x45564157;    /* 'WAVE' */
		final int subChunk1 = 0x20746d66; /* 'fmt ' */
		final int subChunk2 = 0x61746164; /* 'data' */

		/* 
		 * We'll have 16 bits per sample, so each sample has 2 bytes, with that we just divide
		 * the size of the byte buffer (minus the header) by 2, then multiply by the amount 
		 * of channels... assuming i didn't mess anything up, which is likely with this much code.
		*/
		final int samplesPerChannel = ((buffer.length-44) / 2) * numChannels;

		final short frameSize = (short) (numChannels * bitsPerSample / 8);
		final int sampleDataLength = samplesPerChannel * frameSize;
		final int bytesPerSec = sampleRate * frameSize;
		final int dataSize = 36 + sampleDataLength;

		/* ChunkID */
		buffer[0]  = (byte)  (chunk & 0xff);
		buffer[1]  = (byte) ((chunk >>> 8)  & 0xff);
		buffer[2]  = (byte) ((chunk >>> 16) & 0xff);
		buffer[3]  = (byte) ((chunk >>> 24) & 0xff);
		/* ChunkSize (or File size) */
		buffer[4]  = (byte)  (dataSize & 0xff); 
		buffer[5]  = (byte) ((dataSize >> 8)  & 0xff);
		buffer[6]  = (byte) ((dataSize >> 16) & 0xff);
		buffer[7]  = (byte) ((dataSize >> 24) & 0xff);
		/* Format (WAVE) */
		buffer[8]  = (byte)  (format & 0xff);
		buffer[9]  = (byte) ((format >>> 8 ) & 0xff);
		buffer[10] = (byte) ((format >>> 16) & 0xff);
		buffer[11] = (byte) ((format >>> 24) & 0xff);
		/* SubchunkID (fmt) */
		buffer[12] = (byte)  (subChunk1 & 0xff);
		buffer[13] = (byte) ((subChunk1 >>> 8)  & 0xff);
		buffer[14] = (byte) ((subChunk1 >>> 16) & 0xff);
		buffer[15] = (byte) ((subChunk1 >>> 24) & 0xff);
		/* SubchunkSize (or format chunk size) */
		buffer[16] = (byte)  (subChunkSize & 0xff);
		buffer[17] = (byte) ((subChunkSize >> 8)  & 0xff);
		buffer[18] = (byte) ((subChunkSize >> 16) & 0xff);
		buffer[19] = (byte) ((subChunkSize >> 24) & 0xff);
		/* Audioformat */
		buffer[20] = (byte)  (audioFormat & 0xff);
		buffer[21] = (byte) ((audioFormat >> 8) & 0xff);
		/* NumChannels (will be the same as source ADPCM) */
		buffer[22] = (byte)  (numChannels & 0xff);
		buffer[23] = (byte) ((numChannels >> 8) & 0xff);
		/* SampleRate (will be the same as source ADPCM) */
		buffer[24] = (byte)  (sampleRate & 0xff);
		buffer[25] = (byte) ((sampleRate >> 8)  & 0xff);
		buffer[26] = (byte) ((sampleRate >> 16) & 0xff);
		buffer[27] = (byte) ((sampleRate >> 24) & 0xff);
		/* ByteRate (BytesPerSec) */
		buffer[28] = (byte)  (bytesPerSec & 0xff);
		buffer[29] = (byte) ((bytesPerSec >> 8)  & 0xff);
		buffer[30] = (byte) ((bytesPerSec >> 16) & 0xff);
		buffer[31] = (byte) ((bytesPerSec >> 24) & 0xff);
		/* BlockAlign (Frame Size) */
		buffer[32] = (byte)  (frameSize & 0xff);
		buffer[33] = (byte) ((frameSize >> 8) & 0xff);
		/* BitsPerSample */
		buffer[34] = (byte)  (bitsPerSample & 0xff);
		buffer[35] = (byte) ((bitsPerSample >> 8) & 0xff);
		/* Subchunk2ID (data) */
		buffer[36] = (byte)  (subChunk2 & 0xff);
		buffer[37] = (byte) ((subChunk2 >>> 8)  & 0xff);
		buffer[38] = (byte) ((subChunk2 >>> 16) & 0xff);
		buffer[39] = (byte) ((subChunk2 >>> 24) & 0xff);
		/* Subchunk2 Size (sampledata length) */
		buffer[40] = (byte)  (sampleDataLength & 0xff);
		buffer[41] = (byte) ((sampleDataLength >> 8)  & 0xff);
		buffer[42] = (byte) ((sampleDataLength >> 16) & 0xff);
		buffer[43] = (byte) ((sampleDataLength >> 24) & 0xff);
	}

	/* Decode the received IMA WAV ADPCM stream into a signed PCM16LE byte array, then return it to PlatformPlayer. */
	public ByteArrayInputStream decodeImaAdpcm(InputStream stream, int[] wavHeaderData) throws IOException
	{
		byte[] input = new byte[stream.available()];
		readInputStreamData(stream, input, 0, stream.available());

		byte[] output = decodeADPCM(input, input.length, wavHeaderData[3], wavHeaderData[1]);
		buildHeader(output, wavHeaderData[3], wavHeaderData[2]); /* Builds a new header for the decoded stream. */

		return new ByteArrayInputStream(output);
	}

}
