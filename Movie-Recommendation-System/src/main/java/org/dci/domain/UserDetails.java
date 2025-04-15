package org.dci.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserDetails {
    private Integer user_id;
    private String fullName;
    private LocalDate birthDate;
    private String email;
}
