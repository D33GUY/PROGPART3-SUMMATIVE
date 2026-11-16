/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.poepart1;
import java.io.IOException;
import java.util.*;
/**
 *
 * @author Student
 */
public class PoePart1 {
   
    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);
        
        Login login = new Login();
        boolean running = true;
        
        
          
        
        System.out.println("==========  WELCOME!!   =========");
        System.out.println("==========  MENU!!      =========");
        
        while(running){
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            String choice = input.nextLine();
            
            switch(choice){
                case "1":
                    registerMenu(input, login);
                    break;
                case "2":
                    if(loginMenu(input, login)){
                        quickChat(input, login);
                        running = false;
                    }
                    break;
                case "3":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                    
                default:
                    System.out.println("Invalid Option, please choose 1,2 or 3.");
            }
        }
        
        input.close();
        
    } 
    
    private static void registerMenu(Scanner input, Login login){
        System.out.println("======== USER REGISTRATION ==========");
        
        System.out.print("Enter a username: ");
        String userName = input.nextLine();
        
        System.out.print("Enter a password: ");
        String password = input.nextLine();
        
        System.out.print("Enter a South African cell phone number e.g: +27723456789. ");
        String cellPhoneNumber = input.nextLine();
        
        System.out.print("Enter your name: ");
        String firstName = input.nextLine();
        
        System.out.print("Enter your last name: ");
        String lastName = input.nextLine();
        
        String registrationMessage = login.registerUser(userName, password, cellPhoneNumber, firstName, lastName);
        
        System.out.println(registrationMessage);
    }
    
    private static boolean loginMenu(Scanner input, Login login) throws IOException{
        if(!login.isRegistered()){
            System.out.println("No registered user found. Please register first.");
            return false;
        }
        
        System.out.println("========= USER LOGIN ============");
        while(true){
                 System.out.print("Enter your username(or 'exit' to return): ");
        String userName = input.nextLine();
        
        if(userName.equalsIgnoreCase("exit")){
            return false;
        }
        
        System.out.print("Enter your password: ");
        String password = input.nextLine();
        
            System.out.println(login.returnLoginStatus(userName, password));
            
            if(login.loginUser(userName, password)){
                return true;
            }
        }
      }
      private static void quickChat(Scanner input, Login login) throws IOException{
           System.out.println("\nWelcome to QuickChat");
           
           int loaded = Message.loadStoredMessages();
           if(loaded > 0){
               System.out.println(loaded + " stored message(s) loaded from file.");
           }
           
           String sender = login.getUserName();
            int maxMessages = askForMessageCount(input);
            int messagesEntered = 0;
            boolean quit = false;
            
            while(!quit){
                System.out.println("\n======== QUICKCHAT MENU ==========");
                System.out.println("1. Send Message");
                System.out.println("2. Show recently sent messages ");
                System.out.println("3. Quit. ");
                System.out.println("4. Stored Messages");
                System.out.println("Choose an option: ");
            String choice = input.nextLine().trim();
            
            switch(choice){
                case "1": 
                    if(messagesEntered >= maxMessages){
                        System.out.println("You have already eneterd all" + maxMessages + "messages you set at the start.");
                    }else{
                        messagesEntered = sendMessages(input, maxMessages, messagesEntered, sender);
                    }
                    break;
                case "2": 
                    System.out.println("Coming Soon");
                    break;
                case "3": 
                    quit = true;
                    break;
                case "4":
                    storedMessagesMenu(input);
                default: 
                    System.out.println("Invalid option, please choose 1, 2, or 3.");
           
                }
            }
        }
      
      private static int askForMessageCount(Scanner input){
          while (true){
              System.out.println("How many messages do you wish to enter?");
              
              try{
                  int count = Integer.parseInt(input.nextLine().trim());
                  if (count > 0){
                      return count;
                  }
              }catch(NumberFormatException e){
                  
              }
              System.out.println("Please enter a whole number greater than 0.");
          }
    }
      
      private static int sendMessages(Scanner input, int maxMessages, int messagesEntered, String sender) throws IOException{
          while(messagesEntered < maxMessages){
              System.out.println("\n--- Message " + (messagesEntered + 1) + "of " + maxMessages + "---");
              
              String recipient = askForRecipient(input, messagesEntered);
              String text = askForMessageText(input, messagesEntered);
              
              Message message = new Message(recipient, text, messagesEntered);
              message.setSender(sender);
              System.out.println("Message ID generated: " + message.getMessageID());
              
              handleMessageChoice(input, message);
              messagesEntered++;
          }
          
          System.out.println("\n===== SUMMARY =====");
          System.out.println(Message.printMessages());
          System.out.println("\nTotal number of messages sent: " + Message.returnTotalMessages());
          
          return messagesEntered;
      }
      
      private static String askForRecipient(Scanner input, int messageNumber){
          while(true){
              System.out.println("Enter the recipient's cell phone number(e.g. +27718693002): ");
              String recipient = input.nextLine().trim();
              
              String status = new Message(recipient, "", messageNumber).checkRecipientCell();
              System.out.println(status);
              
              if(status.equals(Message.cellSuccess)){
                  return recipient;
              }
          }
      }
      
      private static String askForMessageText(Scanner input, int messageNumber){
          while(true){
              System.out.println("Enter your message (max 250 characters):");
              String text = input.nextLine().trim();
              
              String status = new Message("", text, messageNumber).checkMessageLength();
              System.out.println(status);
              
              if(status.equals(Message.messageReady)){
                  return text;
              }
          }
      }
      
      private static void handleMessageChoice(Scanner input, Message message) throws IOException{
          
          boolean handled = false;
          
          while(!handled){
              System.out.println("\nWhat would you like to do with thuis message? ");
              System.out.println("1. Send message.");
              System.out.println("2. Disregard message. ");
              System.out.println("3. Store message to send later. ");
              System.out.print("Choose an option: ");
              
            int choice;
            try{
                 choice = Integer.parseInt(input.nextLine().trim());
            }catch(NumberFormatException e){
                System.out.println(Message.invalidOption);
                continue;
            }
            String result = message.sentMessage(choice);
            System.out.println(result);
            
            if(choice == Message.Send){
                System.out.println("\n" + message.getDetails());
                handled = true;
            }else if(choice ==Message.Disregard){
                String confirm = input.nextLine().trim();
                if(confirm.equals("0")){
                    System.out.println(message.disregardMessage());
                    handled = true;
                }
            }else if(choice == Message.Store){
                handled = result.equals(Message.messageStored);
            }
          }
      }
      
      private static void storedMessagesMenu(Scanner input) throws IOException{
          boolean back = false;
          
          while(!back){
              System.out.println("\n===== STORED MESSAGES =====");
              System.out.println("1. Display the sender and recipient of all stored messages");
              System.out.println("2. Display the longest stored message");
              System.out.println("3. Search for a message ID");
              System.out.println("4. Search for all messages sent or stored for a recipient");
              System.out.println("5. Delete a message using its message hash");
              System.out.println("6. Display a report of all stored messages");
              System.out.println("7. Back to the QuickChat menu");
              System.out.print("Choose an option:");
              
              String choice = input.nextLine().trim();
              
              switch(choice){
                  case "1": 
                      System.out.println("\n" + Message.storedSendersAndRecipients());
                      break;
                  case "2":
                      System.out.println("\nLongest stored message:\n" + Message.longestStoredMessage());
                      break;
                  case "3":
                      searchByIdMenu(input);
                      break;
                  case "4":
                      searchByRecipientMenu(input);
                      break;
                  case "5":
                      deleteByHashMenu(input);
                      break;
                  case "6":
                      System.out.println("\n" + Message.storedMessagesReport());
                      break;
                  case "7":
                      back = true;
                      break;
                  default:
                      System.out.println("Invalid option, please choose 1 to 7.");
              } 
          }
      }
      
      private static void searchByIdMenu(Scanner input){
          System.out.println("Enter the message ID to search for: ");
          String id = input.nextLine().trim();
          
          Message found = Message.findByMessageID(id);
          if(found == null){
              System.out.println(Message.messageNotFound);
          }else {
              System.out.println("Recipient: " + found.getRecipient());
              System.out.println("Message: " + found.getMessageText());
          }
      }
      
      private static void searchByRecipientMenu(Scanner input){
          System.out.println("Enter the recipient's cell phone number: ");
          String recipient = input.nextLine().trim();
          
          List<String>results = Message.searchByRecipient(recipient);
          if(results.isEmpty()){
              System.out.println("No message found for " + recipient + ".");
          }else {
              for(String text: results){
                  System.out.println("-" + text);
              }
          }
      }
      
      private static void deleteByHashMenu(Scanner input) throws IOException{
          System.out.println("Enter the message hash of the message to delete: ");
          String hash = input.nextLine().trim();
          
          System.out.println(Message.deleteByMessageHash(hash));
      }
}
