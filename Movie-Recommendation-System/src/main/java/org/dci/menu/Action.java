package org.dci.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Action {
    private String title;
    private Runnable action;

    public void invoke() {
        action.run();
    }
}
