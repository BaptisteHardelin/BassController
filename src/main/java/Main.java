import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.Yin;

import javax.sound.sampled.*;
import javax.sound.sampled.Mixer.Info;

public class Main {

    // Focusrite USB audio index 12
    // https://0110.be/releases/TarsosDSP/TarsosDSP-latest/TarsosDSP-latest-Documentation/w
    public static void getAvailableInput() {
        System.out.println("List Of Available Input :");
        Info[] mixers = AudioSystem.getMixerInfo();
        for (int i = 0; i < mixers.length; i++) {
            System.out.println(i + ": " + mixers[i].getName() + " - " + mixers[i].getDescription());
        }
    }

    public static void getBassNoteFromAudioCard() throws LineUnavailableException {
        float sampleRate = 44100;
        int bufferSize = 1024;
        int overlap = 0;
        int selectedMixerIndex = 12; // Vérifie bien que c'est la Focusrite

        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        Mixer mixer = AudioSystem.getMixer(mixers[selectedMixerIndex]);

        AudioFormat format = new AudioFormat(sampleRate, 16, 2, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine line = (TargetDataLine) mixer.getLine(info);
        line.open(format, bufferSize * 2);
        line.start();

        AudioInputStream audioStream = new AudioInputStream(line);
        JVMAudioInputStream jvmAudioStream = new JVMAudioInputStream(audioStream);

        AudioDispatcher dispatcher = new AudioDispatcher(jvmAudioStream, bufferSize, overlap);

        dispatcher.addAudioProcessor(new AudioProcessor() {

            Yin yin = new Yin(sampleRate, bufferSize / 2);

            @Override
            public boolean process(AudioEvent audioEvent) {
                float[] stereoBuffer = audioEvent.getFloatBuffer();
                float[] monoBuffer = new float[stereoBuffer.length / 2];

                for (int i = 0; i < monoBuffer.length; i++) {
                    monoBuffer[i] = stereoBuffer[2 * i + 1];
                }

                PitchDetectionResult result = yin.getPitch(monoBuffer);
                float pitch = result.getPitch();
                if (pitch > 0) {
                    System.out.println("Frequency : " + pitch + " Hz — Note : " + frequencyToNote(pitch));
                }

                return true;
            }

            @Override
            public void processingFinished() {
            }
        });

        new Thread(dispatcher, "Audio Dispatcher").start();
    }

    private static String frequencyToNote(float freq) {
        String[] notes = {
                "Do", "Do#", "Ré", "Ré#", "Mi", "Fa", "Fa#", "Sol", "Sol#", "La", "La#", "Si"
        };
        final double A4 = 440.0;
        final int A4_MIDI = 69;
        int midiNumber = (int) Math.round(12 * (Math.log(freq / A4) / Math.log(2)) + A4_MIDI);
        int noteIndex = midiNumber % 12;
        int octave = (midiNumber / 12) - 1;

        return notes[noteIndex] + octave;
    }


    public static void main(String[] args) throws LineUnavailableException {
        // getAvailableInput();
        getBassNoteFromAudioCard();
    }
}
