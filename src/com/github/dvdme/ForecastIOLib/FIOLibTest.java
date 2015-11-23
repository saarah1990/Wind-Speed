package com.github.dvdme.ForecastIOLib;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class FIOLibTest {

    private static final String APIKEY = "77ccedf783c65b4aa615e5a0dfa627dc";
    private static final JTextField jTextField = new JTextField("2013-05-06T12:00:00    ");
    private static JLabel jLabel;
    public static void main(String[] args) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException{
        
        JFrame oWindow = new JFrame("Saudi Arabia Map"); 
        JPanel jPanel = new JPanel(new BorderLayout());
        
        ImageIcon iIcon = new ImageIcon("map.png");
        jLabel = new JLabel(iIcon);
        
        JButton jButton = new JButton("Select Date");
       
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    jLabel.setIcon(getUpdtedImage());
                } catch (IOException | ParserConfigurationException | SAXException | XPathExpressionException ex) {
                    Logger.getLogger(FIOLibTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        JPanel innerJapenl = new JPanel(new FlowLayout());
        innerJapenl.add(jTextField);
        innerJapenl.add(jButton);
        
        jPanel.add(jLabel,BorderLayout.CENTER);
        jPanel.add(innerJapenl,BorderLayout.NORTH);
        
        oWindow.setResizable(false);
        oWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        oWindow.add(jPanel);
        oWindow.setVisible(true);
        oWindow.pack();

    }//main
    public static ImageIcon getUpdtedImage() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException
    {
        final BufferedImage image = ImageIO.read(new File("map.png"));
        Graphics g = image.getGraphics();
        g.setFont(g.getFont().deriveFont(30f));
        g.setColor(Color.BLUE);
        g.drawString(getWindSpeed(jTextField.getText(), "riyadh"), 360, 220);
        g.setColor(Color.RED);
        g.drawString(getWindSpeed(jTextField.getText(), "jeddah"), 90, 390);
        g.setColor(Color.orange);
        g.drawString(getWindSpeed(jTextField.getText(), "mecca"), 120, 330);
        g.setColor(Color.green);
        g.drawString(getWindSpeed(jTextField.getText(), "abha"), 245, 540);
        g.setColor(Color.yellow);
        g.drawString(getWindSpeed(jTextField.getText(), "dhammam"), 500, 140);
        g.dispose();
       
        return new ImageIcon(image);
    }
    public static String getWindSpeed(String a_Date, String a_City) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException
    {
        ForecastIO fio = new ForecastIO(APIKEY);
        fio.setUnits(ForecastIO.UNITS_SI);
        fio.setLang(ForecastIO.LANG_ENGLISH);
        fio.setTime(a_Date);
        
        String LocPoint = getLocationPoint(a_City);
        fio.getForecast(LocPoint.split(",")[0], LocPoint.split(",")[1]);
        
        
        FIODaily daily = new FIODaily(fio);
        if (daily.days() < 0) {
            return "No daily data.";
        }
        
        return daily.getDay(0).getByKey("windSpeed");
    }
    public static String getLocationPoint(String a_City) throws MalformedURLException, SAXException, XPathExpressionException, IOException, ParserConfigurationException
    {
        URL myUrl = new URL("http://maps.googleapis.com/maps/api/geocode/xml?address="+a_City+"&sensor=false");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(myUrl.openStream());
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("/GeocodeResponse/result/geometry/location/lat");
        
        String swLat = expr.evaluate(doc, XPathConstants.STRING).toString();
        System.out.println(a_City + " Lat: " + swLat);

        XPathExpression expr2 = xpath.compile("/GeocodeResponse/result/geometry/location/lng");
        String swLang = expr2.evaluate(doc, XPathConstants.STRING).toString();
        System.out.println(a_City + " Lan: " + swLang);
        
        return swLat+","+swLang;
    }
    public void Main()  throws MalformedURLException, ParserConfigurationException, SAXException, XPathExpressionException, IOException 
    {
        URL myUrl = new URL("http://maps.googleapis.com/maps/api/geocode/xml?address=Dammam&sensor=false");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(myUrl.openStream());
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("/GeocodeResponse/result/geometry/location/lat");
        String swLat = expr.evaluate(doc, XPathConstants.STRING).toString();
        System.out.println("swLat: " + swLat);

        XPathExpression expr2 = xpath.compile("/GeocodeResponse/result/geometry/location/lng");
        String swLang = expr2.evaluate(doc, XPathConstants.STRING).toString();
        //Some coordinates for testing
        //Lisbon:   38.7252993 , -9.1500364
        //Madrid:   40.41678 , -3.70379
        //Ceuta:    35.88838 , -5.32464
        //Paris:    48.85661 , 2.35222 
        //Berlin:   52.51917 , 13.40609
        //Brasilia: -15.83454 , -47.98828
        //London:   51.51121 , -0.11982
        //Alcatraz: 37.8267 , -122.423
        //Caracas:  10.4880555, -66.8791667
        ForecastIO fio = new ForecastIO(APIKEY);
        fio.setUnits(ForecastIO.UNITS_SI);
        fio.setLang(ForecastIO.LANG_ENGLISH);
        fio.setTime("2013-05-06T12:00:00");
        fio.getForecast(swLat, swLang);

        //Response Headers info
        System.out.println("Response Headers:");
        System.out.println("Cache-Control: " + fio.getHeaderCache_Control());
        System.out.println("Expires: " + fio.getHeaderExpires());
        System.out.println("X-Forecast-API-Calls: " + fio.getHeaderX_Forecast_API_Calls());
        System.out.println("X-Response-Time: " + fio.getHeaderX_Response_Time());
        System.out.println("\n");

        //ForecastIO info
        System.out.println("Latitude: " + fio.getLatitude());
        System.out.println("Longitude: " + fio.getLongitude());
        System.out.println("Timezone: " + fio.getTimezone());
        System.out.println("Offset: " + fio.offsetValue());
        System.out.println("\n");
        //Currently data
        FIOCurrently currently = new FIOCurrently(fio);

        System.out.println("\nCurrently\n");
        String[] f = currently.get().getFieldsArray();
        for (String f1 : f) {
            System.out.println(f1 + ": " + currently.get().getByKey(f1));
        }
        System.out.println("\n");

        //Minutely data
        FIOMinutely minutely = new FIOMinutely(fio);
        if (minutely.minutes() < 0) {
            System.out.println("No minutely data.");
        } else {
            System.out.println("\nMinutely\n");
        }
        for (int i = 0; i < minutely.minutes(); i++) {
            String[] m = minutely.getMinute(i).getFieldsArray();
            System.out.println("Minute #" + (i + 1));
            for (String m1 : m) {
                System.out.println(m1 + ": " + minutely.getMinute(i).getByKey(m1));
            }
            System.out.println("\n");
        }

        //Hourly data
        FIOHourly hourly = new FIOHourly(fio);
        if (hourly.hours() < 0) {
            System.out.println("No hourly data.");
        } else {
            System.out.println("\nHourly:\n");
        }
        for (int i = 0; i < hourly.hours(); i++) {
            String[] h = hourly.getHour(i).getFieldsArray();
            System.out.println("Hour #" + (i + 1));
            for (String h1 : h) {
                System.out.println(h1 + ": " + hourly.getHour(i).getByKey(h1));
            }
            System.out.println("\n");
        }

        //Daily data
        FIODaily daily = new FIODaily(fio);
        if (daily.days() < 0) {
            System.out.println("No daily data.");
        } else {
            System.out.println("\nDaily:\n");
        }
        for (int i = 0; i < daily.days(); i++) {
            String[] h = daily.getDay(i).getFieldsArray();
            System.out.println("Day #" + (i + 1));
            for (String h1 : h) {
                System.out.println(h1 + ": " + daily.getDay(i).getByKey(h1));
            }
            System.out.println("\n");
        }

        //Flags data
        FIOFlags flags = new FIOFlags(fio);
        System.out.println("Available Flags: ");
        for (String availableFlag : flags.availableFlags()) {
            System.out.println(availableFlag);
        }
        System.out.println("\n");
        for (String metarStation : flags.metarStations()) {
            System.out.println("Metar Station: " + metarStation);
        }
        System.out.println("\n");
        for (String dStation : flags.isdStations()) {
            System.out.println("ISD Station: " + dStation);
        }
        System.out.println("\n");
        for (String source : flags.sources()) {
            System.out.println("Source: " + source);
        }
        System.out.println("\n");
        System.out.println("Units: " + flags.units());
        System.out.println("\n");

        //Alerts data
        FIOAlerts alerts = new FIOAlerts(fio);
        if (alerts.NumberOfAlerts() <= 0) {
            System.out.println("No alerts for this location.");
        } else {
            System.out.println("Alerts");
            for (int i = 0; i < alerts.NumberOfAlerts(); i++) {
                System.out.println(alerts.getAlert(i));
            }
        }
    }

}
