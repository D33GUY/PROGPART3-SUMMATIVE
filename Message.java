/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.poepart1;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Student
 */
public class Message {
   public static final int Send = 1;
   public static final int Disregard = 2;
   public static final int Store = 3;
   
   public static final int maxMessageLength = 250;
   
   public static final String messageReady = "Message ready to send.";
   public static final String cellSuccess = "Cell number successfully captured.";
   public static final String cellFailure = "Cell number is incorrectly formatted or does not have an international code.";
   public static final String messageSent = "Message successfully sent.";
   public static final String messageDelete = "Press 0 to delete the message.";
   public static final String messageDeleted = "Message deleted.";
   public static final String messageStored = "Message successfully stored.";
   public static final String messageNotStored = "Message could not be stored.";
   public static final String invalidOption = "Invalid option. Please choose 1(send), 2(Disregard), 3(store)";
   public static final String messageNotFound = "Message ID not found.";
   public static final String messageHashNotFound = "Message hash not found.";
   public static final String messageNotDeleted = "Message could not be deleted.";
   public static final String noMessages = "No messages found.";
   
   private static final List<Message> sentMessage = new ArrayList<>();
   private static final List<Message> disregardedMessages = new ArrayList<>();
    private static final List<Message> storedMessage = new ArrayList<>();
    private static final List<String> messageHashes = new ArrayList<>();
    private static final List<String> messageIDs = new ArrayList<>();
    private static String storageFile = "storedMessage.json";
    
    private String messageID;
    private String recipient;
    private String messageText;
    private int messageNumber;
    private String messageHash;
   private String sender = "Uknown";
   
    public Message(String recipient, String messageText, int messageNumber){
        this(generateMessageID(), recipient, messageText, messageNumber);   
    }
    
    public Message(String messageID, String recipient, String messageText, int messageNumber){
        this(messageID, recipient, messageText, messageNumber, null);
    }
    public Message(String messageID, String recipient,String messageText, int messageNumber, String messageHash){
        this.messageID = messageID;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageNumber = messageNumber;
        this.messageHash =  (messageHash != null) ? messageHash : createMessageHash();
    }
    
    private static String generateMessageID(){
         long id = ThreadLocalRandom.current().nextLong(10_000_000_000L);
        return String.format("%010d", id);
    }
    
    public boolean checkMessageID(){
        return messageID != null && messageID.length() <=10;
    }
    
    public String checkRecipientCell(){
        Login login = new Login();
        
        if(login.checkCellPhoneNumber(recipient)){
            return cellSuccess;
        }
            return cellFailure; 
    }
    
    public String checkMessageLength(){
        int length = messageText.length();
        if(length <= maxMessageLength){
            return messageReady;
        }
        int excess = length - maxMessageLength;
        return "Message exceeds 250 characters by " + excess + ", please reduce the size.";
    }
    
    public String createMessageHash(){
        String idPrefix = messageID.length() >=2 ? messageID.substring(0,2) : messageID;
        
        String [] words = messageText.trim().split("\\s+");
        String firstWord = stripPunctuation(words[0]);
        String lastWord = stripPunctuation(words[words.length -1]);
        
        return (idPrefix + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
    }
    
    private static String stripPunctuation(String word){
        return word.replaceAll("[A-Za-z0-9]", "");
    }
    
    public String sentMessage(int choice) throws IOException{
        switch (choice){
            case Send:
                sentMessage.add(this);
                return messageSent;
            case Disregard:
                return messageDelete;
            case Store:
                return storeMessage() ? messageStored : messageNotStored;
            default:
                return invalidOption;
        }
    }
    
    public String disregardMessage() {
           disregardedMessages.add(this);
             recordIdentifiers();
            return messageDeleted;
    }
    
    private void recordIdentifiers() {
        messageHashes.add(messageHash);
        messageIDs.add(messageID);
    }
    
    private void removeIdentifiers(){
        int index = messageIDs.indexOf(messageID);
        if (index >= 0){
            messageIDs.remove(index);
            messageHashes.remove(index);
        }
    }
    
    public boolean storeMessage() throws IOException{
        storedMessage.add(this);
        if(writeStoredMessageToFile()){
            return true;
        }
        storedMessage.remove(this);
        return false;
    }
    
    private static boolean writeStoredMessageToFile() throws IOException{
        StringBuilder json = new StringBuilder("\n");
         for(int i = 0; i<storedMessage.size(); i++){
             Message m = storedMessage.get(i);
             json.append(" {\n");
             json.append( "\"messageID\": \"").append(escapeJson(m.messageID)).append("\",\n");
             json.append( "\"messageHash\":\"").append(escapeJson(m.messageHash)).append("\",\n");
             json.append(" \"sender\": \"").append(escapeJson(m.sender)).append("\",\n");
             json.append( "\"recipient\": \"").append(escapeJson(m.recipient)).append("\",\n");
             json.append( "\"message\": \"").append(escapeJson(m.messageText)).append("\",\n");
             json.append(" \"messageNumber\": ").append(m.messageNumber).append("\n");
             
             json.append( " }");
             if(i < storedMessage.size() -1 ){
                 json.append(",");
             }
             json.append("\n");   
         }
         json.append("]\n");
        try{
             Files.write(Paths.get(storageFile), json.toString().getBytes(StandardCharsets.UTF_8));
             return true;
        } catch(IOException e){
            return false;
        }    
    }  
    private static String escapeJson(String text){
       StringBuilder escaped = new StringBuilder();
       for (char c : text.toCharArray()){
          switch (c) {
              case '"':
                  escaped.append("\\\"");
                  break;
              case '\\':
                  escaped.append("\\\\");
                  break;
              case '\n':
                 escaped.append("\\n");
                 break;
              case 'r':
                  escaped.append("\\r");
                  break;
              case 't':
                  escaped.append("\\t");
                  break;
              default:
                  if(c <0*20){
                      escaped.append(String.format("\\u%04x", (int) c));
                  }else {
                      escaped.append(c);
                  }
          } 
       }
       return escaped.toString();
    }
    
    public static int loadStoredMessages() {
        Path path = Paths.get(storageFile);
        if (!Files.exists(path)){
            return storedMessage.size();
        }
        
         List<Message> loaded;
         try{
             String json = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
             loaded = parseMessages(json);
         } catch (IOException e) {
             return storedMessage.size();
         }
         
         
        storedMessage.clear();
        storedMessage.addAll(loaded);
        
        for(Message m : storedMessage) {
            if (!messageIDs.contains(m.messageID)) {
                 m.recordIdentifiers();
            }
        }
        return storedMessage.size();
    }
    
    private static List<Message> parseMessages(String json){
        List<Message> result = new ArrayList<>();
        Map<String, String> current = null;
        String pendingKey = null;
        int i = 0;
        
        while (i < json.length()){
            char c = json.charAt(i);
            
            if (c == '{'){
                 current = new HashMap<>();
                pendingKey = null;
                i++;
            }else if (c == '}'){
                if (current != null){
                     Message m = buildMessage(current);
                    if (m != null){
                        result.add(m);
                    }
                }
                 current = null;
                 i++;
            }else if (c == '"') {
                  StringBuilder text = new StringBuilder();
                   i++;
                   while (i < json.length() && json.charAt(i) != '"'){
                       char ch = json.charAt(i);
                        if (ch == '\\' && i + 1 < json.length()){
                            i++;
                            char escape = json.charAt(i);
                            switch (escape){
                                case 'n':
                                    text.append('\n');
                                break;
                                case 'r':
                                    text.append('\r');
                                break;
                                case 't':
                                    text.append('\t');
                                break;
                                case 'u':
                                    if (i + 4 < json.length()){
                                        try{
                                             text.append((char) Integer.parseInt(json.substring(i + 1, i + 5), 16));
                                        }catch (NumberFormatException e){
                                            text.append('?');
                                        }
                                        i += 4;
                                    }
                                    break;
                                    default:
                                            text.append(escape);
                            }
                        }else{
                            text.append(ch);
                        }
                        i++;
                   }
                   i++;
                    if (current != null){
                        if (pendingKey == null){
                            pendingKey = text.toString(); 
                        }else{
                             current.put(pendingKey, text.toString());
                              pendingKey = null;
                        }
                    }
            }else if(current !=null&& pendingKey != null && (Character.isDigit(c) || c == '-')){
                int start = i;
                while (i < json.length()
                        && (Character.isDigit(json.charAt(i)) || json.charAt(i) == '-')){
                    i++;
                }
                current.put(pendingKey, json.substring(start, i));
                pendingKey = null;
            }else{
                i++;
            }
        }
        return result;
    }
    
    private static Message buildMessage(Map<String, String> fields) {
         String id = fields.get("messageID");
        if (id == null){
            return null;
        }
        int number;
        try{
            number = Integer.parseInt(fields.getOrDefault("messageNumber", "0"));
        }catch (NumberFormatException e){
            number = 0;
        }
        Message m = new Message(id,
                fields.getOrDefault("recipient", ""),
                fields.getOrDefault("message", ""),
                number,
                fields.get("messageHash"));
        m.sender = fields.getOrDefault("sender", "Unknown");
        return m;
    }
    
    private static List<Message> allMessages(){
          List<Message> all = new ArrayList<>();
        all.addAll(sentMessage);
        all.addAll(storedMessage);
        all.addAll(disregardedMessages);
        return all;
    }
    
     public static String longestMessage(List<Message> messages) {
         if (messages == null || messages.isEmpty()){
              return noMessages;
         }
          Message longest = messages.get(0);
        for (Message m : messages){
             if (m.messageText.length() > longest.messageText.length()){
                 longest = m;
             }
        }
        return longest.messageText;
     }
     
     public static String longestStoredMessage(){
          return longestMessage(storedMessage);
     }
     
     public static Message findByMessageID(String id){
          if (id == null){
              return null;
          }
           String wanted = id.trim();
        for (Message m : allMessages()){
            if (m.messageID.equals(wanted)){
                 return m;
            }
        }
        return null;
     }
     
     public static String searchByMessageID(String id){
         Message found = findByMessageID(id);
        return (found == null) ? messageNotFound : found.messageText;
     }
     
     public static List<String> searchByRecipient(String recipient){
         List<String> found = new ArrayList<>();
        if (recipient == null){
            return found; 
        }
        String wanted = recipient.trim();
        
        for (Message m : sentMessage){
            if (m.recipient.equals(wanted)){
               found.add(m.messageText);  
            }
        }
         for (Message m : storedMessage) {
             if (m.recipient.equals(wanted)) {
                 found.add(m.messageText);
             }
         }
         return found;
     }
     
     public static String deleteByMessageHash(String hash) throws IOException{
         if (hash == null){
            return messageNotFound; 
         }
         String wanted = hash.trim();
         
         for (int i = 0; i < storedMessage.size(); i++){
             Message m = storedMessage.get(i);
            if (m.messageHash.equalsIgnoreCase(wanted)) {
                storedMessage.remove(i);
                
                 if (!writeStoredMessageToFile()){
                     storedMessage.add(i, m);
                     return messageNotDeleted;
                 } 
                 m.removeIdentifiers();
                 return "Message: \"" + m.messageText + "\" successfully deleted.";
            }
         }
         return messageHashNotFound;
     }
     
     public static String storedSendersAndRecipients(){
        if (storedMessage.isEmpty()) {
            return noMessages;   
        }
        StringBuilder lines = new StringBuilder();
        for (int i = 0; i < storedMessage.size(); i++) {
             Message m = storedMessage.get(i);
            lines.append("Sender: ").append(m.sender)
                    .append(" | Recipient: ").append(m.recipient);
            if (i < storedMessage.size() - 1){
                lines.append("\n");
            }
        }
        return lines.toString();
     }
     
     public static String sentMessagesReport(){
         return buildReport("===== SENT MESSAGES REPORT =====", sentMessage);
     }
     
     public static String storedMessagesReport(){
         
        return buildReport("===== STORED MESSAGES REPORT =====", storedMessage);
     }
     
     private static String buildReport(String title, List<Message> messages) {
         StringBuilder report = new StringBuilder(title);
         if (messages.isEmpty()){
              report.append("\n").append(noMessages);
            return report.toString();
         }
         for (int i = 0; i < messages.size(); i++){
            report.append("\n\nMessage ").append(i + 1).append("\n");
            report.append(messages.get(i).getFullDetails()); 
         }
         return report.toString();
     }
    
    public String getDetails(){
        return "MessageID: "+ messageID
                +"\nMessageHash: " + messageHash
                +"\nRecipient: " + recipient
                + "\nMessage: " + messageText;
    }
    
    
    public String getFullDetails(){
         return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nSender: " + sender
                + "\nRecipient: " + recipient
                + "\nMessage: " + messageText;
    }
    
    public static String printMessages(){
        if(sentMessage.isEmpty()){
            return "No messages have been sent.";
        }
        StringBuilder all = new StringBuilder();
        for (int i = 0; i < sentMessage.size(); i++){
           all.append(sentMessage.get(i).getDetails());
           if(i < sentMessage.size() -1){
               all.append("\n\n");
           }
        }
        return all.toString();
    }
    
    public static int returnTotalMessages(){
        return sentMessage.size();
    }
    
      public static List<Message> getSentMessages(){
          return Collections.unmodifiableList(sentMessage);
      }
      
      public static List<Message> getDisregardedMessages() {
          return Collections.unmodifiableList(disregardedMessages);
      }
      
      public static List<Message> getStoredMessages() {
          return Collections.unmodifiableList(storedMessage);
      }
      
      public static List<String> getMessageHashes(){
         return Collections.unmodifiableList(messageHashes); 
      }
      
      public static List<String> getMessageIDs(){
           return Collections.unmodifiableList(messageIDs);
      }
    
    public static void resetSession(){
       sentMessage.clear();
       disregardedMessages.clear();
       storedMessage.clear();
       messageHashes.clear();
       messageIDs.clear();
    }
    
    public static void setStorageFile(String fileName){
        storageFile = fileName;
    } 
    
    public String getMessageID(){
        return messageID;
    }
    
    public String getMessageHash(){
        return messageHash;
    }
    
    public String getRecipient(){
        return recipient;
    }
    
    public String getMessageText(){
        return messageText;
    }
    
    public int getMessageNumber(){
        return messageNumber;
    }
    
    public String getSender(){
        return sender;
    }
    
    public void setSender(String sender){
        this.sender = sender;
    }

 
}
