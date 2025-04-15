package org.dci;


import org.dci.menu.MainMenu;
import org.dci.repository.HistoryRepository;
import org.dci.repository.UserRepository;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class ConsoleApplication {
    private  UserRepository userRepository;
    private HistoryRepository historyRepository;
    public static final Scanner scanner = new Scanner(System.in);


    public void run() {
        userRepository = UserRepository.getInstance();
        MainMenu mainMenu = new MainMenu(userRepository);
        mainMenu.show();;
        exitSystem();
    }


    public void exitSystem() {
        System.out.println("Goodbye!");
    }
}


