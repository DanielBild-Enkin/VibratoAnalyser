package vibratoanalyser;

/* Reads sound data from a file and presents it in a format that
 * the VibratoAnalyser can handle. */

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;


public class VAFileSource implements VADataSource {

	private File soundFile;
	private AudioInputStream audioStream;
	private AudioFormat audioFormat;
	private SourceDataLine sourceLine;

	private int nBytesRead;
	private byte[] abData;

	public VAFileSource(String filename, int bufferSize) {
		loadAudioFileOrBust(filename);
		initInputLineOrBust();

		/* Check that we can read from the file */
		nBytesRead = 0;
		abData = new byte[bufferSize];
		
		nBytesRead = readInto(abData);
	}

	public int readInto(byte[] vaData){
		try {
			nBytesRead = audioStream.read(vaData, 0, vaData.length);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return nBytesRead;
	}

	/* ------------------------------------------------------------ */

	private void loadAudioFileOrBust(String filename) {
		try {
			soundFile = new File(filename);
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
	}

	private void initInputLineOrBust() {
		
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
	}
}
