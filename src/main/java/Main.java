import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

public class Main {

    // Focusrite USB audio index 12
    public static void main(String[] args) {
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        for (int i = 0; i < mixers.length; i++) {
            System.out.println(i + ": " + mixers[i].getName());
        }
    }
}
