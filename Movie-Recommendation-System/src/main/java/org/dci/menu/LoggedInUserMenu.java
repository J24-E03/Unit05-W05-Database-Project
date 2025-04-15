package org.dci.menu;

import org.dci.client.MovieDetails;
import org.dci.domain.Movie;
import org.dci.domain.User;
import org.dci.domain.UserType;

import org.dci.repository.MovieRepository;
import org.dci.service.MovieRecommendationService;
import org.dci.utils.Colors;
import org.dci.utils.Logger;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class LoggedInUserMenu extends Menu {

    private User loggedInUser;
    private final MovieRecommendationService movieService = MovieRecommendationService.getInstance();
    private final MovieRepository movieRepository = MovieRepository.getInstance();


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
            moviesDetails.forEach(movieDetails -> {
                Optional<Movie> movie = movieRepository.addNewMovie(movieDetails);
                if (movie.isPresent()) {
                    movies.add(movie.get());
                }
            });


            movies.forEach(movie -> Logger.printResult(movie.makeMovieDetailsReadyForPrint()));

        } catch (InterruptedException e) {
            Logger.error("An error occurred: " + e.getMessage());
        }
    }

    private void viewHistory() {
        Logger.printResultTitle("These are your chat histories:");
        //historyRepository.getUserHistory(loggedInUser.getId()).forEach(history -> Logger.printResultTitle(history.prepareHistoryTobeShown()));
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
                loggedInUser.setType(UserType.PREMIUM_USER);
                Logger.printInfo("Congratulations! Your account has been successfully upgraded to Premium. Enjoy your new benefits!");
                populateUserActions();
            }

        } while (keepAsking);

    }

    private void populateUserActions() {
        actions.clear();
        actions = getActions();
    }

    public void logOut() {
        loggedInUser = null;
    }
}
