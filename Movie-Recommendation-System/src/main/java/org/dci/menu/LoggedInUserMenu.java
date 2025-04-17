package org.dci.menu;

import org.dci.client.MovieDetails;
import org.dci.domain.*;

import org.dci.repository.ActorRepository;
import org.dci.repository.MovieRepository;
import org.dci.repository.QueryRepository;
import org.dci.repository.UserRepository;
import org.dci.service.MovieRecommendationService;
import org.dci.utils.Colors;
import org.dci.utils.Logger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class LoggedInUserMenu extends Menu {

    private User loggedInUser;
    private final MovieRecommendationService movieService = MovieRecommendationService.getInstance();
    private final MovieRepository movieRepository = MovieRepository.getInstance();
    private final QueryRepository queryRepository = QueryRepository.getInstance();
    private final UserRepository userRepository = UserRepository.getInstance();
    private final ActorRepository actorRepository = ActorRepository.getInstance();


    public LoggedInUserMenu(User user) {
        super("Welcome " + user.getUsername(), new ArrayList<>());
        loggedInUser = user;
        actions.clear();
        actions.addAll(getActions());
    }

    @Override
    protected boolean shouldContinueRunning() {
        return super.shouldContinueRunning() && loggedInUser != null;
    }

    @Override
    public List<Action> getActions() {
        List<Action> actionList = new ArrayList<>();
        actionList.add(new Action("Get Movie Recommendation", this::showGetMovieRecommendation));
        actionList.add(new Action("View Chat History", this::viewHistory));
        if (loggedInUser.getType() == UserType.NORMAL_USER) {
            actionList.add(new Action("Upgrade to premium", this::upgradeToPremium));
        }
        actionList.add(new Action("View My Profile Information", this::viewUserInfo));
        actionList.add(new Action("Update My Profile Information", this::updateUserInfo));
        actionList.add(new Action("Logout", this::logOut));
        return actionList;
    }

    public void showGetMovieRecommendation() {
        System.out.println("Tell us your favorite movie genres, and we'll find the best recommendations for you! ");
        String userInput = scanner.nextLine();
        System.out.print(Colors.colorize("Here are some movie recommendations based on your preferences ", Colors.ANSI_BOLD_CYAN));
        try {
            for (int i = 0; i < 5; i++) {
                System.out.print(Colors.colorize(".", Colors.ANSI_BOLD_CYAN));
                Thread.sleep(200);
            }

            System.out.println();
            List<MovieDetails> moviesDetails = movieService.getMovieRecommendations(userInput, loggedInUser.returnNumberOfSuggestions());


            if (moviesDetails.isEmpty()) {
                Logger.printResult("Sorry, I couldn't find any movie based of what you want.");
                return;
            }

            List<Movie> movies = new ArrayList<>();
            moviesDetails.forEach(movie -> {
                List<Actor> actors = movieService.getMovieActor(movie.getId());
                actors.forEach(actorRepository::addNewActor);
                Optional<Movie> movieOptional = movieRepository.addNewMovie(movie, actors);
                movieOptional.ifPresent(movies::add);
            });


            movies.forEach(movie -> Logger.printResult(movie.makeMovieDetailsReadyForPrint()));
            queryRepository.addNewQuery(loggedInUser.getId(), userInput, movies);


        } catch (InterruptedException e) {
            Logger.error("An error occurred: " + e.getMessage());
        }
    }

    private void viewHistory() {
       HistoryMenu historyMenu = new HistoryMenu(loggedInUser);
       historyMenu.show();
    }

    public void upgradeToPremium() {
        boolean keepAsking = true;
        do {
            System.out.println("Upgrade to Premium and enjoy exclusive features! Would you like to upgrade now? (Y/N)");
            String userInput = scanner.nextLine();

            if (userInput.equalsIgnoreCase("N")) {
                return;
            }
            if (userInput.equalsIgnoreCase("Y")) {
                keepAsking = false;
                if (userRepository.upgradeUser(loggedInUser)) {
                    loggedInUser.setType(UserType.PREMIUM_USER);
                    Logger.printInfo("Congratulations! Your account has been successfully upgraded to Premium. Enjoy your new benefits!");
                    populateUserActions();
                } else {
                    System.out.println("An Error occurred while upgrading your account. Please try again.");
                }
            }

        } while (keepAsking);
    }

    public Optional<UserDetails> viewUserInfo() {
        Optional<UserDetails> userDetails = userRepository.viewUserInfo(loggedInUser);
        if (userDetails.isPresent()) {
            System.out.println("Name: " + userDetails.get().getFullName());
            System.out.println("Date Of Birth: " + userDetails.get().getBirthDate());
            System.out.println("Email Address: " + userDetails.get().getEmail());

        }
        return userDetails;
    }

    public void updateUserInfo() {
        Optional<UserDetails> userDetailsOptional = viewUserInfo();
        if (userDetailsOptional.isPresent()) {
            try {
                UserDetails userDetails = userDetailsOptional.get();
                System.out.println("Would you like to update your profile? (Y/N)");
                String userInput = scanner.nextLine();
                if (userInput.equalsIgnoreCase("Y")) {
                    System.out.println("Please Enter your Full Name or -1 to skip updating your name: ");
                    String fullName = scanner.nextLine();
                    if (!fullName.equals("-1")) {
                        userDetails.setFullName(fullName);
                    }

                    System.out.println("Please Enter your date of birth or -1 to skip updating your birthdate: ");
                    String dateOfBirth = scanner.nextLine();
                    if (!dateOfBirth.equals("-1")) {
                        LocalDate birthDate = userRepository.validateDateOfBirth(dateOfBirth);
                        userDetails.setBirthDate(birthDate);
                    }

                    System.out.println("Please Enter your email address or -1 to skip updating your email: ");
                    String email = scanner.nextLine();
                    if (!email.equals("-1")) {
                        userRepository.validateEmail(email);
                        userDetails.setEmail(email);
                    }

                    if (userRepository.updateUser(userDetails)) {
                        System.out.println("Your account has been successfully updated!");
                    } else {
                        System.out.println("An Error occurred while updating your account. Please try again.");
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void populateUserActions() {
        actions.clear();
        actions = getActions();
    }

    public void logOut() {
        loggedInUser = null;
    }
}
