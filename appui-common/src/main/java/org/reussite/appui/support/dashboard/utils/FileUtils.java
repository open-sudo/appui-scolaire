package org.reussite.appui.support.dashboard.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class FileUtils {
	
	public static final String  WHITE_SPACE = " ";
	
	public static final String  APRORESCO_EMAIL_AS_SUFFIX = "@aproresco.org";
	static Map<String,List<String> > content= new HashMap<String,List<String> >();
	
	   public static List<String> readLines(String filePath){
		   if(content.get(filePath)!=null) {
			   return content.get(filePath);
		   }
		   try (InputStream inputStream = FileUtils.class.getResourceAsStream(filePath);
			         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			   List<String> lines= readLines(inputStream);
			   content.put(filePath, lines);
			    } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
