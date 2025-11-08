package com.ernx.automation;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// creates temporary email account using mail.tm api and fetches OTP from emails
public class EmailAPI {
    
    private String email;
    private String token;
    private OkHttpClient client;
    private Gson gson;
    private Pattern otpPattern;
    
    // constructor to create email account
    public EmailAPI() {
        client = new OkHttpClient();
        gson = new Gson();
        otpPattern = Pattern.compile("\\b(\\d{4})\\b");
        
        try {
            // get domain
            Request req1 = new Request.Builder().url("https://api.mail.tm/domains").get().build();
            Response resp1 = client.newCall(req1).execute();
            String body1 = resp1.body().string();
            resp1.close();
            
            JsonObject json1 = gson.fromJson(body1, JsonObject.class);
            JsonArray domains = json1.getAsJsonArray("hydra:member");
            String domain = domains.get(0).getAsJsonObject().get("domain").getAsString();
            
            // create email
            String username = "test" + System.currentTimeMillis();
            email = username + "@" + domain;
            
            // create account
            JsonObject accountData = new JsonObject();
            accountData.addProperty("address", email);
            accountData.addProperty("password", "password123");
            RequestBody body = RequestBody.create(accountData.toString(), MediaType.parse("application/json"));
            Request req2 = new Request.Builder().url("https://api.mail.tm/accounts").post(body).build();
            Response resp2 = client.newCall(req2).execute();
            resp2.close();
            
            // get token
            JsonObject loginData = new JsonObject();
            loginData.addProperty("address", email);
            loginData.addProperty("password", "password123");
            RequestBody loginBody = RequestBody.create(loginData.toString(), MediaType.parse("application/json"));
            Request req3 = new Request.Builder().url("https://api.mail.tm/token").post(loginBody).build();
            Response resp3 = client.newCall(req3).execute();
            String tokenBody = resp3.body().string();
            resp3.close();
            
            JsonObject tokenJson = gson.fromJson(tokenBody, JsonObject.class);
            token = tokenJson.get("token").getAsString();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getEmail() {
        return email;
    }
    
    // get OTP from email messages
    public String getOTP(int timeoutSeconds) {
        long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000);
        
        while (System.currentTimeMillis() < endTime) {
            try {
                // get messages
                Request req = new Request.Builder()
                    .url("https://api.mail.tm/messages")
                    .addHeader("Authorization", "Bearer " + token)
                    .get()
                    .build();
                Response resp = client.newCall(req).execute();
                String respBody = resp.body().string();
                resp.close();
                
                JsonObject json = gson.fromJson(respBody, JsonObject.class);
                JsonArray messages = json.getAsJsonArray("hydra:member");
                
                if (messages != null && messages.size() > 0) {
                    // get first message
                    String msgId = messages.get(0).getAsJsonObject().get("id").getAsString();
                    
                    Request msgReq = new Request.Builder()
                        .url("https://api.mail.tm/messages/" + msgId)
                        .addHeader("Authorization", "Bearer " + token)
                        .get()
                        .build();
                    Response msgResp = client.newCall(msgReq).execute();
                    String msgBody = msgResp.body().string();
                    msgResp.close();
                    
                    JsonObject msg = gson.fromJson(msgBody, JsonObject.class);
                    String text = "";
                    
                    if (msg.has("text")) {
                        text = msg.get("text").getAsString();
                    } else if (msg.has("intro")) {
                        text = msg.get("intro").getAsString();
                    }
                    
                    // find OTP
                    Matcher m = otpPattern.matcher(text);
                    if (m.find()) {
                        return m.group(1);
                    }
                }
                
                Thread.sleep(3000);
            } catch (Exception e) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                }
            }
        }
        return null;
    }
}
