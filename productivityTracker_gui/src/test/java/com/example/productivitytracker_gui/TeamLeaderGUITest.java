package com.example.productivitytracker_gui;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TeamLeaderGUITest extends TeamLeaderGUI{


    @Test
    public void test() {

        TeamLeaderGUI t=new  TeamLeaderGUI("tina","12345");
        assertNotNull(t,"The object teamleader is not null");
        assertNotEquals("", t.getNameField(), "User name field cannot be empty");
        assertNotEquals("", t.getPasswordField(), "Password field cannot be empty");
        assertEquals("tina",t.getNameField(),"Wrong name for teamleader");
        assertEquals("12345",t.getPasswordField(),"Wrong password for teamleader");

    }
}
