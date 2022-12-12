/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.rapydcheckoutexample;


//import jakarta.servlet.DispatcherType;
//import java.io.IOException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.POST;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;


@WebServlet("/Purchase")
public class CheckoutServlet extends HttpServlet{
    String cancelCheckoutURL = "http://example.com/cancel";
    String completeCheckoutURL = "http://example.com/complete";
    String country = "US";
    String currency = "USD";
    String language = "en";    
    public static String hash256(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        return bytesToHex(md.digest());
    }
    
    public static String bytesToHex(byte[]bytes) {
        StringBuffer result = new StringBuffer();
        for (byte byt: bytes)
            result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }
    
    public static String hmacDigest(String msg, String keyString, String algo) {
        String digest = null;
        try {
            SecretKeySpec key = new SecretKeySpec((keyString).getBytes("ASCII"), algo);
            Mac mac = Mac.getInstance(algo);
            mac.init(key);
            
            byte[]bytes = mac.doFinal(msg.getBytes("UTF-8"));
            
            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (UnsupportedEncodingException e) {
            System.out.println("hmacDigest UnsupportedEncodingException");
        }
        catch (InvalidKeyException e) {
            System.out.println("hmacDigest InvalidKeyException");
        }
        catch (NoSuchAlgorithmException e) {
            System.out.println("hmacDigest NoSuchAlgorithmException");
        }
        return digest;
    }
    
    public static String generateString() {
        int leftLimit = 97;   // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char)randomLimitedInt);
        }
        String generatedString = buffer.toString();
        
        return (generatedString);
    }
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
        try {
            System.out.println("GetPOS Start");
            String httpMethod = "post";                           // get|put|post|delete - must be lowercase
            String urlPath = "/v1/checkout"; 
            String basePath = "https://sandboxapi.rapyd.net"; // hardkeyed for this example
            String salt = generateString(); // Randomly generated for each request.
            long timestamp = System.currentTimeMillis() / 1000L; // Unix time (seconds).
            String accessKey = "0F32811C67FADC15E0ED";                    // The access key received from Rapyd.
            String secretKey = "d8abc747ebfec6cb10a049a9904d7c7652cefeb4cafa6304b15a0176ccf4ef19eac4c0ba61ed8f65";                    // Never transmit the secret key by itself.
            String bodyString = "{"
                + "\"amount\": 10,\n" +
                "\"complete_checkout_url\":" + completeCheckoutURL +",\n" +
                "\"country\":" + country+",\n" +
                "\"currency\":" + currency + ",\n" +
                "\"cancel_checkout_url\":" + cancelCheckoutURL + ",\n" +
                "\"language\":" + language +  "}";                                     // Always empty for GET; strip nonfunctional whitespace.
                                                                       // Must be a String or an empty String.
            String toEnc = httpMethod + urlPath + salt + Long.toString(timestamp) + accessKey + secretKey + bodyString;
            System.out.println("String to be encrypted::" + toEnc);
            String StrhashCode = hmacDigest(toEnc, secretKey, "HmacSHA256");
            String signature = Base64.getEncoder().encodeToString(StrhashCode.getBytes());
            HttpClient httpclient = HttpClients.createDefault();
            
            try {
                HttpGet httpget = new HttpGet(basePath + urlPath);
             
                httpget.addHeader("Content-Type", "application/json");
                httpget.addHeader("access_key", accessKey);
                httpget.addHeader("salt", salt);
                httpget.addHeader("timestamp", Long.toString(timestamp));
                httpget.addHeader("signature", signature);
                
                // Create a custom response handler
                ResponseHandler < String > responseHandler = new ResponseHandler < String > () {
                     @ Override
                    public String handleResponse(
                        final HttpResponse response)throws ClientProtocolException,
                    IOException {
                        int status = response.getStatusLine().getStatusCode();
                        HttpEntity entity = response.getEntity();
                        
                        return entity != null ? EntityUtils.toString(entity) : null;
                    }
                };
                
                String responseBody = httpclient.execute(httpget, responseHandler);
                System.out.println("----------------------------------------");
                System.out.println(responseBody);
            } catch (Exception e) {
            
                System.out.println(e.getMessage());
                    
                for (StackTraceElement exc : e.getStackTrace()) {
                    System.out.println(exc.toString());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
                    
                for (StackTraceElement exc : e.getStackTrace()) {
                    System.out.println(exc.toString());
                }
        }
    }
 }
    

