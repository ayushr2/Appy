package main.java.com.bigred.appy;

/**
 * @author Ayush Ranjan
 * @since 16/09/17.
 */

public class User {
    public String name;
    public String photoUriString;
    public String email;
    public String emailClean;
    public long score;
    public long numHelped;

    public User() {

    }

    public User(String name, String photoUriString, String email, String emailClean) {
        this.name = name;
        this.photoUriString = photoUriString;
        this.email = email;
        this.emailClean = emailClean;
        this.score = 0;
        this.numHelped = 0;
    }
}
