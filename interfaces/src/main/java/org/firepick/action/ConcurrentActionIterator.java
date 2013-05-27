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

import java.util.*;

public class ConcurrentActionIterator implements ListIterator<Action> {
    private Iterator<Action> sourceIterator;
    private List<Action> previousActions = new ArrayList<Action>();
    private int position;
    private Action nextAction;
    private boolean isConcurrent = true;

    public ConcurrentActionIterator(Iterable<Action> actions) {
        sourceIterator = actions.iterator();
    }

    @Override
    public boolean hasNext() {
        if (nextAction == null && sourceIterator.hasNext()) {
            nextAction = sourceIterator.next();
        }
        return position < previousActions.size() || nextAction != null;
    }

    @Override
    public Action next() {
        Action result = nextAction;
        if (position < previousActions.size()) {
            result = previousActions.get(position);
            position++;
            return result;
        }

        Action peekAction = null;
        for (; ; ) {
            peekAction = sourceIterator.hasNext() ? sourceIterator.next() : null;
            if (peekAction == null) {
                break;
            }
            Action concurrentAction = isConcurrent ? result.withAction(peekAction) : null;
            if (concurrentAction == null) {
                break;
            }
            result = concurrentAction;
        }
        previousActions.add(result);
        position++;
        nextAction = peekAction;
        return result;
    }

    @Override
    public boolean hasPrevious() {
        return position > 0;
    }

    @Override
    public Action previous() {
        if (position <= 0) {
            throw new NoSuchElementException("Attempt to move before end of list");
        }
        position--;
        Action result = previousActions.get(position);
        return result;
    }

    @Override
    public int nextIndex() {
        return position;
    }

    @Override
    public int previousIndex() {
        return position-1;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Action action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(Action action) {
        throw new UnsupportedOperationException();
    }

    public boolean isConcurrent() {
        return isConcurrent;
    }

    public void setConcurrent(boolean concurrent) {
        isConcurrent = concurrent;
    }
}
