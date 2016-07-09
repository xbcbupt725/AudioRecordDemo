package com.sogou.speech.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.sogou.speech.settings.IRecordAudioConfig;

import android.util.Log;

public class WavHelper implements IRecordAudioConfig{
	private static final String TAG = "WavHelper";

	public static void constructWav(OutputStream os, ByteOrder bo,
			byte[] byteData) throws IOException {
		WavHelper.addWavHeadChars(os, "RIFF".toCharArray());
		int file_size0 = byteData.length + 44 - 8;
		WavHelper.addWavHeadInt(os, bo, file_size0);
		WavHelper.addWavHeadChars(os, "WAVEfmt".toCharArray());
		WavHelper.addWavHeadByte(os, (byte) 0x20);
		WavHelper.addWavHeadInt(os, bo, 0x10);
		WavHelper.addWavHeadShort(os, bo, (short) 0x01);
		WavHelper.addWavHeadShort(os, bo, (short) 1);
		WavHelper.addWavHeadInt(os, bo, 16000);
		WavHelper.addWavHeadInt(os, bo, 32000);
		WavHelper.addWavHeadShort(os, bo, (short) 2);
		WavHelper.addWavHeadShort(os, bo, (short) 16);
		WavHelper.addWavHeadChars(os, "data".toCharArray());
		WavHelper.addWavHeadInt(os, bo, byteData.length);

		int i = 0;
		/**
		 *  remove encrypt part. 2016-6-2
		 */
//		byte[] encryptedData = null;
//		encryptedData = CommonUtils.RC4encrypt(byteData, KEY);
		os.write(byteData);
		/*for (byte b : encryptedData) {
			i++;
			WavHelper.addWavHeadByte(os, b);
		}*/
		Log.d(TAG, "pcm length " + i);
	}

	public static byte[] constructWavHeader(byte[] byteData) {
		ByteBuffer byteBuf = ByteBuffer.allocate(HEADER_SIZE);
		try {
			byteBuf.put("RIFF".getBytes("ASCII"));
			byteBuf.putInt(byteData.length + 44 - 8);
			byteBuf.put("WAVEfmt".getBytes("ASCII"));
			byteBuf.put((byte) 0x20);
			byteBuf.putInt(0x10);
			byteBuf.putShort((short) 0x01);
			byteBuf.putShort((short) 1);
			byteBuf.putInt(16000);
			byteBuf.putInt(32000);
			byteBuf.putShort((short) 2);
			byteBuf.putShort((short) 16);
			byteBuf.put("data".getBytes("ASCII"));
			byteBuf.putInt(byteData.length);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byteBuf.flip();
		return byteBuf.array();
	}
	
	
	public static void addWavHeadInt(OutputStream os, ByteOrder bo, int addone)
			throws IOException {
		// if (bo.equals(ByteOrder.BIG_ENDIAN)) {
		os.write((addone >> 0) & 0x000000ff);
		os.write((addone >> 8) & 0x000000ff);
		os.write((addone >> 16) & 0x000000ff);
		os.write((addone >> 24) & 0x000000ff);
		// } else {
		// os.write((addone >> 24) & 0x000000ff);
		// os.write((addone >> 16) & 0x000000ff);
		// os.write((addone >> 8) & 0x000000ff);
		// os.write((addone >> 0) & 0x000000ff);
		// }
	}

	public static void addWavHeadByte(OutputStream os, byte addone)
			throws IOException {
		os.write(addone);
	}

	public static void addWavHeadChar(OutputStream os, char addone)
			throws IOException {
		os.write(addone);
	}

	public static void addWavHeadChars(OutputStream os, char[] addone)
			throws IOException {
		for (char c : addone) {
			os.write(c);
		}
	}

	public static void addWavHeadShort(OutputStream os, ByteOrder bo,
			short addone) throws IOException {
		// if (bo.equals(ByteOrder.BIG_ENDIAN)) {
		os.write((addone >> 0) & 0x000000ff);
		os.write((addone >> 8) & 0x000000ff);
		// } else {
		// os.write((addone >> 8) & 0x000000ff);
		// os.write((addone >> 0) & 0x000000ff);
		// }
	}

	private static final int HEADER_SIZE = 44;

	public static int readHeader(InputStream wavStream) throws IOException {

		ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		wavStream.read(buffer.array(), buffer.arrayOffset(), buffer.capacity());

		buffer.rewind();
		buffer.position(buffer.position() + 20);
		int format = buffer.getShort();
		Log.d(TAG, "Unsupported encoding: " + format); // 1 means
																		// Linear
																		// PCM
		int channels = buffer.getShort();
		Log.d(TAG, "channels: "
				+ channels);
		int rate = buffer.getInt();
		Log.d(TAG, "rate: " + rate);
		buffer.position(buffer.position() + 6);
		int bits = buffer.getShort();
		Log.d(TAG, "bits: " + bits);
		int dataSize = 0;
		while (buffer.getInt() != 0x61746164) { // "data" marker
			Log.d(TAG, "Skipping non-data chunk");
			int size = buffer.getInt();
			wavStream.skip(size);

			buffer.rewind();
			wavStream.read(buffer.array(), buffer.arrayOffset(), 8);
			buffer.rewind();
		}
		dataSize = buffer.getInt();
		Log.d(TAG, "datasize: " + dataSize);

		return dataSize;
	}
}
