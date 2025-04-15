package org.dci;

import org.dci.utils.HikariCPConfig;

public class Main {
    public static void main(String[] args) {
        HikariCPConfig.initialize("ai_based_movie_recommendation_system");
        ConsoleApplication app = new ConsoleApplication();
        app.run();
    }
}