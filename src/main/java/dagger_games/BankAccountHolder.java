package dagger_games;

public class BankAccountHolder {

    public final String firstName;
    public final String lastName;
    public final String personalId;
    public final String email;

    BankAccountHolder(String firstName, String lastName, String personalId, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.email = email;
    }
}
