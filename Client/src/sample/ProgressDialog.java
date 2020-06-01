package sample;

import javax.swing.*;
import java.awt.*;

public class ProgressDialog {
    private final JFrame frame = new JFrame();
    private final JDialog dialog = new JDialog(frame, "Выполнение задачи", false);
    private final JProgressBar progressBar = new JProgressBar();

    public ProgressDialog(){
        frame.setUndecorated(true);
        progressBar.setIndeterminate(true);
        progressBar.setForeground( new Color(31, 87, 219));
        dialog.setUndecorated(true);

        dialog.getContentPane().add(progressBar);
        dialog.pack();
        dialog.setDefaultCloseOperation(0);
        Toolkit kit = dialog.getToolkit();
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment. getLocalGraphicsEnvironment();
        GraphicsDevice[] graphicsDevices = graphicsEnvironment.getScreenDevices();
        Insets insets = kit.getScreenInsets(graphicsDevices[0].getDefaultConfiguration());
        Dimension dimension = kit.getScreenSize();
        int max_width = (dimension.width - insets.left - insets.right);
        int max_height = (dimension.height - insets.top - insets.bottom);
        dialog.setLocation((max_width - dialog.getWidth()-50) / 2, (max_height - dialog.getHeight()-100) / 2);

        dialog.setVisible(true);
        progressBar.setVisible(true);
        dialog.setAlwaysOnTop(true);
    }
    public void showDialog(){
        dialog.setVisible(true);
        dialog.setAlwaysOnTop(true);
    }
    public void closeDialog(){
        if (dialog.isVisible()){
            dialog.getContentPane().remove(progressBar);
            dialog.getContentPane().validate();
            dialog.setVisible(false);
        }
    }
}