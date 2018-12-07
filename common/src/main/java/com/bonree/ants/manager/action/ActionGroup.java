package com.bonree.ants.manager.action;

import java.util.HashMap;
import java.util.Map;

public class ActionGroup<T> {

    protected Map<String, AntsAction<T>> actions;

    public ActionGroup() {
        this.actions = new HashMap<>();
    }

    public AntsAction<T> putAction(String name, AntsAction<T> action) {
        return actions.put(name, action);
    }

    public AntsAction<T> removeAction(String name) {
        return actions.remove(name);
    }

    public AntsAction<T> getAction(String name) {
        return actions.get(name);
    }
}
