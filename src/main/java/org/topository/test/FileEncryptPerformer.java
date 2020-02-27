package org.topository.test;

import java.awt.Cursor;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

class FileEncryptPerformer extends SwingWorker {

    JProgressBar fProgressBar;
    private byte fileContent[];
    private String fileName;
    Mainframe mainFrame ;

    public FileEncryptPerformer(Mainframe mainFrame, byte fileContent[],String fileName, JProgressBar progressBar) {
        this.mainFrame   = mainFrame;
        this.fileContent = fileContent;
        this.fileName = fileName;
        this.fProgressBar = progressBar;
    }

    protected String doInBackground() throws Exception {
        mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        setStatusText(0);
        encrypt();
        writeToFile();
        return "Finished";
    }

    private void writeToFile() throws IOException {
        BufferedOutputStream bos = null;
        try {
            //create an object of FileOutputStream
            FileOutputStream fos = new FileOutputStream(new File(fileName));
            //create an object of BufferedOutputStream
            bos = new BufferedOutputStream(fos);
            bos.write(fileContent);

        }catch (FileNotFoundException e) {
            if(bos !=null)
                bos.close();
            done();
            JOptionPane.showMessageDialog(mainFrame, "File not found.", "Error Message", JOptionPane.INFORMATION_MESSAGE);
            return ;
        }
        catch (IOException e) {
            if(bos !=null)
                bos.close();
            done();
            JOptionPane.showMessageDialog(mainFrame, "Exception while reading file.", "Error Message", JOptionPane.INFORMATION_MESSAGE);
            return ;
        }

    }

    protected void done() {
        mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        setStatusText(100);

    }

    public void encrypt() {
        int stepSize = fileContent.length/100;
        for ( int i = 0; i < fileContent.length; i++) {
            fileContent[i] = (byte) (fileContent[i] ^ (byte)1);
            float stepCount = (float)i/fileContent.length;
            final int count = (int)(stepCount*100);
            //fProgressBar.setValue(count);
            setStatusText(count);
        }
    }

    public void setStatusText(final int status) {
        SwingUtilities.invokeLater(new Runnable(){

            //@Override
            public void run() {

                fProgressBar.setValue(status);
            }

        });
    }
}
