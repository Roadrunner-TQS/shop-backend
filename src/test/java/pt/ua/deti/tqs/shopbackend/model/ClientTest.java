package pt.ua.deti.tqs.shopbackend.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ClientTest {

    @Test
    @DisplayName("Check if the password is correct")
    void checkPassword() {
        Client client = new Client();
        client.setPassword("password");
        assertThat(client.checkPassword("password"), is(true));
    }

    @Test
    @DisplayName("Check if the password is incorrect")
    void checkPassword2() {
        Client client = new Client();
        client.setPassword("password");
        assertThat(client.checkPassword("password2"), is(false));
    }
}