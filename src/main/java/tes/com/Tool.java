package tes.com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.sf.json.JSONObject;




public class Tool {

	
   public static final String KEY_ALGORITHM="AES";
   public static final String CIPHER_ALGORITHM="AES";
   
   public static Key toKey(byte[] key) throws Exception{
       SecretKey secretKey=new SecretKeySpec(key,KEY_ALGORITHM);
       return secretKey;
   }

   public static byte[] decrypt(byte[] data,byte[] key) throws Exception{
       Key k =toKey(key);
       Cipher cipher=Cipher.getInstance(CIPHER_ALGORITHM);
       cipher.init(Cipher.DECRYPT_MODE, k);
       return cipher.doFinal(data);
   }
   

   public static void main(String[] args) throws UnsupportedEncodingException{
	   
	   
       if(args.length != 2){
           System.err.println("参数为 -s 加密字符串 或者 -f 文件路径");
           System.exit(1);
       }

       if(args[0].equalsIgnoreCase("-s")){
    	   String source = args[1];
           byte[] result = handleStr(source);
           if(result != null){
        	 System.out.println("Before Decode:"+ source);
          	 System.out.println("After Decode:"+ new String(result));
           }
    	   
       }else{
    	   
    	  File file = new File(args[1]);

         if(!file.exists()){
         	System.err.println("找不到加密后的文件！path:" + args[1]);
         	System.exit(1);
         }
         
         BufferedReader reader = null;
         try {
             //System.out.println("以行为单位读取文件内容，一次读一整行：");
             reader = new BufferedReader(new FileReader(file));
             String tempString = null;

             while ((tempString = reader.readLine()) != null) {
                 String temArr[] = tempString.split("\\| \\|");
                 
                 System.out.println("Befor Decode:"+ tempString);
                 byte[] result = handleStr(temArr[3]);
                 
                 JsonFormatTool tool = new JsonFormatTool();  
                 
                // System.out.println("After Decode:"+ new String(result));
                 if(result != null){
                	   JSONObject jsobjcet = new JSONObject();  
                	   
                       jsobjcet.put("After Decode:", new String(result));  
                	 System.out.println(tool.formatJson(jsobjcet.toString()));
                	 
                 }
             }
             reader.close();
         } catch (IOException e) {
             e.printStackTrace();
         } finally {
             if (reader != null) {
                 try {
                     reader.close();
                 } catch (IOException e1) {
                 }
             }
         }
                  
       }
       

   }
   
   public static byte[] handleStr(String str){
      
       try{
    	   
    	  byte[] key = "va/SZFAr/EUuygJ=".getBytes("UTF-8");
     	  byte[] step1 = Base64.getDecoder().decode(str.getBytes());//un base64
     	  byte[] step2 = decrypt(step1, key); // un AES
     	  
     	  ByteArrayInputStream bis = new  ByteArrayInputStream(step2);
          GZIPInputStream gzis = new GZIPInputStream(bis);
          byte[] buf = new byte[1024]; 
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
           int len;
           while((len = gzis.read(buf)) > 0) {
         	  baos.write(buf, 0, len);
           }
     	  byte[] step3 = baos.toByteArray(); //un zip
     	  return step3;
     	
     	  
       }catch(Exception e){
           e.printStackTrace();
           return null;
       }
       
       
   }
} 
