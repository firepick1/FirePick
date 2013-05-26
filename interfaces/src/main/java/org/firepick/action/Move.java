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

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public class Move extends ActionBase {
    private SortedSet<Position> positions = new TreeSet<Position>();
    private boolean isRapid;

    public Move(String name) {
        super(name);
    }

    public Move withPosition(Positioner motor, double position) {
        return withPosition(new Position(motor, position));
    }

    public Move withPosition(Position position) {
        addActor(position.getPositioner());
        positions.add(position);
        return this;
    }

    public Move withDurationMillis(long value) {
        setDurationMillis(value);
        return this;
    }

    public Move withRapid(boolean value) {
        this.isRapid = value;
        return this;
    }

    public SortedSet<Position> getPositions() {
        return Collections.unmodifiableSortedSet(positions);
    }

    public boolean isRapid() {
        return isRapid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        for (Position position : getPositions()) {
            sb.append(position);
        }
        return sb.toString();
    }
}
