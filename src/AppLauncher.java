import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        //We are using invokeLater as it is useful for swing GUI's like ours because it makes updates to the GUI more thread Safe
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                //Display our weather app gui
                new WeatherAppGUI().setVisible(true);
            }
        });
    }
}
