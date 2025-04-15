package org.dci.menu;

import org.dci.domain.User;
import org.dci.repository.QueryRepository;

import java.sql.SQLException;
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
    private void chooseTimeRange() {}
}
