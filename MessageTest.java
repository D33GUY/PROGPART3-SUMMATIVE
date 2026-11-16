/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.poepart1;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

/**
 *
 * @author Student
 */
public class MessageTest {
    
    private static final String TEST_FILE = "test-storedMessages.json";
        private Message message1;
        private Message message2;

@BeforeEach
    void setUp() {
        Message.resetSession();
        Message.setStorageFile(TEST_FILE);

        // Message IDs are supplied so the expected hashes are predictable
        message1 = new Message("0012345678", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        message2 = new Message("0198765432", "08575975889",
                "Hi Keegan, did you receive the payment?", 1);
    }

@AfterEach
    void tearDown() {
        new File(TEST_FILE).delete();
    }

 

@Test
    void messageLengthSuccess() {
        assertEquals("Message ready to send.", message1.checkMessageLength());
    }

@Test
    void messageLengthFailure() {
        String longText = new String(new char[260]).replace('\0', 'a');
        Message longMessage = new Message("0012345678", "+27718693002", longText, 0);

        assertEquals("Message exceeds 250 characters by 10, please reduce the size.",
                longMessage.checkMessageLength());
    }

@Test
    void recipientNumberCorrectlyFormatted() {
        assertEquals("Cell phone number successfully captured.", message1.checkRecipientCell());
    }

@Test
    void recipientNumberIncorrectlyFormatted() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.",
                message2.checkRecipientCell());
    }

    @Test
    void messageHashTestCase1() {
        assertEquals("00:0:HITONIGHT", message1.createMessageHash());
    }

    @Test
    void messageHashesForAllTestMessages() {
        Message[] messages = {message1, message2};
        String[] expectedHashes = {"00:0:HITONIGHT", "01:1:HIPAYMENT"};

        for (int i = 0; i < messages.length; i++) {
            assertEquals(expectedHashes[i], messages[i].createMessageHash());
        }
    }

    @Test
    void messageIdIsCreated() {
        Message generated = new Message("+27718693002", "Hi there", 0);

        assertTrue(generated.checkMessageID());
        assertTrue(generated.getMessageID().matches("\\d{10}"));
    }
    
    @Test
    void userSelectsSendMessage() throws IOException {
        assertEquals("Message successfully sent.", message1.sentMessage(Message.Send));
    }

    @Test
    void userSelectsDisregardMessage() throws IOException {
        assertEquals("Press 0 to delete the message.", message2.sentMessage(Message.Disregard));
    }

    @Test
    void userSelectsStoreMessage() throws IOException {
        assertEquals("Message successfully stored.", message1.sentMessage(Message.Store));
    }

    @Test
    void totalNumberOfMessagesSent() throws IOException {
        message1.sentMessage(Message.Send);      
        message2.sentMessage(Message.Disregard);  

        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    void printMessagesListsSentMessages() throws IOException {
        message1.sentMessage(Message.Send);

        String printed = Message.printMessages();
        assertTrue(printed.contains("00:0:HITONIGHT"));
        assertTrue(printed.contains("+27718693002"));
    }
}