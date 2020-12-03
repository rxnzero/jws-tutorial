package com.dhlee.jws.saaj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

import com.dhlee.jws.soap.SoapMessageUtil;

public class Attachments {
    public static void main(String[] args) {
        FileReader fr = null;
        BufferedReader br = null;
        String line = "";

        try {
            // One argument is passed in from build.xml
//            if (args.length != 1) {
//                System.err.println("Usage: asant run " +
//                    "-Dtext-file=<filename>");
//                System.exit(1);
//            }
        	String filePath = "d:\\sample.txt";
        	
            // Create message factory
            MessageFactory messageFactory = MessageFactory.newInstance();

            // Create a message
            SOAPMessage message = messageFactory.createMessage();

            // Get the SOAP header and body from the message
            // and remove the header
            SOAPHeader header = message.getSOAPHeader();
            SOAPBody body = message.getSOAPBody();
//            header.detachNode();

            // Create attachment part for text
            AttachmentPart attachment1 = message.createAttachmentPart();

            fr = new FileReader(new File(filePath));
            br = new BufferedReader(fr);

            String stringContent = "";
            line = br.readLine();

            while (line != null) {
                stringContent = stringContent.concat(line);
                stringContent = stringContent.concat("\n");
                line = br.readLine();
            }

            attachment1.setContent(stringContent, "text/plain");
            attachment1.setContentId("attached_text");

            message.addAttachmentPart(attachment1);

            // Create attachment part for image
//            URL url = new URL("file:///d:/¿Ãµø»∆-ªÁ¡¯.jpg");
//            DataHandler dataHandler = new DataHandler(url);
//            AttachmentPart attachment2 =
//                message.createAttachmentPart(dataHandler);
//            attachment2.setContentId("attached_image");
//
//            message.addAttachmentPart(attachment2);
//            
//            // Now extract the attachments
//            Iterator iterator = message.getAttachments();
//
//            while (iterator.hasNext()) {
//                AttachmentPart attached = (AttachmentPart) iterator.next();
//                String id = attached.getContentId();
//                String type = attached.getContentType();
//                System.out.println("Attachment " + id + 
//                    " has content type " + type);
//
//                if (type.equals("text/plain")) {
//                    Object content = attached.getContent();
//                    System.out.println("Attachment contains:\n" + content);
//                }
//            }
            
            System.out.println("1. Get SOAPMessage");
            System.out.println(SoapMessageUtil.getSoap(message));
            
            System.out.println("2. Get Soap XML from SOAPMessage");
            System.out.println(SoapMessageUtil.getSoapXml(message));
            
            
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.toString());
            System.exit(1);
        } catch (IOException e) {
            System.out.println("I/O exception: " + e.toString());
            System.exit(1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}