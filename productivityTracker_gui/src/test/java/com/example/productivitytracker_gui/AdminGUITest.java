package com.example.productivitytracker_gui;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AdminGUITest extends AdminGUI {


    @Test
    public void test() {

            AdminGUI a=new AdminGUI("petra","35462");
            assertNotNull(a,"The object admin is not null");
            assertNotEquals("", a.getNameField(), "User name field cannot be empty");
            assertNotEquals("", a.getPasswordField(), "Password field cannot be empty");

    }

}
