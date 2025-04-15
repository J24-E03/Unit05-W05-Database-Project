package org.dci;


import org.dci.menu.MainMenu;
import org.dci.repository.HistoryRepository;
import org.dci.repository.UserRepository;
import org.dci.service.MovieRecommendationService;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class ConsoleApplication {
    private  UserRepository userRepository;
    private final MovieRecommendationService movieService = MovieRecommendationService.getInstance();
    public static final Scanner scanner = new Scanner(System.in);


    public void run() {
        userRepository = UserRepository.getInstance();
        movieService.populateGenres();
        MainMenu mainMenu = new MainMenu(userRepository);
        mainMenu.show();
        exitSystem();
    }


    public void exitSystem() {
        System.out.println("Goodbye!");
    }
}


