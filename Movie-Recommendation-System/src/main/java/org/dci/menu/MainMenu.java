package org.dci.menu;

import org.dci.domain.User;
import org.dci.domain.UserType;
import org.dci.repository.HistoryRepository;
import org.dci.repository.UserRepository;
import org.dci.utils.Logger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainMenu extends Menu {
    private UserRepository userRepository;
    private HistoryRepository historyRepository;



    public MainMenu(UserRepository userRepository, HistoryRepository historyRepository) {
        super("Welcome to Movie Recommendation system", new ArrayList<>());
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        actions.clear();
        actions.addAll(getActions());
    }

    @Override
    public List<Action> getActions() {
        return new ArrayList<>(Arrays.asList(
                        new Action("Sign Up", this::showSignupPrompt),
                        new Action("Sign In", this::showSignInPrompt)
                ));
    }

    public void showSignupPrompt() {
        //get username
        String username = inputUsername(true);
        if (username.equals("-1")) {
            return;
        }

        //get password
        String password = inputPassword(true);
        if (password.equals("-1")) {
            return;
        }

        //get user type
        int type = inputUserType();
        if (type == -1) {
            return;
        }
        UserType userType = UserType.values()[type];

        // get user first name
        String fullName = inputFullName();
        if (fullName.equals("-1")) {
            return;
        }

        // get user date of birth
        LocalDate birthDate = inputBirthDate();
        if (!birthDate.equals(LocalDate.now())) {
            return;
        }

        // get user email
        String email = inputEmail();
        if (email.equals("-1")) {
            return;
        }

        userRepository.signUp(username, password, userType, fullName, birthDate, email);
        Logger.printInfo("You have successfully created your account.");
        showSignInPrompt();
    }


    public String inputUsername(boolean isNewUser) {
        String username;
        do {
            try {
                if (isNewUser) {
                    System.out.println("Please enter a unique username. It should be lowercase and contain no spaces.");
                } else {
                    System.out.println("Please enter your username: ");
                }
                Logger.printNavigation("To return to the previous menu, enter '-1'.");
                username = scanner.nextLine();
                if (Objects.equals(username, "-1")) {
                    break;
                }

                if (isNewUser) {
                    userRepository.validateUsername(username);
                }
                break;
            } catch (IllegalArgumentException e) {
                Logger.error("An error occurred: " + e.getMessage());
            }
        } while (true);
        return username;
    }

    public String inputPassword(boolean isNewUser) {
        String password;
        do {
            try {
                if (isNewUser) {
                    System.out.println("Please set a password for your account: ");
                    Logger.printHint("""
                            Your password must meet the following requirements:
                            • At least 6 characters long
                            • Contains at least one lowercase letter
                            • Contains at least one uppercase letter
                            • Includes at least one digit""");

                } else {
                    System.out.println("Enter your password to continue. ");
                }

                Logger.printNavigation("To return to the previous menu, enter '-1'.");
                password = scanner.nextLine();
                if (Objects.equals(password, "-1")) {
                    break;
                }

                if (isNewUser) {
                    userRepository.validatePassword(password);
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        } while (true);
        return password;
    }

    public int inputUserType() {
        int type;

        do {
            try {
                System.out.println("Please select your account type.");
                Logger.printHint("Enter '0' for a free account or '1' to upgrade to a premium account (€5/month)");
                Logger.printNavigation("To return to the previous menu, enter '-1'.");
                type = scanner.nextInt();
                scanner. nextLine();

                if (type == -1) {
                    break;
                }

                if (type == 0 || type == 1) {
                    break;
                }

                throw new RuntimeException("Error: Invalid account type. Please enter '0' for free or '1' for premium.");
            } catch (Exception e) {
                if(e.getMessage() != null) {
                    Logger.error("An error occurred: " + e.getMessage());
                } else {
                    Logger.error("Error: Invalid account type. Please enter '0' for free or '1' for premium.");
                }
                scanner. nextLine();
            }
        } while (true);
        return type;
    }

    public String inputFullName() {
        String fullName;
        do {
            try {
                System.out.println("Please enter your full name: ");
                Logger.printNavigation("To return to the previous menu, enter '-1'.");
                fullName = scanner.nextLine();
                if (Objects.equals(fullName, "-1")) {
                    break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        } while (true);
        return fullName;
    }

    public LocalDate inputBirthDate() {
        LocalDate birthDate = LocalDate.now();
        do {
            try {
                System.out.println("Please enter your birth date: (YYYY-MM-DD) ");
                Logger.printNavigation("To return to the previous menu, enter '-1'.");

                String dob = scanner.nextLine();
                if (Objects.equals(dob, "-1")) {
                    break;
                }
                birthDate = userRepository.validateDateOfBirth(dob);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        } while (true);
        return birthDate;
    }

    public String inputEmail() {
        String email;
        do {
            try {
                System.out.println("Please enter your email address: ");
                Logger.printNavigation("To return to the previous menu, enter '-1'.");
                email = scanner.nextLine();
                if (Objects.equals(email, "-1")) {
                    break;
                }
                userRepository.validateEmail(email);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        } while (true);
        return email;
    }

    public void showSignInPrompt() {
        System.out.println("Please log into system:");
        String username = inputUsername(false);
        if (username.equals("-1")) {
            return;
        }

        String password = inputPassword(false);
        if (password.equals("-1")) {
            return;
        }

        try {
            User loggedInUser = userRepository.login(username, password);
            LoggedInUserMenu loggedInUserMenu = new LoggedInUserMenu(loggedInUser,historyRepository);
            loggedInUserMenu.show();
        } catch (Exception e) {
            Logger.error("An error occurred: " + e.getMessage());
        }

    }
}
