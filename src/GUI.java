import java.awt.*;
import java.awt.event.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.*;
public class GUI extends JFrame implements ActionListener {
   JPanel panel;
   JComboBox<String> branchesCombo;
   JTextField branchName_text;
   JButton submit, cancel;
   GUI() {
	   
	  
      // Username Label
	   branchesCombo = new JComboBox<String>();
	   branchName_text = new JTextField();
      // Submit
      submit = new JButton("RUN");
      panel = new JPanel(new GridLayout(3, 1));
      panel.add(branchesCombo);
      panel.add(branchName_text);
      panel.add(submit);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      // Adding the listeners to components..
      submit.addActionListener(this);
      add(panel, BorderLayout.CENTER);
      setTitle("Please Login Here !");
      setSize(450,350);
      setVisible(true);
   }
   public static void main(String[] args) {
      new GUI();
   }
   @Override
   public void actionPerformed(ActionEvent ae) {

   
   }
}