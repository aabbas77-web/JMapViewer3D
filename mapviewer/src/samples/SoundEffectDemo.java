import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
   
// Testing the SoundEffect enum in a Swing application
public class SoundEffectDemo extends JFrame {
   
   // Constructor
   public SoundEffectDemo() {
      // Pre-load all the sound files
      SoundEffect.init();
      SoundEffect.volume = SoundEffect.Volume.LOW;  // un-mute
   
      // Set up UI components
      Container cp = this.getContentPane();
      cp.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
      JButton btnSound1 = new JButton("Sound 1");
      btnSound1.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            SoundEffect.EXPLODE.play();
         }
      });
      cp.add(btnSound1);
      JButton btnSound2 = new JButton("Sound 2");
      btnSound2.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            SoundEffect.GONG.play();
         }
      });
      cp.add(btnSound2);
      JButton btnSound3 = new JButton("Sound 3");
      btnSound3.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            SoundEffect.SHOOT.play();
         }
      });
      cp.add(btnSound3);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setTitle("Test SoundEffct");
      this.pack();
      this.setVisible(true);
   }
   
   public static void main(String[] args) {
      new SoundEffectDemo();
   }
}
