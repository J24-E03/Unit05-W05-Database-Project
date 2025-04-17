package org.dci.domain;

public class PremiumUser extends User {

    private static final int MAX_SUGGESTION_NUMBER = 10;
    public PremiumUser() {
        setType(UserType.PREMIUM_USER);
    }

    @Override
    public int returnNumberOfSuggestions() {
        return MAX_SUGGESTION_NUMBER;
    }

    @Override
    public boolean canSaveFavorites() {
        return true;
    }
}
