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

import org.firepick.IAction;
import org.firepick.IActor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ActionBase implements IAction {
    protected Collection<IActor> actors = new ArrayList<IActor>();
    private long durationMillis;
    private String name;

    protected ActionBase(String name) {
        this.name = name;
    }

    @Override
    public void execute() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<IActor> getActors() {
        return Collections.unmodifiableCollection(actors);
    }

    @Override
    public long getDurationMillis() {
        return durationMillis;
    }

    public void setDurationMillis(long durationMillis) {
        this.durationMillis = durationMillis;
    }

    protected void addActor(IActor value) {
        actors.add(value);
    }

    public String getName() {
        return name;
    }
}
