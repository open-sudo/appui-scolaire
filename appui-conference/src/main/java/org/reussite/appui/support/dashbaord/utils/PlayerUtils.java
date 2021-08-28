package org.reussite.appui.support.dashbaord.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PlayerUtils {
	
	public static final String  WHITE_SPACE = " ";
	protected static final Logger logger = LoggerFactory.getLogger(PlayerUtils.class);

	public static final String  APRORESCO_EMAIL_AS_SUFFIX = "@aproresco.org";
	static Map<String,List<String> > content= new HashMap<String,List<String> >();
	
	   public static List<String> readLines(String filePath){
		   if(content.get(filePath)!=null) {
			   return content.get(filePath);
		   }
		   if(filePath.startsWith("http://") || filePath.startsWith("https://") ) {
			   InputStream in=null;
				try {
					in = new URL(filePath ).openStream();
				} catch (IOException e) {
					logger.error("Unable to read content from:"+filePath,e);;
				}
				try {
					List<String> lines= Arrays.asList(IOUtils.toString( in, Charset.defaultCharset() )) ;
					content.put(filePath, lines);
					return lines;
				}catch (IOException e) {
					logger.error("Could not read content from file:"+filePath,e);
				} finally {
				     try {
						in.close();
					} catch (IOException e) {
					}
			    }
		   }
		  
		   try (InputStream inputStream = PlayerUtils.class.getResourceAsStream(filePath);
			         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			   List<String> lines= readLines(inputStream);
			   content.put(filePath, lines);
		    } catch (IOException e) {
				logger.error("Could not read content from file:"+filePath,e);
			}
		  
		 return content.get(filePath);
	   }
	   
	   private static List<String> readLines(InputStream resource){
			 List<String> lines= new ArrayList<String>();
			 BufferedReader reader=null;
		 try {
			 reader = new BufferedReader( new InputStreamReader(resource)) ;
			 String line="";
			 do{
				 line=reader.readLine();
				 if(line!=null) {
					 lines.add(line);
				 }
			 }while(line!=null);
			 
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
		 if(reader!=null) {
			 try {
				reader.close();
			} catch (IOException e) {
			}
		 }
		 return lines;
	   }
	
    public static String generatePassword() {
    
        List<String> capitalCaseLetters = Arrays.asList("ABCDEFGHIJKLMNOPQRSTUVWXYZ".split(""));
        List<String> lowerCaseLetters = Arrays.asList("abcdefghijklmnopqrstuvwxyz".split(""));
        List<String> numbers = Arrays.asList("1234567890".split(""));
        List<String> specialCharacters = Arrays.asList("!$@".split(""));
        
        Random random = new Random();
        List<String> password = Arrays.asList(
          capitalCaseLetters.get(random.nextInt(capitalCaseLetters.size())),
          lowerCaseLetters.get(random.nextInt(lowerCaseLetters.size())),
          capitalCaseLetters.get(random.nextInt(capitalCaseLetters.size())),
          numbers.get(random.nextInt(numbers.size())),
          lowerCaseLetters.get(random.nextInt(lowerCaseLetters.size())),
          capitalCaseLetters.get(random.nextInt(capitalCaseLetters.size())),
          numbers.get(random.nextInt(numbers.size())),
          specialCharacters.get(random.nextInt(specialCharacters.size()))
        );
      
        return  String.join("", password);
     }
    
}
