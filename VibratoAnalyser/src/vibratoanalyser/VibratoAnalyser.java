/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vibratoanalyser;

import java.io.File;

import java.nio.ByteBuffer;


import java.io.PrintWriter;
import java.io.FileNotFoundException;

/**
 *
 * @author daniel
 */
public class VibratoAnalyser {

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
		VAFileSource audioFile;
		audioFile = new VAFileSource(filename, 2048);

		/* Read bytes from audio stream & find pitches */

		double sum;
		int nBytesRead;
		int[] ints;
		double[] doubles;
		byte[] abData;
		
		abData = new byte[PitchFinder.SAMPLE_COUNT*2];
		nBytesRead = 0;
		
		while (nBytesRead != -1) {
			nBytesRead = audioFile.readInto(abData);
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
		VibratoAnalyser va = new VibratoAnalyser();

		va.playSound("/home/daniel/VibratoAnalyser/VibratoAnalyser/testdata//example_vibrato.wav");


	}

}
