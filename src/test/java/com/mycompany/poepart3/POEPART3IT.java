package com.mycompany.poepart3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class POEPART3IT {

    @BeforeEach
    public void setUp() {
        POEPART3.messages.clear();
        POEPART3.sentMessages.clear();
        POEPART3.disregardMessages.clear();
        POEPART3.storedMessages.clear();
        POEPART3.messageHashes.clear();
        POEPART3.messageIDs.clear();
        POEPART3.sentCount = 0;

        POEPART3.addMessageToArrays("0000000001", "+27834557896", "Did you get the cake?", "Sent");
        POEPART3.addMessageToArrays("0000000002", "+27838884567", "Where are you? You are late! I have asked you to be on time.", "Stored");
        POEPART3.addMessageToArrays("0000000003", "+27834484567", "Yohoooo, I am at your gate.", "Disregard");
        POEPART3.addMessageToArrays("0000000004", "0838884567", "It is dinner time!", "Sent");
        POEPART3.addMessageToArrays("0000000005", "+27838884567", "Ok, I am leaving without you.", "Stored");
    }

    @Test
    @DisplayName("Sent messages array contains correct entries")
    public void testSentMessagesContainsCorrectEntries() {
        assertTrue(POEPART3.sentMessages.contains("Did you get the cake?"));
        assertTrue(POEPART3.sentMessages.contains("It is dinner time!"));
    }

    @Test
    @DisplayName("Sent messages array has exactly 2 entries")
    public void testSentMessagesSize() {
        assertEquals(2, POEPART3.sentMessages.size());
    }

    @Test
    @DisplayName("Stored messages are NOT in the sent array")
    public void testStoredNotInSentArray() {
        assertFalse(POEPART3.sentMessages.contains("Where are you? You are late! I have asked you to be on time."));
        assertFalse(POEPART3.sentMessages.contains("Ok, I am leaving without you."));
    }

    @Test
    @DisplayName("Disregarded messages are NOT in the sent array")
    public void testDisregardedNotInSentArray() {
        assertFalse(POEPART3.sentMessages.contains("Yohoooo, I am at your gate."));
    }

    @Test
    @DisplayName("sentCount matches size of sentMessages array")
    public void testSentCountMatchesArray() {
        assertEquals(POEPART3.sentMessages.size(), POEPART3.sentCount);
    }

    @Test
    @DisplayName("Longest message is correctly identified")
    public void testLongestMessageIsCorrect() {
        String result = POEPART3.displayLongestMessage();
        assertEquals("Where are you? You are late! I have asked you to be on time.", result);
    }

    @Test
    @DisplayName("Longest message is longer than all others")
    public void testLongestMessageIsActuallyLongest() {
        String longest = POEPART3.displayLongestMessage();
        for (Message m : POEPART3.messages) {
            assertTrue(longest.length() >= m.content.length());
        }
    }

    @Test
    @DisplayName("Longest message returns empty string when no messages exist")
    public void testLongestMessageEmptyList() {
        POEPART3.messages.clear();
        String result = POEPART3.displayLongestMessage();
        assertEquals("", result);
    }

    @Test
    @DisplayName("Search by developer number returns correct message")
    public void testSearchByDeveloperNumber() {
        String result = POEPART3.searchByMessageIDLogic("0838884567");
        assertEquals("It is dinner time!", result);
    }

    @Test
    @DisplayName("Search by message ID (0000000001) returns correct message")
    public void testSearchByMessageId() {
        String result = POEPART3.searchByMessageIDLogic("0000000001");
        assertEquals("Did you get the cake?", result);
    }

    @Test
    @DisplayName("Search for unknown ID returns empty string")
    public void testSearchByMessageIdNotFound() {
        String result = POEPART3.searchByMessageIDLogic("NOTEXISTS");
        assertEquals("", result);
    }

    @Test
    @DisplayName("Recipient search returns 2 results for +27838884567")
    public void testSearchByRecipientResultCount() {
        ArrayList<String> results = POEPART3.searchByRecipientLogic("+27838884567");
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("Recipient search returns correct message content")
    public void testSearchByRecipientContent() {
        ArrayList<String> results = POEPART3.searchByRecipientLogic("+27838884567");
        assertTrue(results.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue(results.contains("Ok, I am leaving without you."));
    }

    @Test
    @DisplayName("Unknown recipient returns empty result list")
    public void testSearchByRecipientNotFound() {
        ArrayList<String> results = POEPART3.searchByRecipientLogic("+27000000000");
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Delete by hash returns true for a valid hash")
    public void testDeleteByHashReturnsTrue() {
        String hash = getHashForContent("Where are you? You are late! I have asked you to be on time.");
        assertNotNull(hash);
        assertTrue(POEPART3.deleteByHashLogic(hash));
    }

    @Test
    @DisplayName("Delete by hash reduces messages list size by 1")
    public void testDeleteByHashReducesMessageCount() {
        String hash = getHashForContent("Where are you? You are late! I have asked you to be on time.");
        int before = POEPART3.messages.size();
        POEPART3.deleteByHashLogic(hash);
        assertEquals(before - 1, POEPART3.messages.size());
    }

    @Test
    @DisplayName("Delete by hash reduces storedMessages array by 1")
    public void testDeleteByHashReducesStoredArray() {
        String hash = getHashForContent("Where are you? You are late! I have asked you to be on time.");
        int before = POEPART3.storedMessages.size();
        POEPART3.deleteByHashLogic(hash);
        assertEquals(before - 1, POEPART3.storedMessages.size());
    }

    @Test
    @DisplayName("Deleted message no longer appears in messages list")
    public void testDeletedMessageIsGone() {
        String hash = getHashForContent("Where are you? You are late! I have asked you to be on time.");
        POEPART3.deleteByHashLogic(hash);
        for (Message m : POEPART3.messages) {
            assertNotEquals("Where are you? You are late! I have asked you to be on time.", m.content);
        }
    }

    @Test
    @DisplayName("Delete by hash returns false for an unknown hash")
    public void testDeleteByHashReturnsFalse() {
        assertFalse(POEPART3.deleteByHashLogic("BADHASH"));
    }

    @Test
    @DisplayName("Delete with unknown hash does not change message count")
    public void testDeleteByHashUnknownNoChange() {
        int before = POEPART3.messages.size();
        POEPART3.deleteByHashLogic("BADHASH");
        assertEquals(before, POEPART3.messages.size());
    }

    @Test
    @DisplayName("Report: total message count is 5")
    public void testReportTotalMessages() {
        assertEquals(5, POEPART3.messages.size());
    }

    @Test
    @DisplayName("Report: messageHashes array has 5 entries")
    public void testReportHashCount() {
        assertEquals(5, POEPART3.messageHashes.size());
    }

    @Test
    @DisplayName("Report: messageIDs array has 5 entries")
    public void testReportIDCount() {
        assertEquals(5, POEPART3.messageIDs.size());
    }

    @Test
    @DisplayName("Report: array distribution is 2 Sent, 2 Stored, 1 Disregarded")
    public void testReportArrayDistribution() {
        assertEquals(2, POEPART3.sentMessages.size());
        assertEquals(2, POEPART3.storedMessages.size());
        assertEquals(1, POEPART3.disregardMessages.size());
    }

    @Test
    @DisplayName("Report: no message has null or empty hash/id/recipient/content")
    public void testReportNoNullFields() {
        for (Message m : POEPART3.messages) {
            assertNotNull(m.id);
            assertNotNull(m.hash);
            assertNotNull(m.recipient);
            assertNotNull(m.content);
            assertFalse(m.id.isEmpty());
            assertFalse(m.hash.isEmpty());
            assertFalse(m.recipient.isEmpty());
            assertFalse(m.content.isEmpty());
        }
    }

    @Test
    @DisplayName("Valid SA number passes validateNumber")
    public void testValidateNumberValid() {
        assertEquals("Recipient number entered successfully", POEPART3.validateNumber("+27834557896"));
    }

    @Test
    @DisplayName("Number without +27 fails validateNumber")
    public void testValidateNumberNoPrefix() {
        assertTrue(POEPART3.validateNumber("0834557896").contains("incorrect"));
    }

    @Test
    @DisplayName("Too-short number fails validateNumber")
    public void testValidateNumberTooShort() {
        assertTrue(POEPART3.validateNumber("+2783455").contains("incorrect"));
    }

    @Test
    @DisplayName("Valid username passes checkUserName")
    public void testCheckUserNameValid() {
        assertTrue(POEPART3.checkUserName("Us_er"));
    }

    @Test
    @DisplayName("Username without underscore fails")
    public void testCheckUserNameNoUnderscore() {
        assertFalse(POEPART3.checkUserName("UserA"));
    }

    @Test
    @DisplayName("Username over 5 chars fails")
    public void testCheckUserNameTooLong() {
        assertFalse(POEPART3.checkUserName("User_Name"));
    }

    @Test
    @DisplayName("Valid password passes checkPasswordComplexity")
    public void testPasswordValid() {
        assertTrue(POEPART3.checkPasswordComplexity("Password1!"));
    }

    @Test
    @DisplayName("Password without uppercase fails")
    public void testPasswordNoUppercase() {
        assertFalse(POEPART3.checkPasswordComplexity("password1!"));
    }

    @Test
    @DisplayName("Password without digit fails")
    public void testPasswordNoDigit() {
        assertFalse(POEPART3.checkPasswordComplexity("Password!!"));
    }

    @Test
    @DisplayName("Password without special character fails")
    public void testPasswordNoSpecial() {
        assertFalse(POEPART3.checkPasswordComplexity("Password1A"));
    }

    @Test
    @DisplayName("Hash format starts with correct ID prefix and sequence number")
    public void testCreateMessageHashFormat() {
        String hash = POEPART3.createMessageHash("0000000001", 0, "Did you get the cake?");
        assertTrue(hash.startsWith("00:0:"));
        assertTrue(hash.contains("DID"));
        assertTrue(hash.contains("CAKE?"));
    }

    private String getHashForContent(String content) {
        for (Message m : POEPART3.messages) {
            if (m.content.equals(content)) return m.hash;
        }
        return null;
    }
}