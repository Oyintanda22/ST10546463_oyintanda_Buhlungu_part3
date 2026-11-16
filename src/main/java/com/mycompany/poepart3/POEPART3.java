package com.mycompany.poepart3;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

class Message {
    String id;
    String hash;
    String recipient;
    String content;
    String timestamp;
    String status;

    public Message(String id, String hash, String recipient,
                   String content, String timestamp, String status) {
        this.id = id;
        this.hash = hash;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = timestamp;
        this.status = status;
    }
}

public class POEPART3 {

    // Five parallel arrays
    static ArrayList<String> sentMessages = new ArrayList<>();
    static ArrayList<String> disregardMessages = new ArrayList<>();
    static ArrayList<String> storedMessages = new ArrayList<>();
    static ArrayList<String> messageHashes = new ArrayList<>();
    static ArrayList<String> messageIDs = new ArrayList<>();

    // Master message list
    static ArrayList<Message> messages = new ArrayList<>();

    static Scanner input = new Scanner(System.in);

    static int sentCount = 0;
    static int messageLimit = 0;
    static int messageCounter = 0;

    static String loggedInUsername = "";

    static final String JSON_FILE = "messages.json";

    // ================= VALIDATION METHODS =================

    public static boolean checkUserName(String username) {
        return username != null
                && username.contains("_")
                && username.length() <= 5;
    }

    public static boolean checkPasswordComplexity(String password) {
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return password != null && Pattern.matches(regex, password);
    }

    public static boolean checkCellPhoneNumber(String number) {
        String regex = "^\\+27\\d{9}$";
        return number != null && Pattern.matches(regex, number);
    }

    public static String registerUser(String username, String password) {
        if (!checkUserName(username)) {
            return "Username is not correct. Must have _ and max 5 chars.";
        }

        if (!checkPasswordComplexity(password)) {
            return "Password is not correct. Must have 8+ chars, capital letter, number, and special char.";
        }

        return "Username and password successfully captured. User registered!";
    }

    public static boolean loginUser(String username, String password,
                                    String storedUsername, String storedPassword) {
        return username.equals(storedUsername)
                && password.equals(storedPassword);
    }

    public static String returnLoginStatus(boolean status) {
        if (status) {
            return "Login successful! Welcome back!";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    public static String validateNumber(String num) {
        if (num == null) {
            return "incorrect: Number cannot be empty.";
        }

        if (!num.startsWith("+27")) {
            return "incorrect: Recipient number must start with +27";
        }

        if (num.length() != 12) {
            return "incorrect: Recipient number must contain 9 digits after +27";
        }

        if (!num.substring(3).matches("\\d{9}")) {
            return "incorrect: Only digits allowed after +27";
        }

        return "Recipient number entered successfully";
    }

    // ================= MESSAGE HASH =================

    public static String createMessageHash(String id, int num, String msg) {
        String trimmed = msg.trim();

        String[] words = trimmed.isEmpty()
                ? new String[]{"MSG"}
                : trimmed.split("\\s+");

        String first = words[0];
        String last = words[words.length - 1];

        return (id.substring(0, 2) + ":" + num + ":"
                + first + last).toUpperCase();
    }

    // ================= MAIN METHOD =================

    public static void main(String[] args) {

        String[] credentials = registerUser();

        login(credentials);

        messageLimit = getMessageLimit();

        boolean running = true;

        while (running) {

            displayMenu();

            String choice = input.nextLine().trim();

            switch (choice) {

                case "1":
                    sendMessage();
                    break;

                case "2":
                    showMessages();
                    break;

                case "3":
                    discardLastMessage();
                    break;

                case "4":
                    storedMessages();
                    break;

                case "5":
                    saveAndExit();
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Choose 1-5.");
            }
        }

        input.close();
    }

    // ================= PARALLEL ARRAYS =================

    public static void addMessageToArrays(String id, String recipient,
                                          String content, String status) {

        String timestamp = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss").format(new Date());

        String hash = createMessageHash(id, messages.size(), content);

        Message msg = new Message(
                id, hash, recipient, content, timestamp, status);

        messages.add(msg);

        messageIDs.add(id);
        messageHashes.add(hash);

        if (status.equals("Sent")) {
            sentMessages.add(content);
            sentCount++;

        } else if (status.equals("Stored")) {
            storedMessages.add(content);

        } else if (status.equals("Disregard")) {
            disregardMessages.add(content);
        }
    }

    public static void removeMessageFromArrays(Message m) {

        messages.remove(m);

        messageIDs.remove(m.id);
        messageHashes.remove(m.hash);

        if (m.status.equals("Sent")) {
            sentMessages.remove(m.content);
            sentCount--;

        } else if (m.status.equals("Stored")) {
            storedMessages.remove(m.content);

        } else if (m.status.equals("Disregard")) {
            disregardMessages.remove(m.content);
        }
    }

    // ================= REGISTRATION =================

    public static String[] registerUser() {

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("               REGISTRATION              ");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        String username;

        while (true) {
            System.out.print("Enter Username (must contain '_' and max 5 chars): ");
            username = input.nextLine().trim();

            if (checkUserName(username)) {
                break;
            }

            System.out.println("Invalid username. Example: User_");
        }

        String password;

        while (true) {
            System.out.print("Enter Password (8+ chars, 1 capital, 1 number, 1 special char): ");
            password = input.nextLine();

            if (checkPasswordComplexity(password)) {
                break;
            }

            System.out.println("Invalid password. Example: Password5!");
        }

        String cellPhone;

        while (true) {
            System.out.print("Enter Cell Phone (+27 followed by 9 digits): ");
            cellPhone = input.nextLine().trim();

            if (checkCellPhoneNumber(cellPhone)) {
                System.out.println("Cell phone number successfully added.");
                break;
            }

            System.out.println("Invalid number. Example: +27123456789");
        }

        System.out.println(registerUser(username, password));

        return new String[]{username, password};
    }

    // ================= LOGIN =================

    public static void login(String[] credentials) {

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("                  LOGIN                  ");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        while (true) {

            System.out.print("Enter Username: ");
            String loginUser = input.nextLine();

            System.out.print("Enter Password: ");
            String loginPass = input.nextLine();

            boolean success = loginUser(
                    loginUser, loginPass,
                    credentials[0], credentials[1]);

            System.out.println(returnLoginStatus(success));

            if (success) {

                loggedInUsername = loginUser;

                System.out.println("Welcome back " + loginUser
                        + ", great to see you!");

                System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.println("          WELCOME TO QUICKCHAT            ");
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

                break;
            }
        }
    }

    // ================= MESSAGE LIMIT =================

    public static int getMessageLimit() {

        while (true) {

            try {
                System.out.print("How many messages do you want to send? ");

                int limit = Integer.parseInt(input.nextLine().trim());

                if (limit > 0) {
                    return limit;
                }

                System.out.println("Please enter a number greater than 0.");

            } catch (NumberFormatException e) {

                System.out.println("Please enter a whole number.");
            }
        }
    }

    // ================= MAIN MENU =================

    public static void displayMenu() {

        System.out.println("\n~~~~~~~ QUICKCHAT MENU ~~~~~~~");
        System.out.println("1. Send Message");
        System.out.println("2. Show recently sent messages");
        System.out.println("3. Discard Last Message");
        System.out.println("4. Stored Messages");
        System.out.println("5. Quit");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        System.out.print("Choose an option (1-5): ");
    }

    public static void composeMessage() {
        messageCounter++;
    }

    // ================= SEND MESSAGE =================

    public static void sendMessage() {

        if (sentCount >= messageLimit) {
            System.out.println("[WARNING] Message limit reached!");
            return;
        }

        composeMessage();

        System.out.println("\n~~~~ Message No. " + messageCounter + " ~~~~");

        System.out.print("Enter Recipient Number (+27XXXXXXXXX): ");

        String rec = input.nextLine().trim();

        String validation = validateNumber(rec);

        if (!validation.equals("Recipient number entered successfully")) {
            System.out.println("ERROR: " + validation);
            return;
        }

        String usernameUser;

        while (true) {

            System.out.print("Enter Recipient Username (must contain '_' and max 5 chars): ");

            usernameUser = input.nextLine().trim();

            if (checkUserName(usernameUser)) {
                break;
            }

            System.out.println("Invalid Recipient username. Example: User_");
        }

        System.out.print("Enter your message (max 250 characters): ");

        String msg = input.nextLine();

        if (msg.length() > 250) {

            int excessChars = msg.length() - 250;

            System.out.println("Message exceeds 250 characters by "
                    + excessChars + " characters. Please reduce the size.");

            return;
        }

        System.out.println("\nWhat would you like to do with this message?");
        System.out.println("1. Send Message");
        System.out.println("2. Store Message");
        System.out.println("3. Disregard Message");

        System.out.print("Choose an option (1-3): ");

        String statusChoice = input.nextLine().trim();

        String status;

        switch (statusChoice) {

            case "1":
                status = "Sent";
                break;

            case "2":
                status = "Stored";
                break;

            case "3":
                status = "Disregard";
                break;

            default:
                System.out.println("Invalid choice. Message disregarded.");
                status = "Disregard";
                break;
        }

        if (status.equals("Sent") && sentCount >= messageLimit) {
            System.out.println("Message limit reached. Message cannot be sent.");
            return;
        }

        String id;

        do {
            id = String.format("%010d",
                    (long) (Math.random() * 10000000000L));
        } while (findById(id) != null);

        addMessageToArrays(id, rec, msg, status);

        Message added = messages.get(messages.size() - 1);

        System.out.println("\n...............................");
        System.out.println("MESSAGE ID         : " + added.id);
        System.out.println("HASH               : " + added.hash);
        System.out.println("RECIPIENT NUMBER   : " + rec);
        System.out.println("RECIPIENT USERNAME : " + usernameUser);
        System.out.println("MESSAGE            : " + msg);
        System.out.println("STATUS             : " + status);
        System.out.println("TIME               : " + added.timestamp);
        System.out.println("...............................");

        System.out.println("Message successfully " + status + ".");
    }

    // ================= DISCARD LAST MESSAGE =================

    public static void discardLastMessage() {

        if (messages.isEmpty()) {
            System.out.println("No messages to discard.");
            return;
        }

        Message lastMessage = messages.get(messages.size() - 1);

        removeMessageFromArrays(lastMessage);

        System.out.println("Message discarded: " + lastMessage.content);
    }

    // ================= SHOW MESSAGES =================

    public static void showMessages() {

        System.out.println("\n~~~~~~~ ALL MESSAGES ~~~~~~~");

        if (messages.isEmpty()) {
            System.out.println("No messages found.");
            return;
        }

        int sentLocalCount = 0;
        int storedLocalCount = 0;
        int disregardedLocalCount = 0;

        for (int i = 0; i < messages.size(); i++) {

            Message msg = messages.get(i);

            switch (msg.status.toLowerCase()) {

                case "sent":
                    sentLocalCount++;
                    break;

                case "stored":
                    storedLocalCount++;
                    break;

                case "disregard":
                    disregardedLocalCount++;
                    break;
            }

            System.out.println("ID        : " + msg.id);
            System.out.println("HASH      : " + msg.hash);
            System.out.println("RECIPIENT : " + msg.recipient);
            System.out.println("MESSAGE   : " + msg.content);
            System.out.println("STATUS    : " + msg.status);
            System.out.println("TIME      : " + msg.timestamp);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        }

        System.out.println("\nTotal: " + messages.size()
                + " | Sent: " + sentLocalCount
                + " | Stored: " + storedLocalCount
                + " | Disregarded: " + disregardedLocalCount);
    }

    // ================= SEARCH MESSAGE =================

    public static void searchMessage() {

        System.out.println("\n~~~~~~ SEARCH ~~~~~~");
        System.out.println("1. By Message ID");
        System.out.println("2. By Recipient Number");

        System.out.print("Choose: ");

        String sc = input.nextLine().trim();

        ArrayList<Message> results = new ArrayList<>();

        if (sc.equals("1")) {

            System.out.print("Enter Message ID: ");

            String sid = input.nextLine().trim();

            for (Message m : messages) {
                if (m.id.equalsIgnoreCase(sid)) {
                    results.add(m);
                }
            }

        } else if (sc.equals("2")) {

            System.out.print("Enter Recipient Number: ");

            String srec = input.nextLine().trim();

            for (Message m : messages) {
                if (m.recipient.equalsIgnoreCase(srec)) {
                    results.add(m);
                }
            }

        } else {
            System.out.println("Invalid option.");
            return;
        }

        if (results.isEmpty()) {
            System.out.println("No matching messages found.");

        } else {

            System.out.println("Results (" + results.size() + "):");

            for (Message m : results) {
                System.out.println("[" + m.status + "] "
                        + m.recipient + " - " + m.content);
            }
        }
    }

    // ================= DELETE MESSAGE BY ID =================

    public static void deleteMessageById() {

        if (messages.isEmpty()) {
            System.out.println("INFO: No messages to delete.");
            return;
        }

        System.out.print("Enter Message ID to delete: ");

        String did = input.nextLine().trim();

        Message target = findById(did);

        if (target == null) {
            System.out.println("No message found with ID: " + did);
            return;
        }

        System.out.println("Found: [" + target.status + "] " + target.content);

        System.out.print("Confirm delete? (yes/no): ");

        if (input.nextLine().trim().equalsIgnoreCase("yes")) {

            removeMessageFromArrays(target);

            System.out.println("Message successfully deleted.");

        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    public static Message findById(String id) {

        for (Message m : messages) {
            if (m.id.equalsIgnoreCase(id)) {
                return m;
            }
        }

        return null;
    }

    public static Message findByHash(String hash) {

        for (Message m : messages) {
            if (m.hash.equalsIgnoreCase(hash)) {
                return m;
            }
        }

        return null;
    }

    // ================= STORED MESSAGES MENU =================

    public static void storedMessages() {

        boolean inSubMenu = true;

        while (inSubMenu) {

            System.out.println("\n.......... STORED MESSAGES MENU .............");
            System.out.println("a. Display sender and recipient of all stored messages");
            System.out.println("b. Display the longest stored message");
            System.out.println("c. Search for a message by message ID");
            System.out.println("d. Search message by recipient number");
            System.out.println("e. Delete a message by using message hash");
            System.out.println("f. Display full message report");
            System.out.println("o. Back to main menu");
            System.out.println(".............................................");

            System.out.print("Choose (a-f or o): ");

            String ch = input.nextLine().trim().toLowerCase();

            switch (ch) {

                case "a":
                    displayStoredSenderRecipient();
                    break;

                case "b":
                    displayLongestMessage();
                    break;

                case "c":
                    searchByMessageID();
                    break;

                case "d":
                    searchByRecipient();
                    break;

                case "e":
                    deleteByHash();
                    break;

                case "f":
                    displayFullReport();
                    break;

                case "o":
                    inSubMenu = false;
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // a. Display sender and recipient

    public static void displayStoredSenderRecipient() {

        System.out.println("\n~~~~ STORED MESSAGES: SENDER AND RECIPIENT ~~~~");

        boolean found = false;

        for (Message m : messages) {

            if (m.status.equals("Stored")) {

                System.out.println("Sender    : " + loggedInUsername);
                System.out.println("Recipient : " + m.recipient);
                System.out.println("Message   : " + m.content);
                System.out.println("-----------------------------------");

                found = true;
            }
        }

        if (!found) {
            System.out.println("No stored messages found.");
        }
    }

    // b. Display longest message

    public static String displayLongestMessage() {

        String longest = "";

        for (Message m : messages) {

            if (m.content.length() > longest.length()) {
                longest = m.content;
            }
        }

        if (longest.isEmpty()) {

            System.out.println("No messages available.");

        } else {

            System.out.println("\n~~~ Longest Message ~~~");
            System.out.println(longest);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~");
        }

        return longest;
    }

    // c. Search by message ID

    public static String searchByMessageID() {

        System.out.print("Enter Message ID or Developer Number to search: ");

        return searchByMessageIDLogic(input.nextLine().trim());
    }

    public static String searchByMessageIDLogic(String query) {

        for (Message m : messages) {

            if (m.id.equalsIgnoreCase(query)
                    || m.recipient.equalsIgnoreCase(query)) {

                System.out.println("Recipient : " + m.recipient);
                System.out.println("Message   : " + m.content);

                return m.content;
            }
        }

        System.out.println("No message found for: " + query);

        return "";
    }

    // d. Search by recipient

    public static ArrayList<String> searchByRecipient() {

        System.out.print("Enter Recipient Number: ");

        return searchByRecipientLogic(input.nextLine().trim());
    }

    public static ArrayList<String> searchByRecipientLogic(String recipient) {

        ArrayList<String> results = new ArrayList<>();

        System.out.println("\n~~~~ Messages for recipient: " + recipient + " ~~~~~");

        for (Message m : messages) {

            if (m.recipient.equalsIgnoreCase(recipient)) {

                System.out.println("[" + m.status + "] " + m.content);

                results.add(m.content);
            }
        }

        if (results.isEmpty()) {
            System.out.println("No messages found for this recipient.");
        }

        return results;
    }

    // e. Delete by message hash

    public static boolean deleteByHash() {

        System.out.print("Enter Message Hash to delete: ");

        return deleteByHashLogic(input.nextLine().trim());
    }

    public static boolean deleteByHashLogic(String hash) {

        Message target = findByHash(hash);

        if (target == null) {

            System.out.println("No message found with hash: " + hash);

            return false;
        }

        String deletedContent = target.content;

        removeMessageFromArrays(target);

        System.out.println("Message \"" + deletedContent
                + "\" successfully deleted.");

        return true;
    }

    // f. Full message report

    public static void displayFullReport() {

        System.out.println("\n.......... FULL MESSAGE REPORT ...........");

        if (messages.isEmpty()) {

            System.out.println("No messages available.");

            return;
        }

        System.out.printf("%-20s %-16s %-10s %s%n",
                "Message Hash", "Recipient", "Status", "Message");

        System.out.println("...............................................................");

        for (Message m : messages) {

            String preview = m.content.length() > 35
                    ? m.content.substring(0, 35) + "..."
                    : m.content;

            System.out.printf("%-20s %-16s %-10s %s%n",
                    m.hash, m.recipient, m.status, preview);
        }

        System.out.println("...............................................................");

        System.out.println("Sent: " + sentMessages.size()
                + " | Stored: " + storedMessages.size()
                + " | Disregarded: " + disregardMessages.size());
    }

    // ================= JSON FILE READING =================

    public static void readJsonIntoArray() {

        System.out.println("\n~~~~~ READING JSON FILE INTO ARRAY ~~~~~");

        ArrayList<String> loadedFromFile = new ArrayList<>();

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(JSON_FILE))) {

            String line;

            while ((line = reader.readLine()) != null) {

                line = line.trim();

                if (line.startsWith("\"message\"")) {

                    int colonIndex = line.indexOf(":");

                    if (colonIndex != -1) {

                        String value = line.substring(colonIndex + 1).trim();

                        if (value.startsWith("\"")) {
                            value = value.substring(1);
                        }

                        if (value.endsWith("\",")) {
                            value = value.substring(0, value.length() - 2);
                        } else if (value.endsWith("\"")) {
                            value = value.substring(0, value.length() - 1);
                        }

                        value = value.replace("\\\"", "\"")
                                .replace("\\\\", "\\");

                        loadedFromFile.add(value);

                        if (!storedMessages.contains(value)) {
                            storedMessages.add(value);
                        }
                    }
                }
            }

            if (loadedFromFile.isEmpty()) {

                System.out.println("JSON file is empty or contains no messages.");

            } else {

                System.out.println("SUCCESS: " + loadedFromFile.size()
                        + " message(s) loaded from " + JSON_FILE + ":");

                for (int i = 0; i < loadedFromFile.size(); i++) {
                    System.out.println("[" + (i + 1) + "] "
                            + loadedFromFile.get(i));
                }
            }

        } catch (IOException e) {

            System.out.println("Could not read '" + JSON_FILE
                    + "'. Please save messages first.");
        }
    }

    // ================= JSON ESCAPING =================

    public static String escapeJson(String value) {

        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // ================= SAVE MESSAGES TO JSON =================

    public static void storeMessage() {

        if (messages.isEmpty()) {

            System.out.println("No messages to save.");

            return;
        }

        try (FileWriter fw = new FileWriter(JSON_FILE)) {

            fw.write("[\n");

            for (int i = 0; i < messages.size(); i++) {

                Message m = messages.get(i);

                fw.write("  {\n");

                fw.write("    \"id\": \"" + escapeJson(m.id) + "\",\n");

                fw.write("    \"hash\": \"" + escapeJson(m.hash) + "\",\n");

                fw.write("    \"recipient\": \"" + escapeJson(m.recipient) + "\",\n");

                fw.write("    \"message\": \"" + escapeJson(m.content) + "\",\n");

                fw.write("    \"status\": \"" + escapeJson(m.status) + "\",\n");

                fw.write("    \"time\": \"" + escapeJson(m.timestamp) + "\"\n");

                fw.write("  }" + (i < messages.size() - 1 ? "," : "") + "\n");
            }

            fw.write("]");

            System.out.println("SUCCESS: " + messages.size()
                    + " message(s) saved to " + JSON_FILE + ".");

        } catch (IOException e) {

            System.out.println("Could not write file: " + e.getMessage());
        }
    }

    // ================= SAVE AND EXIT =================

    public static void saveAndExit() {

        while (true) {

            System.out.print("Save before exit? (yes/no): ");

            String save = input.nextLine().trim();

            if (save.equalsIgnoreCase("yes")) {

                storeMessage();

                System.out.println("Messages saved before exit.");

                break;

            } else if (save.equalsIgnoreCase("no")) {

                break;

            } else {

                System.out.println("Invalid input! Please type yes or no.");
            }
        }

        System.out.println("Total messages sent: " + sentCount);

        System.out.println("Goodbye!");
    }
}