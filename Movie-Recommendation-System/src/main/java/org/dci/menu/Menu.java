package org.dci.menu;

import lombok.Data;
import org.dci.ConsoleApplication;
import org.dci.utils.Logger;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public abstract class Menu {
    private final String title;
    protected List<Action> actions;
    protected static final Scanner scanner = ConsoleApplication.scanner;



    public Menu(String title, List<Action> actions) {
        this.title = title;
        this.actions = actions;
    }

    private boolean isRunning = false;

    protected boolean shouldContinueRunning() {
        return isRunning;
    }

    public void show() {
        isRunning = true;

        do {
            Logger.printMenuTitle(title);
            AtomicInteger index = new AtomicInteger();
            actions.forEach(action -> Logger.printNormal((index.incrementAndGet()
                    + ". " + action.getTitle())));

            Logger.printNavigation("Enter -1 to exit.");

            try {
                int userChoice = scanner.nextInt();
                scanner.nextLine();

                if (userChoice <= 0 || userChoice > actions.size()) {
                    if (userChoice == -1) {
                        isRunning = false;
                    } else {
                        throw new IllegalArgumentException("Please select an option (1-" + actions.size() + "):");
                    }
                } else {
                    actions.get(userChoice - 1).invoke();
                }
            } catch (Exception e) {
                Logger.error(e.getMessage());
            }
        } while (shouldContinueRunning());
    }

    public abstract List<Action> getActions();
}
