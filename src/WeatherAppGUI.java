import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGUI extends JFrame {
    private JSONObject weatherData;
    public WeatherAppGUI(){
        //Setting up our GUI and adding a title
        super("Weather App");

        //Configure gui to end once program process has been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //Set window size in pixels
        setSize(450,650);//w,h

        //Load the gui at center of the screen
        setLocationRelativeTo(null);

        //Make our layout manager null to manually position our components within the gui
        setLayout(null);

        //Making GUI not resizeable
        setResizable(false);

        addGuiComponents();
    }

    private void addGuiComponents(){
        //Search Field
        JTextField searchTextField=new JTextField();

        //Set te location and size of search component
        searchTextField.setBounds(15,15,351,45);

        //Change the font Style and Size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN,24));

        add(searchTextField);

        //Weather image
        JLabel weatherConditionImage=new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);

        //Temperature Text
        JLabel temperatureText=new JLabel("10°C");
        temperatureText.setBounds(0,350,450,54);
        temperatureText.setFont(new Font("Dialog",Font.BOLD,48));
        //Center the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        //Weather Condition Description
        JLabel weatherConditionDesc=new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0,405,450,36);
        weatherConditionDesc.setFont(new Font("Dialog",Font.PLAIN,32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        //Humidity Image
        JLabel humidityImage=new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15,500,74,66);
        add(humidityImage);

        //Humidity Text
        JLabel humidityText=new JLabel("<html><b>Humidity</b> 100%</html>");
        //In above using html and b to keep certain part bold and other plain
        humidityText.setBounds(90,500,85,55);
        humidityText.setFont(new Font("Dialog",Font.PLAIN,16));
        add(humidityText);

        //Windspeed Image
        JLabel windSpeedImg=new JLabel(loadImage("src/assets/windspeed.png"));
        windSpeedImg.setBounds(220,500,74,66);
        add(windSpeedImg);

        //Windspeed Text
        JLabel windSpeedText=new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windSpeedText.setBounds(310,500,85,55);
        windSpeedText.setFont(new Font("Dialog",Font.PLAIN,16));
        add(windSpeedText);

        //Search Button
        JButton searchButton=new JButton(loadImage("src/assets/search.png"));
        //Change the cursor to a hand when hovering over search button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Get location from user
                String userInput=searchTextField.getText();

                //Validate input- remove whitespaces to ensure non-empty text
                if(userInput.replaceAll("\\s","").length()<=0){
                    return;
                }

                //retrieve weather data
                weatherData=WeatherApp.getWeatherData(userInput);

                //Update GUI

                //Update weather image
                String weatherCondition=(String) weatherData.get("weather_condition");
                switch (weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.pngImage"));
                        break;
                }

                //Update temperature text
                double temperature=(double) weatherData.get("temperature");
                temperatureText.setText(temperature+"°C");

                //Update Weather condition text
                weatherConditionDesc.setText(weatherCondition);

                //Update Humidity text
                long humidity=(long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> "+humidity+"%</html>");

                //Update Windspeed text
                double windspeed=(double) weatherData.get("windspeed");
                windSpeedText.setText("<html><b>Windspeed</b> "+windspeed+"km/h</html>");
            }
        });
        add(searchButton);
    }

    private ImageIcon loadImage(String path){
        try{
            //Read the image file from the path given
            BufferedImage image= ImageIO.read(new File(path));

            //Returns an image icon sp that pur component can render it
            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Could not find resource");
        return null;
    }
}
