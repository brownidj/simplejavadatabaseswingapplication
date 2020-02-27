package org.topository.test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;


public class Mainframe extends JFrame implements ActionListener {

    public void createGUI(){
        setTitle("File Locker");
        fileSelectBtn = new JButton("::");
        fileSelectBtn.addActionListener(this);

        pb = new JProgressBar(0, 100);
        //  pb.setValue(0);
        pb.setIndeterminate(false);
        pb.setStringPainted(true);
        pb.setVisible(true);

        JPanel panel = new JPanel();
        label = new JLabel("File");

        textField = new JTextField(15);
        //  textField.setEditable(false);
        panel.add(label);
        panel.add(textField);
        panel.add(fileSelectBtn);


        JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());
        panel1.add(panel, BorderLayout.NORTH);

        JPanel panel2 = new JPanel();
        encryptBtn    = new JButton("Encrypt");
        encryptBtn.addActionListener(this);
        panel2.add(encryptBtn);
        panel2.add(pb);
        panel1.add(panel2, BorderLayout.CENTER);
        panel1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(panel1);
        pack();
        setResizable(false);
        setSize(400, 150);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(fileSelectBtn)){
            JFileChooser filechooser = new JFileChooser(".");

            if (filechooser.showOpenDialog(this) ==  JFileChooser.APPROVE_OPTION)
            {
                selectedFile = filechooser.getSelectedFile();
                textField.setText(selectedFile.getAbsolutePath());
                textField.setToolTipText(selectedFile.getAbsolutePath());
            }
        }else if(e.getSource().equals(encryptBtn)){
            FileInputStream fin = null;
            try {
                // create FileInputStream object
                if(selectedFile == null){
                    JOptionPane.showMessageDialog(this, "Please select any file.", "Error Message", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                fin = new FileInputStream(selectedFile);
                long length = selectedFile.length();
                byte fileContent[] = new byte[(int)selectedFile.length()];

                // Reads up to certain bytes of data from this input stream into an array of bytes.
                fin.read(fileContent);
                fin.close();

                FileEncryptPerformer actionPerform = new FileEncryptPerformer(this,
                        fileContent,
                        selectedFile.getAbsolutePath(),
                        pb);
                actionPerform.execute();

            }
            catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "File not found.", "Error Message", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("File not found" + ex);
            }
            catch (IOException ioe) {
                JOptionPane.showMessageDialog(this, "Exception while reading file.", "Error Message", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("Exception while reading file " + ioe);
            }
            finally {
                // close the streams using close method
                try {
                    if (fin != null) {
                        fin.close();
                    }
                }
                catch (IOException ioe) {
                    JOptionPane.showMessageDialog(this, "Error while closing stream.", "Error Message", JOptionPane.INFORMATION_MESSAGE);
                    System.out.println("Error while closing stream: " + ioe);
                }
            }
        }
    }

    private JButton fileSelectBtn;
    private JButton encryptBtn;
    private JProgressBar pb;
    JTextField textField;
    JLabel label;
    File selectedFile;

}