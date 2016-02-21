package vibratoanalyser;

// Fourier Transform Stuff
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.complex.Complex;

// Testing Stuf
import java.io.PrintWriter;
import java.io.FileNotFoundException;

import java.util.Random;
import java.util.Arrays;
import java.lang.Math;


public class PitchFinder {

	private static FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
	private static final int SAMPLE_COUNT = 1024;
	private static final int WAV_FREQ = 44100;
	private static final Random RNG = new Random(19890528L);


	private static int array_max_index(double[] xs) {
		int max_i = 0;
		double max_x = Double.NEGATIVE_INFINITY;
		for (int i=0; i<xs.length; i++) {
			if (xs[i] > max_x) {
				max_i = i;
				max_x = xs[i];
			}
		}
		return max_i;
	}

	private static double[] abs(Complex[] xs) {
		double[] m = new double[xs.length];
		for(int i=0; i<xs.length; i++) {
			m[i] = xs[i].abs();
		}
		return m;
	}

	private static double[] abs_squared_fft(double[] sample) {
		// take fourier transform of audio sample
		Complex c[] = transformer.transform(sample, TransformType.FORWARD);
		// autocorrelate... maybe the first half?
		Complex half_c[] = c; // Arrays.copyOfRange(c, 0, sample.length/2);
		half_c = transformer.transform(half_c, TransformType.FORWARD);
		for(int i=0; i<half_c.length; i++) {
			half_c[i] = half_c[i].multiply(half_c[i].conjugate());
		}
		half_c = transformer.transform(half_c, TransformType.INVERSE);
		return abs(half_c);
	}

	public static double find_pitch(double[] sample) {
		double[] correlation = abs_squared_fft(sample);
		correlation[0] = 0; // 0 doesn't count; it's perfectly correlated
		int i = array_max_index(correlation);
		return (double) i * WAV_FREQ / SAMPLE_COUNT;
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


	/* Test */
	/*
	public static void main(String[] args) {
		double[] sample = new double[SAMPLE_COUNT];

		for(int i=0; i<SAMPLE_COUNT; i++) {
			sample[i] = i;
		}

		double[] xs = new double[SAMPLE_COUNT];
		for (int i=0; i<xs.length; i++){
			xs[i] = RNG.nextDouble();
			for(int j=0; j<10; j++) {
				xs[i] += (Math.sin((100 + RNG.nextDouble() * 1 - 0.5) * j * (2*Math.PI) * i/SAMPLE_COUNT));
			}
		}
	}
	*/

}
