package org.dci.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class User {
    private Integer id;
    private String username;
    private String password;
    private UserType type;

    public boolean matchPassword(String password) {
        return (PasswordHashing.hashPasswordSHA1(password).equals(this.password));
    }

    public abstract int returnNumberOfSuggestions();
    public abstract boolean canSaveFavorites();
}
