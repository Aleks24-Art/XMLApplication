package com.xml;

import com.xml.parser.XMLParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {

        XMLParser parser = new XMLParser();
        parser.parseWithMistakesCorrection("src/main/resources/group.xml", "src/main/output/newGroup.xml");

        /*String in, out;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter input XML file path (example src/main/resources/group.xml) : ");
        in = reader.readLine();

        System.out.println("Enter output XML file path (example src/main/output/newGroup.xml) : ");
        out = reader.readLine();

        parser.parseWithMistakesCorrection(in, out);*/

    }
}
