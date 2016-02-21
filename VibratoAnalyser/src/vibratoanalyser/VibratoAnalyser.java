/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vibratoanalyser;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import java.io.PrintWriter;
import java.io.FileNotFoundException;

/**
 *
 * @author daniel
 */
public class VibratoAnalyser {

	private final int BUFFER_SIZE = 128000;
	private File soundFile;
	private AudioInputStream audioStream;
	private AudioFormat audioFormat;
	private SourceDataLine sourceLine;

	public VibratoAnalyser() {

	}

	public static int[] toIntArray(byte[] byteArray) {
		int times = Short.SIZE / Byte.SIZE;
		int[] ints = new int[byteArray.length / times];
		for (int ii = 0; ii < ints.length; ii++) {
			ints[ii] = ByteBuffer.wrap(byteArray, ii * times, times).getShort();
		}
		return ints;
	}

	public void playSound(String filename) {

		String strFilename = filename;

		try {
			soundFile = new File(strFilename);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			audioStream = AudioSystem.getAudioInputStream(soundFile);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		audioFormat = audioStream.getFormat();

		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		try {
			sourceLine = (SourceDataLine) AudioSystem.getLine(info);
			sourceLine.open(audioFormat);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		sourceLine.start();

		int nBytesRead = 0;
		byte[] abData = new byte[1024 * 16];
//
		try {
			nBytesRead = audioStream.read(abData, 0, abData.length);
		} catch (IOException e) {
			e.printStackTrace();
		}

		double sum;
		int[] ints;
		double[] doubles;
		while (nBytesRead != -1) {
			try {
				nBytesRead = audioStream.read(abData, 0, abData.length);
			} catch (IOException e) {
				e.printStackTrace();
			}

			ints = toIntArray(abData);
			//save_array("ints.txt", ints);

			doubles = new double[ints.length];
			for (int i=0; i<ints.length; i++) {
				doubles[i] = (double) ints[i];
			}

			System.out.println(PitchFinder.find_pitch(doubles));
		}

	}

	private static void save_array(String filename, int[] xs) {
		PrintWriter pout;
		try {
			pout = new PrintWriter(filename);

			for(int i=0; i<xs.length; i++) {
				pout.println(Integer.toString(xs[i]));
			}
			pout.close();
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't save file");
			System.exit(1);
		}
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		// TODO code application logic here
		VibratoAnalyser va = new VibratoAnalyser();
		va.playSound("testdata/example_vibrato.wav");

	}

}
