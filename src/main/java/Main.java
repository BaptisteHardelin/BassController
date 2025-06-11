import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer.Info;

public class Main {

    // Focusrite USB audio index 12
    public static void getAvailableInput() {
        System.out.println("List Of Available Input :");
        Info[] mixers = AudioSystem.getMixerInfo();
        for (int i = 0; i < mixers.length; i++) {
            System.out.println(i + ": " + mixers[i].getName() + " - " + mixers[i].getDescription());
        }
    }

    public static void main(String[] args) {
        getAvailableInput();
    }
}
