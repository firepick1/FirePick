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

import org.firepick.IActor;
import org.firepick.IActorState;

import java.util.*;

public class Action {
    protected Collection<IActor> actors = new ArrayList<IActor>();
    private SortedSet<IActorState> actorStates = new TreeSet<IActorState>();
    private long durationMillis;
    private String name;

    public Action(String name) {
        this.name = name;
    }

    public Action addPosition(Positioner motor, double position) {
        return addActorState(new Position(motor, position));
    }

    public Action addActorState(IActorState actorState) {
        addActor(actorState.getActor());
        actorStates.add(actorState);
        return this;
    }

    public SortedSet<IActorState> getActorStates() {
        return Collections.unmodifiableSortedSet(actorStates);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        for (IActorState position : getActorStates()) {
            sb.append(position);
        }
        return sb.toString();
    }

    public Collection<IActor> getActors() {
        return Collections.unmodifiableCollection(actors);
    }

    public Set<String> getGroups() {
        HashSet<String> result = new HashSet<String>();
        for (IActor actor: actors) {
            result.add(actor.getGroup());
        }
        return result;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public Action setDurationMillis(long durationMillis) {
        this.durationMillis = durationMillis;
        return this;
    }

    protected void addActor(IActor value) {
        actors.add(value);
    }

    public String getName() {
        return name;
    }

    public Action withAction(Action that) {
        Action result = new Action(this.getName() + "; " + that.getName());
        for (IActorState actorState : this.getActorStates()) {
            result = result.addActorState(actorState);
        }
        for (IActorState actorState : that.getActorStates()) {
            if (this.getActorStates().contains(actorState)) {
                continue;
            }
            if (this.getActors().contains(actorState.getActor())) {
                return null;
            }
            result = result.addActorState(actorState);
        }
        result.setDurationMillis(Math.max(this.getDurationMillis(), that.getDurationMillis()));
        return result;
    }
}
