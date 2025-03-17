package edu.uob;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BasicTests {

    private DBServer server;

    @BeforeEach
    public void setup() {
        server = new DBServer();
    }

    private String generateRandomName() {
        String randomName = "";
        for(int i=0; i<10 ;i++) randomName += (char)(97 + (Math.random() * 25.0));
        return randomName;
    }

    private String sendCommandToServer(String command) {
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> server.handleCommand(command),
                "Server took too long to respond (possible infinite loop)");
    }

    @Test
    public void testCreateDatabaseAndTable2() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"), "Table creation should be successful");
    }

    @Test
    public void testInsertAndSelect2() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Sion', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Rob', 35, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 20, FALSE);");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("Simon"), "Simon should be in the table");
        assertTrue(response.contains("Chris"), "Chris should be in the table");
    }

    @Test
    public void testSelectWithCondition() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Sion', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Rob', 35, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 20, FALSE);");
        String response = sendCommandToServer("SELECT * FROM marks WHERE name != 'Sion';");
        assertFalse(response.contains("Sion"), "Sion should be excluded from the results");
    }

    @Test
    public void testUpdateAndSelect() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 20, FALSE);");
        sendCommandToServer("UPDATE marks SET mark = 38 WHERE name == 'Chris';");
        String response = sendCommandToServer("SELECT * FROM marks WHERE name == 'Chris';");
        assertTrue(response.contains("38"), "Chris' mark should be updated to 38");
    }

    @Test
    public void testDeleteAndSelect() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Sion', 55, TRUE);");
        sendCommandToServer("DELETE FROM marks WHERE name == 'Sion';");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertFalse(response.contains("Sion"), "Sion should be deleted from the table");
    }

    @Test
    public void testErrorHandling() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        String response = sendCommandToServer("SELECT * FROM crew;");
        assertTrue(response.contains("[ERROR]"), "Querying a non-existent table should return an error");
    }

    @Test
    public void testCreateDatabaseAndTable() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("CREATE TABLE coursework (task, submission);");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"), "Table creation should be successful");
    }

    @Test
    public void testInsertAndSelect() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("CREATE TABLE coursework (task, submission);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Sion', 55, TRUE);");
        sendCommandToServer("INSERT INTO coursework VALUES ('OXO', 3);");
        sendCommandToServer("INSERT INTO coursework VALUES ('DB', 1);");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("Simon"), "Simon should be in the table");
        assertTrue(response.contains("Sion"), "Sion should be in the table");
    }

    @Test
    public void testJoinTables() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("CREATE TABLE coursework (task, submission);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Sion', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Rob', 35, FALSE);");
        sendCommandToServer("INSERT INTO coursework VALUES ('OXO', 3);");
        sendCommandToServer("INSERT INTO coursework VALUES ('DB', 1);");
        String response = sendCommandToServer("JOIN coursework AND marks ON submission AND id;");
        assertTrue(response.contains("Simon"), "Simon should be part of the joined table");
        assertTrue(response.contains("DB"), "DB should be part of the joined table");
    }
}
