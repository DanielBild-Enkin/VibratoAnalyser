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
    public static final int SAMPLE_COUNT = 4096;
    private static final int WAV_FREQ = 44100;
    private static final Random RNG = new Random(19890528L);

    private static boolean doit = true;

    private static void hanning_window(double[] xs) {
        for (int i = 0; i < xs.length; i++) {
            xs[i] *= 0.5 * (1 - Math.cos(2 * Math.PI * i / (xs.length - 1)));
        }
    }

    private static int array_max_index(double[] xs) {
        int max_i = 0;
        double max_x = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < xs.length; i++) {
            if (xs[i] > max_x) {
                max_i = i;
                max_x = xs[i];
            }
        }
        return max_i;
    }

    private static double[] abs(Complex[] xs) {
        double[] m = new double[xs.length];
        for (int i = 0; i < xs.length; i++) {
            m[i] = xs[i].abs();
        }
        return m;
    }

    private static Complex[] fftshift(Complex[] c) {

        Complex shiftC[] = new Complex[c.length];
        System.arraycopy(c, c.length / 2, shiftC, 0, c.length / 2);
        System.arraycopy(c, 0, shiftC, c.length / 2, c.length / 2);

        return shiftC;
    }

    private static double[] autoCorrelateFft(double[] sample, int iter) {
        // take fourier transform of audio sample

//        hanning_window(sample);
        Complex c[] = transformer.transform(sample, TransformType.FORWARD);
//        c[0] = new Complex(0);

//            for (int i = 0; i < 1; i++) {
//
//                c[i] = new Complex(0); // 0 doesn't count; it's perfectly correlated
//            };
//            
//            for (int i = 464; i < SAMPLE_COUNT/2+464; i++) {
//
//                c[i] = new Complex(0); // 0 doesn't count; it's perfectly correlated
//            };
//            
//            
//            for (int i = SAMPLE_COUNT-2; i < SAMPLE_COUNT-1; i++) {
//
//                c[i] = new Complex(0); // 0 doesn't count; it's perfectly correlated
//            };
        // autocorrelate... maybe the first half?
//        Complex shiftC[] = fftshift(c);
        Complex shiftC[] = Arrays.copyOfRange(c, 0, sample.length / 2);
        shiftC = transformer.transform(shiftC, TransformType.FORWARD);
        for (int i = 0; i < shiftC.length; i++) {
            shiftC[i] = shiftC[i].multiply(shiftC[i].conjugate());
        }
        shiftC = transformer.transform(shiftC, TransformType.INVERSE);

        if (iter < 10) {

            String fourierName = String.format("fourier_transform_%d.txt", iter);
//            save_array(fourierName, abs(Arrays.copyOfRange(c, 0, c.length / 2)));
            save_array(fourierName, abs(c));
            String correlateName = String.format("correlate_%d.txt", iter);
//            save_array(correlateName, abs(Arrays.copyOfRange(shiftC, 0, shiftC.length / 2)));

//            for (int i = 0; i < 2; i++) {
//
//                shiftC[i] = new Complex(0); // 0 doesn't count; it's perfectly correlated
//            };
            save_array(correlateName, abs(shiftC));
            String sampleName = String.format("sample_%d.txt", iter);
            save_array(sampleName, sample);
//            doit = false;
        }

        return abs(shiftC);

    }

    public static double find_pitch(double[] sample, int iter) {
        double[] correlation = autoCorrelateFft(sample, iter);
//        for (int i = 0; i < 1; i++) {
//
//            correlation[i] = 0; // 0 doesn't count; it's perfectly correlated
//        };

        int i = array_max_index(Arrays.copyOfRange(correlation, 0, correlation.length / 2));
        return (double) i ;//* WAV_FREQ / SAMPLE_COUNT;
    }

    private static void save_array(String filename, double[] xs) {
        PrintWriter pout;
        try {
            pout = new PrintWriter(filename);

            for (int i = 0; i < xs.length; i++) {
                pout.println(Double.toString(xs[i]));
            }
            pout.close();
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't save file");
            System.exit(1);
        }
    }


    /* Test */
    public static void main(String[] args) {
        double[] sample = new double[SAMPLE_COUNT];

        for (int i = 0; i < SAMPLE_COUNT; i++) {
            sample[i] = i;
        }

        double[] xs = new double[SAMPLE_COUNT];
        for (int i = 0; i < xs.length; i++) {
            xs[i] = 10 * RNG.nextDouble();
            for (int j = 0; j < 5; j++) {
                xs[i] += (Math.sin((100.5) * (j + 1) * (2 * Math.PI) * i / SAMPLE_COUNT));
                //+ RNG.nextDouble() * 1 - 0.5
            }
        }

//        save_array("xs.txt", xs);
        double pitch = PitchFinder.find_pitch(xs, 0);
        System.out.println(pitch);

    }

}
