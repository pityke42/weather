package org.example;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherGUI extends JFrame{
    public WeatherGUI(){
        //title
        super("Weather Application");
        //close program (end gui)
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //size
        setSize(455, 655);
        //center
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        addGuiComponents();
    }
    private void addGuiComponents(){
        //search
        JTextField searchField = new JTextField();
        //location and size
        searchField.setBounds(35,15,317,35);
        //font style and size
        searchField.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(searchField);
        //search button
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        //cursor, button hovering
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13,47,45);
        add(searchButton);

        //weather image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);

        //temperature
        JLabel temperatureText = new JLabel("10C");
        temperatureText.setBounds(0,350,450,54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD,48));

        //center text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER );
        add(temperatureText);
        JLabel weatherCondDesc = new JLabel("Cloudy");
        weatherCondDesc.setBounds(0,405,450,36);
        weatherCondDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherCondDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherCondDesc);

        //humidity img
        JLabel humidityImg = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImg.setBounds(15,500,74,66);
        add(humidityImg);
        //humidity text
        JLabel humidityTxt = new JLabel("<html><b>Humidity</b></html>");
        humidityTxt.setBounds(90,500,85,55);
        humidityTxt.setFont(new Font("Dialog",Font.PLAIN,16));
        add(humidityTxt);

        //windspeed img
        JLabel windspeedImg = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImg.setBounds(220,500,74,66);
        add(windspeedImg);
        //windspeed txt
        JLabel windspeedTxt = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windspeedTxt.setBounds(310,500,86,55);
        windspeedTxt.setFont(new Font("Dialog",Font.PLAIN,14));
        add(windspeedTxt);

    }
    //create images in gui components
    private ImageIcon loadImage(String sourcePath){
        try{
            //read image file form path
            BufferedImage image = ImageIO.read(new File(sourcePath));
            return new ImageIcon(image);
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Cannot find resource");
        return null;
    }

}
