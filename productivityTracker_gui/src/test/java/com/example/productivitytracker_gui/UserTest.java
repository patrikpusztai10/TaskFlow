package com.example.productivitytracker_gui;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest extends User {


    @Test
    public void test() {

        User u=new User("petra","35462");
        assertNotNull(u,"The object user is not null");
        assertNotEquals("", u.getNameField(), "User name field cannot be empty");
        assertNotEquals("", u.getPasswordField(), "Password field cannot be empty");



    }
    @Test
    public void testDateFormatter() {
        User u = new User();
        String formattedDate = u.date_formatter();
        assertNotNull(formattedDate, "The formatted date should not be null");
        assertTrue(formattedDate.matches("\\d{2}-\\d{2}-\\d{4}"), "The date format should match dd-MM-yyyy");
    }


}
