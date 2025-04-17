package org.dci.domain;

public class NormalUser extends User {

    private static final int MAX_SUGGESTION_NUMBER = 5;
    public NormalUser() {
        setType(UserType.NORMAL_USER);
    }

    public NormalUser(int id, String username, String password, UserType type) {
        super(id, username, password, type);
    }

    @Override
    public int returnNumberOfSuggestions() {
        return MAX_SUGGESTION_NUMBER;
    }

    @Override
    public boolean canSaveFavorites() {
        return false;
    }
}
