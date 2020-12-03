package com.dhlee.jws.soap;

import java.io.ByteArrayOutputStream;

import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

public class SoapMessageUtil {

	public SoapMessageUtil() {
		
	}
	
	public static String getSoapXml(SOAPMessage soapMessage) throws Exception {
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final Transformer transformer = transformerFactory.newTransformer();

        // Format it
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        final Source soapContent = soapMessage.getSOAPPart().getContent();

        final ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
        final StreamResult result = new StreamResult(streamOut);
        transformer.transform(soapContent, result);

        return streamOut.toString();
    }
    
    public static String getSoap(SOAPMessage soapMessage) throws Exception {
    	final ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
        soapMessage.writeTo(streamOut);
        return streamOut.toString();
    }
}
