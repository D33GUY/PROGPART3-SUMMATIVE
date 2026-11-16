# PROGPART3-SUMMATIVE
QuickChat — Part 3: Store Data and Display Task Report

A Java console application. A user registers, logs in, sends, stores or discards messages, and can then search, delete and report on their stored messages.

Built for PROG5121 — Part 3 (Store data and display task report). This part extends Part 1 (registration and login) and Part 2 (sending messages).

What Part 3 adds
Five arrays, populated while the program runs (nothing is hard-coded):
Array	Contents
Sent messages	Every message that was sent
Disregarded messages	Every message that was disregarded
Stored messages	Every stored message, read back from the JSON file
Message hashes	The hash of every message
Message IDs	The ID of every message
A fourth QuickChat menu option, "Stored Messages", which uses those arrays to:
display the sender and recipient of all stored messages
display the longest stored message
search for a message ID and display the recipient and message
search for all messages sent or stored for a particular recipient
delete a message using its message hash
display a report listing the full details of all stored messages
Reading the JSON file back into an array when QuickChat starts, so messages stored in an earlier run are available again.
Unit tests for all of the above, using the test data from the brief.
Project structure
File	Purpose
Login.java	Registration and login logic (Part 1)
Message.java	Message data, validation, the five arrays, JSON storage, search, delete and reports
PoePart1.java	Entry point; runs the menus and handles user input/output
MessageTest.java	Unit tests for Part 2
StoredMessagesTest.java	Unit tests for Part 3
ci.yml	GitHub Actions workflow that runs the unit tests on every push
javac Login.java Message.java PoePart1.java
java PoePart1
Menu flow
===== MENU =====              ===== QUICKCHAT MENU =====
1. Register                   1) Send Messages
2. Login                      2) Show recently sent messages  ("Coming Soon.")
3. Exit                       3) Quit
                              4) Stored Messages
Register, then log in. Messages can only be sent after a successful login.
After Welcome to QuickChat. enter how many messages you wish to enter.
For each message, enter the recipient and text, then choose to send it, disregard it (press 0 to delete) or store it.
Choose option 4 to open the Stored Messages menu:
1) Display the sender and recipient of all stored messages
2) Display the longest stored message
3) Search for a message ID
4) Search for all messages sent or stored for a recipient
5) Delete a message using its message hash
6) Display a report of all stored messages
7) Back to the QuickChat menu
How the arrays are populated
Action	Effect
Send a message	Added to the sent array; its hash and ID are added to the hash and ID arrays
Store a message	Added to the stored array, written to storedMessages.json, hash and ID recorded
Disregard a message (press 0)	Added to the disregarded array; hash and ID recorded
Start QuickChat	loadStoredMessages() reads storedMessages.json into the stored array
Delete by hash	Removed from the stored array, the JSON file, and the hash and ID arrays
Storing and reading JSON

Stored messages are saved to storedMessages.json as an array of objects:

json
[
  {
    "messageID": "5418048751",
    "messageHash": "54:1:WHERETIME",
    "sender": "kyl_1",
    "recipient": "+27838884567",
    "message": "Where are you? You are late! I have asked you to be on time.",
    "messageNumber": 1
  }
]
The whole file is rewritten each time, so it is always one valid JSON array.
Quotes, backslashes and control characters in a message are escaped when writing and restored when reading, so any message survives a save and reload.
Loading the file when QuickChat starts also stops a new run from overwriting messages stored in an earlier run.
Key methods added in Part 3 (Message.java)
Method	Returns	Description
loadStoredMessages()	int	Reads the JSON file into the stored array; returns how many were loaded
disregardMessage()	String	Adds the message to the disregarded array
longestMessage(List)	String	Text of the longest message in a list
longestStoredMessage()	String	Text of the longest stored message
findByMessageID(String)	Message	The message with that ID, or null
searchByMessageID(String)	String	Message text for an ID, or "Message ID not found."
searchByRecipient(String)	List<String>	Texts of all sent or stored messages for a recipient
deleteByMessageHash(String)	String	Deletes a stored message and updates the JSON file
storedSendersAndRecipients()	String	Sender and recipient of every stored message
sentMessagesReport()	String	Report of all sent messages
storedMessagesReport()	String	Report of the full details of all stored messages
getSentMessages(), getDisregardedMessages(), getStoredMessages(), getMessageHashes(), getMessageIDs()	List	Read-only access to the arrays
Test data (from the brief)
#	Recipient	Message	Flag
1	+27834557896	"Did you get the cake?"	Sent
2	+27838884567	"Where are you? You are late! I have asked you to be on time."	Stored
3	+27834484567	"Yohoooo, I am at your gate."	Disregard
4	0838884567 (developer entry)	"It is dinner time!"	Sent
5	+27838884567	"Ok, I am leaving without you."	Stored
Unit tests (StoredMessagesTest.java)

Each test processes the five test messages by their flag, so the arrays are filled by the program's own logic.

Brief requirement	Test	Expected result
Sent messages array correctly populated	sentMessagesArrayCorrectlyPopulated	"Did you get the cake?", "It is dinner time!"
Disregarded array	disregardedMessagesArrayCorrectlyPopulated	"Yohoooo, I am at your gate."
Stored array read from JSON	storedMessagesArrayIsReadBackFromJsonFile	Messages 2 and 5
Hash and ID arrays	messageHashAndIdArraysContainAllMessages	5 hashes and 5 IDs
Display the longest message	displayLongestMessage, displayLongestStoredMessage	"Where are you? You are late! I have asked you to be on time."
Search for message ID	searchForMessageId, searchForMessageIdNotFound	"It is dinner time!" / "Message ID not found."
Search by recipient	searchAllMessagesForARecipient	Messages 2 and 5 for +27838884567
Delete using a message hash	deleteAMessageUsingItsHash, deleteWithUnknownHash	Message 2 deleted from the array and the file
Sender and recipient display	displaySenderAndRecipientOfStoredMessages	One line per stored message
Display report	displayReportOfSentMessages, displayReportOfStoredMessages	Hash, recipient and message of each message
JSON round trip	specialCharactersSurviveTheJsonFile	Quotes and backslashes survive save and reload




