package com.example.hw01cst438;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

public class UserTests {
    @Test
    public void nullUser() {
        assertEquals(null, MainActivity.searchForUser("a", "b"));
    }

    @Test
    public void verifyUserCredentials() {
        ArrayList<String> test1 = (MainActivity.searchForUser("admin", "admin"));
        assertEquals(3, test1.size());

        ArrayList<String> test2 = (MainActivity.searchForUser("admin2", "admin2"));
        assertEquals(3, test2.size());

        ArrayList<String> test3 = (MainActivity.searchForUser("admin3", "admin3"));
        assertEquals(3, test3.size());
    }

    @Test
    public void verifyUsernameOnly() {
        ArrayList<String> test1 = (MainActivity.searchForUser("admin", "a"));
        assertEquals(1, test1.size());

        ArrayList<String> test2 = (MainActivity.searchForUser("admin", "admin"));
        assertEquals(3, test2.size());
    }
}
