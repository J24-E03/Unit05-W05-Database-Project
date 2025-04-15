package org.dci.utils;

public class Logger {
    public static void error(String message) {
        System.out.println(Colors.colorize(message, Colors.ANSI_BOLD_RED));
    }

    public static void printMenuTitle(String message) {
        System.out.println(Colors.colorize(message, Colors.ANSI_BOLD_GREEN));
    }

    public static void printNormal(String message) {
        System.out.println(message);
    }

    public static void printInfo(String message) {
        System.out.println(Colors.colorize(message, Colors.ANSI_BOLD_PURPLE));
    }

    public static void printHint(String message) {
        System.out.println(Colors.colorize(message, Colors.ANSI_BOLD_CYAN));
    }

    public static void printNavigation(String message) {
        System.out.println(Colors.colorize(message, Colors.ANSI_BOLD_BLUE));
    }

    public static void printResultTitle(String message) {
        System.out.println(Colors.colorize(message, Colors.ANSI_BOLD_CYAN));
    }

    public static void printResult(String message) {
        System.out.println(Colors.colorize(message, Colors.ANSI_CYAN));
    }


    public static String resetColoredMessage(String message) {
        return Colors.ANSI_RESET + message;
    }
}

