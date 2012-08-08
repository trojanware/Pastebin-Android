package com.Pastebin;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class myXmlContentHandler extends DefaultHandler {
	
	ShowPastesActivity src;
	int index_pasteTitle, index_pasteKey;
	boolean isPaste, isPaste_title, isPaste_key;
	
	public myXmlContentHandler(ShowPastesActivity c){
		src = c;		
		index_pasteTitle = index_pasteKey = 0;
		isPaste = isPaste_title = isPaste_key = false;
	}
		
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attr){
		if(qName.equalsIgnoreCase("paste")){
			System.out.println("In paste");
			isPaste = true;
			src.noPastes++;
		}
		else if(qName.equalsIgnoreCase("paste_key")){
			isPaste_key = true;
			System.out.println("In paste_key");
		}
		else if(qName.equalsIgnoreCase("paste_title")){
			isPaste_key = true;
			System.out.println("In paste_title");
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName){
		if(qName.compareTo("paste")==0){			
			System.out.println("out paste");
		}
		else if(qName.compareTo("paste_key")==0){
			//isPaste_key = false;
			System.out.println("out paste_key and ispaste_key = "+isPaste_key+" and is p_title="+isPaste_title);
		}
		else if(qName.compareTo("paste_title")==0){
			//isPaste_title = false;
			System.out.println("out paste_titleand ispaste_key = "+isPaste_key);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length){
		if(isPaste_key){
			src.paste_key[index_pasteKey++] = new String(ch,start,length);
			System.out.println("From paste key = "+new String(ch,start,length));
		}
		if(isPaste_title){
			src.paste_title[index_pasteTitle++] = new String(ch,start,length);
			System.out.println("From paste title = "+new String(ch,start,length));
		}
		
	}
}
