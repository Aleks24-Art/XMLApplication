package com.xml.parser;

import org.apache.commons.math3.util.Precision;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;


public class XMLParser {


    public void parseWithMistakesCorrection(String in, String out) throws ParserConfigurationException, IOException, SAXException {
        File file = new File(in);

        //Создание проверяющего парсера
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        //Создаём обработчик ошибок
        XMLErrorChecker errorChecker = new XMLErrorChecker();
        //Настройка, что бы парсер посылал ошибки в XMLErrorChecker
        builder.setErrorHandler(errorChecker);
        Document document = builder.parse(file);

        //Достаём список всех студентов
        NodeList studentNodeList = document.getElementsByTagName("student");
        //Проходимся по нему
        for (int i = 0; i < studentNodeList.getLength(); i++) {
            Element studentElement = (Element) studentNodeList.item(i);
            //Достаём список всех предметов
            NodeList subjectNode = studentElement.getElementsByTagName("subject");
            //Достаём средний балл для проверки
            double averageFromDoc = Precision.round(Double.parseDouble(studentElement.getElementsByTagName("average").item(0).getTextContent()), 1);
            double trueAverage = getTrueAverageMark(subjectNode);
            //Сравниваем средний балл и меняем при необходимости
            if (trueAverage != averageFromDoc) {
                System.out.println("Average mark " + averageFromDoc + " was changed to " + trueAverage);
                changeAverageMarkInAnotherFile(document, out, i, trueAverage);
            }
        }
    }


    private void changeAverageMarkInAnotherFile(Document doc, String out, int nodePos, double trueAverage) {

        Element studentElem = (Element) doc.getElementsByTagName("student").item(nodePos);
        Node averageNode = studentElem.getElementsByTagName("average").item(0).getFirstChild();
        averageNode.setNodeValue(Double.toString(trueAverage));
        try {
            doc.getDocumentElement().normalize();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(out));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            System.out.println("XML was successful changed");
        } catch (TransformerException e) {
            System.out.println("Exception" + e + "while transforming XML file " + out);
        }
    }

    private double getTrueAverageMark(NodeList subjectNode) {
        double trueAverage = 0;
        //Проходимся по всем предметам получая правильный средний балл
        for (int j = 0; j < subjectNode.getLength(); j++) {
            Element childElement = (Element) subjectNode.item(j);
            trueAverage += Double.parseDouble(childElement.getAttribute("mark"));
        }
        //Округляем по десятых
        trueAverage = Precision.round(trueAverage / subjectNode.getLength(), 1);
        return trueAverage;
    }
}