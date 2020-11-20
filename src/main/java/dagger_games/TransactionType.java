package dagger_games;

public enum TransactionType {

    DEPOSIT {
        @Override
        Amount apply(Amount amount, Amount balance) {
            return balance.add(amount);
        }
    },

    WITHDRAWAL {
        @Override
        Amount apply(Amount amount, Amount balance) {
            return balance.subtract(amount);
        }
    };

    abstract Amount apply(Amount amount, Amount balance);

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
