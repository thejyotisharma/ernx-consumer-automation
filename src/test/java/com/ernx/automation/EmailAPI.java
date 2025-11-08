package com.ernx.automation;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import java.io.IOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// creates temporary email account using mail.tm api and fetches OTP from emails
public class EmailAPI {
    
    private static final String API_URL = "https://api.mail.tm";
    private static final Pattern OTP_PATTERN = Pattern.compile("\\b(\\d{4})\\b");
    
    private String email;
    private String token;
    private OkHttpClient client;
    private Gson gson;
    
    // constructor to create email account
    public EmailAPI() {
        client = new OkHttpClient.Builder().build();
        gson = new Gson();
        
        try {
            // create account
            String localPart = "test" + System.currentTimeMillis() % 100000 + (new Random().nextInt(900) + 100);
            JsonObject account = createMailTmAccount(localPart);
            email = account.get("address").getAsString();
            String password = account.get("password").getAsString();
            
            // get token
            token = getMailTmToken(email, password);
        } catch (Exception e) {
            System.out.println("Error creating email account: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // create mail.tm account
    private JsonObject createMailTmAccount(String localPart) throws IOException {
        // get domain from mail.tm api
        Request domainsReq = new Request.Builder()
                .url(API_URL + "/domains")
                .get()
                .build();
        
        try (Response resp = client.newCall(domainsReq).execute()) {
            if (!resp.isSuccessful()) {
                throw new IOException("Failed to fetch domains: " + resp.code());
            }
            JsonObject domResp = gson.fromJson(resp.body().string(), JsonObject.class);
            JsonArray domains = domResp.getAsJsonArray("hydra:member");
            if (domains.size() == 0) {
                throw new IOException("No mail.tm domains available");
            }
            String domain = domains.get(0).getAsJsonObject().get("domain").getAsString();
            
            String address = localPart + "@" + domain;
            String password = "password123";
            
            JsonObject payload = new JsonObject();
            payload.addProperty("address", address);
            payload.addProperty("password", password);
            
            RequestBody body = RequestBody.create(payload.toString(), MediaType.parse("application/json"));
            Request createReq = new Request.Builder()
                    .url(API_URL + "/accounts")
                    .post(body)
                    .build();
            
            try (Response createResp = client.newCall(createReq).execute()) {
                if (createResp.code() == 201 || createResp.code() == 422) {
                    JsonObject ret = new JsonObject();
                    ret.addProperty("address", address);
                    ret.addProperty("password", password);
                    return ret;
                } else {
                    throw new IOException("Failed to create account: " + createResp.code() + " / " + createResp.body().string());
                }
            }
        }
    }
    
    // get token from mail.tm
    private String getMailTmToken(String address, String password) throws IOException {
        JsonObject payload = new JsonObject();
        payload.addProperty("address", address);
        payload.addProperty("password", password);
        
        RequestBody body = RequestBody.create(payload.toString(), MediaType.parse("application/json"));
        Request req = new Request.Builder()
                .url(API_URL + "/token")
                .post(body)
                .build();
        
        try (Response resp = client.newCall(req).execute()) {
            if (!resp.isSuccessful()) {
                // retry once after short wait
                System.out.println("Token request unsuccessful: " + resp.code() + ". Retrying once...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                try (Response resp2 = client.newCall(req).execute()) {
                    if (!resp2.isSuccessful()) {
                        throw new IOException("Failed to get token: " + resp2.code() + " / " + resp2.body().string());
                    }
                    JsonObject jo = gson.fromJson(resp2.body().string(), JsonObject.class);
                    return jo.get("token").getAsString();
                }
            }
            JsonObject jo = gson.fromJson(resp.body().string(), JsonObject.class);
            return jo.get("token").getAsString();
        }
    }
    
    public String getEmail() {
        return email;
    }
    
    // get OTP from email messages
    public String getOTP(int timeoutSeconds) {
        long timeoutMillis = timeoutSeconds * 1000L;
        long pollInterval = 3000L;
        long start = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - start < timeoutMillis) {
            try {
                String otp = checkLatestMessagesForOtp();
                if (otp != null) {
                    return otp;
                }
                Thread.sleep(pollInterval);
            } catch (Exception e) {
                System.out.println("Error fetching OTP: " + e.getMessage());
                try {
                    Thread.sleep(pollInterval);
                } catch (InterruptedException ie) {
                }
            }
        }
        return null;
    }
    
    // check latest messages for OTP
    private String checkLatestMessagesForOtp() throws IOException {
        Request req = new Request.Builder()
                .url(API_URL + "/messages?page=1&limit=10")
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();
        
        try (Response resp = client.newCall(req).execute()) {
            if (!resp.isSuccessful()) {
                System.out.println("Failed to list messages: " + resp.code());
                return null;
            }
            JsonObject list = gson.fromJson(resp.body().string(), JsonObject.class);
            JsonArray members = list.getAsJsonArray("hydra:member");
            if (members == null || members.size() == 0) {
                return null;
            }
            
            // iterate through messages
            for (int i = 0; i < members.size(); i++) {
                JsonObject msgMeta = members.get(i).getAsJsonObject();
                String id = msgMeta.get("id").getAsString();
                
                // fetch full message
                Request msgReq = new Request.Builder()
                        .url(API_URL + "/messages/" + id)
                        .addHeader("Authorization", "Bearer " + token)
                        .get()
                        .build();
                
                try (Response msgResp = client.newCall(msgReq).execute()) {
                    if (!msgResp.isSuccessful()) {
                        continue;
                    }
                    JsonObject msg = gson.fromJson(msgResp.body().string(), JsonObject.class);
                    String subject = msg.has("subject") ? msg.get("subject").getAsString() : "";
                    String text = "";
                    
                    // check text, intro, or html fields
                    if (msg.has("text") && !msg.get("text").isJsonNull()) {
                        text = msg.get("text").getAsString();
                    } else if (msg.has("intro") && !msg.get("intro").isJsonNull()) {
                        text = msg.get("intro").getAsString();
                    } else if (msg.has("html") && !msg.get("html").isJsonNull()) {
                        text = msg.get("html").getAsString();
                    }
                    
                    // combine subject and text to find OTP
                    String combined = subject + "\n" + text;
                    Matcher m = OTP_PATTERN.matcher(combined);
                    if (m.find()) {
                        return m.group(1);
                    }
                }
            }
        }
        return null;
    }
}
