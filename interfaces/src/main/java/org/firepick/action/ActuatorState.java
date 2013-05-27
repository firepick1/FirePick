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

public class ActuatorState implements IActorState {
    private Actuator actuator;
    private String state;

    public ActuatorState(Actuator actuator, String state) {
        this.state = state;
        this.actuator = actuator;
    }

    public String getState() {
        return state;
    }

    public Actuator getActuator() {
        return actuator;
    }

    public String toString() {
        return actuator.getName() + ":" + state;
    }

    @Override
    public int compareTo(IActorState that) {
        if (that instanceof ActuatorState) {
            ActuatorState thatState = (ActuatorState) that;
            int cmp = getActuator().compareTo(thatState.getActuator());
            if (cmp == 0) {
                cmp = getState().compareTo(thatState.getState());
            }
            return cmp;
        }
        return getClass().toString().compareTo(that.getClass().toString());
    }

    @Override
    public IActor getActor() {
        return actuator;
    }

    @Override
    public Object getValue() {
        return state;
    }
}
