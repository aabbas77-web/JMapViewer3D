import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;
import javax.swing.*;
   
// To play sound using Clip, the process need to be alive.
// Hence, we use a Swing application.
public class SoundClipTest extends JFrame {
   
    public Clip clip = null;
    
   // Constructor
   public SoundClipTest() {
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setTitle("Test Sound Clip");
      this.setSize(300, 200);
      this.setVisible(true);
      
      Button b = new Button();
      b.setLabel("Play");
      b.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              if (clip.isRunning()) 
                  clip.stop();
              else
                  clip.loop(Clip.LOOP_CONTINUOUSLY);
          }
      });
      this.add(b);
   
      try {
        // from a wave File
        File soundFile = new File("D:\\Ali\\WorldWind\\MapViewer\\data\\sounds\\Bomber.wav");
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);

        // Open an audio input stream.
//         URL url = this.getClass().getClassLoader().getResource("gameover.wav");
//         AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);

         // Get a sound clip resource.
         clip = AudioSystem.getClip();

         // Open audio clip and load samples from the audio input stream.
         clip.open(audioIn);
//         clip.start();
        
        // Loop()
//        clip.loop(0);  // repeat none (play once), can be used in place of start().
//        clip.loop(5);  // repeat 5 times (play 6 times)
        clip.loop(Clip.LOOP_CONTINUOUSLY);  // repeat forever

      } catch (UnsupportedAudioFileException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (LineUnavailableException e) {
         e.printStackTrace();
      }
   }
   
   public static void main(String[] args) {
      new SoundClipTest();
   }
}
