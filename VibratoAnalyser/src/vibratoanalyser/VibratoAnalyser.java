/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vibratoanalyser;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

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

    public static int[] toDoubleArray(byte[] byteArray) {
        int times = Integer.SIZE / Byte.SIZE;
        int[] doubles = new int[byteArray.length / times];
        for (int ii = 0; ii < doubles.length; ii++) {
            doubles[ii] = ByteBuffer.wrap(byteArray, ii * times, times).getInt();
        }
        return doubles;
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
        int[] doubles; 
        while (nBytesRead != -1) {

            try {
                nBytesRead = audioStream.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }

            doubles = toDoubleArray(abData);

//            System.out.println(Arrays.toString(doubles));
            sum = 0;
            for (int ii = 0; ii < doubles.length; ii++) {

                sum = sum + doubles[ii];

            }

            System.out.println(sum);

        }

        sourceLine.drain();

        sourceLine.close();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        VibratoAnalyser va = new VibratoAnalyser();
        va.playSound("/home/daniel/VibratoAnalyser/VibratoAnalyser/testdata/example_vibrato.wav");

    }

}
