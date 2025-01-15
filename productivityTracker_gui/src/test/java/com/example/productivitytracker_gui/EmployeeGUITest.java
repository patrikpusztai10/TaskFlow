package com.example.productivitytracker_gui;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EmployeeGUITest extends EmployeeGUI {


    @Test
    public void test() {

        EmployeeGUI e=new  EmployeeGUI("petra","35462");
        assertNotNull(e,"The object employee is not null");
        assertNotEquals("", e.getNameField(), "User name field cannot be empty");
        assertNotEquals("", e.getPasswordField(), "Password field cannot be empty");
        assertEquals("tina",e.getNameField(),"Wrong name for employee");
        assertEquals("12345",e.getPasswordField(),"Wrong password for employee");

    }
}
