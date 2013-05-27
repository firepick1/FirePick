package org.firepick.action;
/*
    Copyright (C) 2013 Karl Lew <karl@firepick.org>. All rights reserved.
    DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
    
    This file is part of FirePick Software.
    
    FirePick Software is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FirePick Software is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FirePick Software.  If not, see <http://www.gnu.org/licenses/>.
    
    For more information about FirePick Software visit http://firepick.org
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ActionPlan implements Iterable<Action> {
    private List<Action> actions = new ArrayList<Action>();

    public ActionPlan(){}

    public ActionPlan(Iterator<Action> sourceActions) {
        while (sourceActions.hasNext()) {
            actions.add(sourceActions.next());
        }
    }

    public ActionPlan add(Action action) {
        actions.add(action);
        return this;
    }

    public int size() {
        return actions.size();
    }

    @Override
    public Iterator<Action> iterator() {
        return actions.iterator();
    }

    public ListIterator<Action> listIterator() {
        return actions.listIterator();
    }
}
