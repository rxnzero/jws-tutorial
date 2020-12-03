package com.dhlee.jws.saaj;

import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.namespace.QName;
import java.net.URL;
import java.util.Properties;
import java.util.Enumeration;
import java.util.Iterator;
import java.io.FileInputStream;

public class MyUddiPing {
    public static void main(String[] args) {
        try {
            // two arguments are passed in from build.xml
            if (args.length != 2) {
                System.err.println("Usage: asant run " +
                    "-Dbusiness-name=<name>");
                System.exit(1);
            }

            // Retrieve settings from uddi.properties file,
            // add to system properties
            Properties myprops = new Properties();
            myprops.load(new FileInputStream(args[0]));

            Properties props = System.getProperties();

            Enumeration propNames = myprops.propertyNames();
            while (propNames.hasMoreElements()) {
                String s = (String) propNames.nextElement();
                props.setProperty(s, myprops.getProperty(s));
            }

            // Create connection and message factory
            SOAPConnectionFactory soapConnectionFactory =
                SOAPConnectionFactory.newInstance();
            SOAPConnection connection =
                soapConnectionFactory.createConnection();

            // Use SOAP 1.1 messages for UDDI v2 registries
            MessageFactory messageFactory = 
                MessageFactory.newInstance();
            // Create a message
            SOAPMessage message = messageFactory.createMessage();

            // Get the SOAP header from the message and remove it
            SOAPHeader header = message.getSOAPHeader();
            header.detachNode();

            // Get the SOAP body from the message
            SOAPBody body = message.getSOAPBody();

            // Create a UDDI v2 find_business body element
            SOAPBodyElement findBusiness =
                body.addBodyElement(new QName(
                    "urn:uddi-org:api_v2", "find_business"));
            findBusiness.addAttribute(new QName("generic"), "2.0");
            findBusiness.addAttribute(new QName("maxRows"), "100");

            SOAPElement businessName =
                findBusiness.addChildElement(new QName("name"));
            businessName.addTextNode(args[1]);

            message.saveChanges();

            // Display the message you are sending
            System.out.println("\n---- Request Message ----\n");
            message.writeTo(System.out);

            // Retrieve the endpoint from system properties
            URL endpoint = new URL(System.getProperties().getProperty("URL"));

            // Send message and get reply
            SOAPMessage reply = connection.call(message, endpoint);

            System.out.println("\n\nReceived reply from: " + 
                endpoint);

            // Display the reply
            System.out.println("\n---- Reply Message ----\n");
            reply.writeTo(System.out);

            // Now parse the reply
            SOAPBody replyBody = reply.getSOAPBody();

            /*
             * The response to a find_business query is a
             * businessList conformant to the UDDI V2 Data
             * Structure specifications. For further details,
             * see
             * http://uddi.org/pubs/DataStructure-V2.03-Published-20020719.htm#_Toc25130802
             */
            System.out.println("\n\nContent extracted from " +
                "the reply message:\n");

            Iterator businessListIterator =
                replyBody.getChildElements(new QName(
                    "urn:uddi-org:api_v2",
                    "businessList"));

            /*	
             * businessList is a mandatory element in a
             * successful response and appears only once.
             */
            SOAPBodyElement businessList =
                (SOAPBodyElement) businessListIterator.next();

            /*
             * Get the businessInfos element. This contains
             * the business entries retrieved that match the
             * criteria specified in the find_business
             * request.
             */
            Iterator businessInfosIterator =
                businessList.getChildElements(new QName(
                    "urn:uddi-org:api_v2", "businessInfos"));

            /*
             * businessInfos, too is a mandatory element in
             * a successful response and appears only once.
             */
            SOAPElement businessInfos =
                (SOAPElement) businessInfosIterator.next();

            /*
             * The businessInfos contains zero or more
             * businessInfo structures. Each businessInfo
             * structure contains the company name and
             * optional description data.
             */
            Iterator businessInfoIterator =
                businessInfos.getChildElements(new QName(
                        "urn:uddi-org:api_v2",
                        "businessInfo",
                        ""));

            if (!businessInfoIterator.hasNext()) {
                System.out.println("No businesses found " +
                    "matching the name \"" + args[1] + "\".");
            } else {
                while (businessInfoIterator.hasNext()) {
                    SOAPElement businessInfo =
                        (SOAPElement) businessInfoIterator.next();

                    // Extract name and description from the 
                    // businessInfo
                    Iterator nameIterator =
                        businessInfo.getChildElements(new QName(
                            "urn:uddi-org:api_v2", "name"));

                    while (nameIterator.hasNext()) {
                        businessName = 
                            (SOAPElement) nameIterator.next();
                        System.out.println("Company name: " +
                            businessName.getValue());
                    }

                    Iterator descriptionIterator =
                        businessInfo.getChildElements(new QName(
                            "urn:uddi-org:api_v2", "description"));

                    while (descriptionIterator.hasNext()) {
                        SOAPElement businessDescription =
                            (SOAPElement) descriptionIterator.next();
                        System.out.println("Description: " +
                            businessDescription.getValue());
                    }

                    System.out.println("");
                }
            }

            // Close the connection
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
