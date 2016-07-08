package com.example.utl;

import java.io.*;

public class PCM2WAV {
	
	
	public final char fileID[] = {'R', 'I', 'F', 'F'};
	public int fileLength;
	public char wavTag[] = {'W', 'A', 'V', 'E'};;
	public char FmtHdrID[] = {'f', 'm', 't', ' '};
	public int FmtHdrLeth;
	public short FormatTag;
	public short Channels;
	public int SamplesPerSec;
	public int AvgBytesPerSec;
	public short BlockAlign;
	public short BitsPerSample;
	public char DataHdrID[] = {'d','a','t','a'};
	public int DataHdrLeth;
	
	
	public byte[] getHeader() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		WriteChar(bos, fileID);
		WriteInt(bos, fileLength);
		WriteChar(bos, wavTag);
		WriteChar(bos, FmtHdrID);
		WriteInt(bos,FmtHdrLeth);
		WriteShort(bos,FormatTag);
		WriteShort(bos,Channels);
		WriteInt(bos,SamplesPerSec);
		WriteInt(bos,AvgBytesPerSec);
		WriteShort(bos,BlockAlign);
		WriteShort(bos,BitsPerSample);
		WriteChar(bos,DataHdrID);
		WriteInt(bos,DataHdrLeth);
		bos.flush();
		byte[] r = bos.toByteArray();
		bos.close();
		return r;
	}

	
	private void WriteShort(ByteArrayOutputStream bos, int s) throws IOException {
		byte[] mybyte = new byte[2];
		mybyte[1] =(byte)( (s << 16) >> 24 );
		mybyte[0] =(byte)( (s << 24) >> 24 );
		bos.write(mybyte);
	}
	
	
	private void WriteInt(ByteArrayOutputStream bos, int n) throws IOException {
		byte[] buf = new byte[4];
		buf[3] =(byte)( n >> 24 );
		buf[2] =(byte)( (n << 8) >> 24 );
		buf[1] =(byte)( (n << 16) >> 24 );
		buf[0] =(byte)( (n << 24) >> 24 );
		bos.write(buf);
	}
	
	
	private void WriteChar(ByteArrayOutputStream bos, char[] id) {
		for (int i=0; i<id.length; i++) {
			char c = id[i];
			bos.write(c);
		}
	}
	
	//直接调用这个方法就行了
	public int convertAudioFiles(String src, String target) throws Exception {
		FileInputStream fis = new FileInputStream(src);
		FileOutputStream fos = new FileOutputStream(target);
		byte[] buf = new byte[1024 * 4];
		int size = fis.read(buf);
		int PCMSize = 0;
		while (size != -1) {
			PCMSize += size;
			size = fis.read(buf);
		}
		fis.close();
		/**
		 * header.Channels = 1;
		 * header.BitsPerSample = 16;
		 * header.SamplesPerSec = 8000;
		 * 
		 * header.Channels = 1;
		 * header.BitsPerSample = 16;
		 * header.SamplesPerSec = 11025;
		 * 
		 */
		PCM2WAV header = new PCM2WAV();
		header.fileLength = PCMSize + 32;
		header.FmtHdrLeth = 16;
		header.BitsPerSample = 16;
		header.Channels = 1;
//		header.Channels = 2;
		header.FormatTag = 0x0001;
		header.SamplesPerSec = 8000;
		header.BlockAlign = (short)(header.Channels * header.BitsPerSample / 8);
		header.AvgBytesPerSec = header.BlockAlign * header.SamplesPerSec;
//		header.AvgBytesPerSec = 22050;
		header.DataHdrLeth = PCMSize;
		byte[] h = header.getHeader();
		assert h.length == 44;
		fos.write(h, 0, h.length);
		//write data stream
		fis = new FileInputStream(src);
		size = fis.read(buf);
		while (size != -1) {
			fos.write(buf, 0, size);
			size = fis.read(buf);
		}
		fis.close();
		fos.close();
		return 1;
	}
	
}