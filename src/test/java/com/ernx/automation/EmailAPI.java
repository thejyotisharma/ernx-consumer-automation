package com.ernx.automation;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import java.util.regex.Pattern;

public class EmailAPI {
    
    private String email;
    private String token;
    private OkHttpClient client;
    
    // constructor to create email account
    public EmailAPI() {
        client = new OkHttpClient();
        try {
            // get domain from mail.tm api
            Request domainRequest = new Request.Builder().url("https://api.mail.tm/domains").get().build();
            Response domainResponse = client.newCall(domainRequest).execute();
            String domainBody = domainResponse.body().string();
            JsonObject domainJson = JsonParser.parseString(domainBody).getAsJsonObject();
            String domain = domainJson.getAsJsonArray("hydra:member").get(0).getAsJsonObject().get("domain").getAsString();
            
            // create username with timestamp
            String username = "test" + System.currentTimeMillis();
            email = username + "@" + domain;
            
            // create account on mail.tm
            JsonObject account = new JsonObject();
            account.addProperty("address", email);
            account.addProperty("password", "password123");
            RequestBody body = RequestBody.create(account.toString(), MediaType.parse("application/json"));
            Request accountRequest = new Request.Builder().url("https://api.mail.tm/accounts").header("Content-Type", "application/json").post(body).build();
            Response accountResponse = client.newCall(accountRequest).execute();
            String accountBody = accountResponse.body().string();
            JsonObject accountJson = JsonParser.parseString(accountBody).getAsJsonObject();
            
            // get token from account creation or login
            if (accountJson.has("token")) {
                token = accountJson.get("token").getAsString();
            } else {
                // login to get token
                JsonObject login = new JsonObject();
                login.addProperty("address", email);
                login.addProperty("password", "password123");
                RequestBody loginBody = RequestBody.create(login.toString(), MediaType.parse("application/json"));
                Request loginRequest = new Request.Builder().url("https://api.mail.tm/token").header("Content-Type", "application/json").post(loginBody).build();
                Response loginResponse = client.newCall(loginRequest).execute();
                String loginBodyStr = loginResponse.body().string();
                JsonObject loginJson = JsonParser.parseString(loginBodyStr).getAsJsonObject();
                token = loginJson.get("token").getAsString();
            }
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
                // get all messages
                Request request = new Request.Builder().url("https://api.mail.tm/messages").header("Authorization", "Bearer " + token).get().build();
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
                
                // check if there are messages
                if (json.has("hydra:member") && json.getAsJsonArray("hydra:member").size() > 0) {
                    // get the first message id
                    String messageId = json.getAsJsonArray("hydra:member").get(0).getAsJsonObject().get("id").getAsString();
                    Request msgRequest = new Request.Builder().url("https://api.mail.tm/messages/" + messageId).header("Authorization", "Bearer " + token).get().build();
                    Response msgResponse = client.newCall(msgRequest).execute();
                    String msgBody = msgResponse.body().string();
                    JsonObject msgJson = JsonParser.parseString(msgBody).getAsJsonObject();
                    String text = msgJson.get("text").getAsString();
                    
                    // find OTP using regex pattern (4 digits)
                    Pattern pattern = Pattern.compile("\\d{4}");
                    java.util.regex.Matcher matcher = pattern.matcher(text);
                    if (matcher.find()) {
                        return matcher.group();
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
