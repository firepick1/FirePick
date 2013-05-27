package org.firepick.action.gcode;
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
import org.firepick.action.Action;
import org.firepick.action.Positioner;

public class GCodeFactory {
    /**
     * Generate the GCode string for a rapid movement G0 operation involving the
     * @param action
     * @param group
     * @return
     */
    public String rapidMove(Action action, String group) {
        StringBuilder sb = new StringBuilder();

        for (IActorState actorState : action.getActorStates()) {
            IActor actor = actorState.getActor();
            if (group.equals(actor.getGroup())) {
                if (actor instanceof Positioner) {
                    sb.append(actorState.toString());
                } else {
                    throw new UnsupportedOperationException("Expected a Positioner: " + actor);
                }
            }
        }
        if (sb.length() == 0) {
            return null;
        }
        sb.insert(0, "G0 ");
        return sb.toString();
    }

}
