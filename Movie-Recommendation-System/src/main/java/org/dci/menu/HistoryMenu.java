package org.dci.menu;

import org.dci.domain.User;
import org.dci.repository.QueryRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HistoryMenu extends Menu {
    private final QueryRepository queryRepository;
    private User loggedUser;
    public HistoryMenu(User loggedUser) {
        super("Viewing History", new ArrayList<>());
        this.loggedUser = loggedUser;
        queryRepository = QueryRepository.getInstance();
        actions.clear();
        actions.addAll(getActions());
    }

    @Override
    public List<Action> getActions() {
        return new ArrayList<>(Arrays.asList(
                new Action("View All", this::viewAllHistory),
                new Action("Choose A Time Range", this::chooseTimeRange)
        ));
    }

    private void viewAllHistory() {
        System.out.println("These are your chat histories:");
        try {
            queryRepository.viewAllUserHistories(loggedUser).forEach(query -> System.out.println(query.prepareHistoryTobeShown()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
    private void chooseTimeRange() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            System.out.println("Enter start date (yyyy-MM-dd):");
            String startInput = scanner.nextLine();
            LocalDate startDate = LocalDate.parse(startInput, formatter);

            System.out.println("Enter end date (yyyy-MM-dd):");
            String endInput = scanner.nextLine();
            LocalDate endDate = LocalDate.parse(endInput, formatter);

            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(23, 59, 59);

            System.out.println("Filtered history from " + start + " to " + end + ":");
            queryRepository.viewUserHistoryInRange(loggedUser, start, end)
                    .forEach(query -> System.out.println(query.prepareHistoryTobeShown()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
