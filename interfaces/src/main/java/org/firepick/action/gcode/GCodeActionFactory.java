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

import org.firepick.IAction;
import org.firepick.action.IActionFactory;
import org.firepick.action.Move;
import org.firepick.action.Position;

public class GCodeActionFactory implements IActionFactory {

    public Move createMove(boolean isRapid) {
        return isRapid ?
                new Move("G0 ").withRapid(isRapid) :
                new Move("G1 ").withRapid(isRapid);
    }

    @Override
    public IAction compose(IAction a1, IAction a2) {
        if (a1.getName().equals(a2.getName())) {
            if (a1 instanceof Move && a2 instanceof Move) {
                return composeMove((Move) a1, (Move) a2);
            }
        }

        return null;
    }

    private IAction composeMove(Move m1, Move m2) {
        if (m1.isRapid() != m2.isRapid()) {
            return null;
        }

        Move result = new Move(m1.getName());
        for (Position position : m1.getPositions()) {
            result = result.withPosition(position);
        }
        for (Position position : m2.getPositions()) {
            if (m1.getActors().contains(position.getPositioner())) {
                return null;
            }
            result = result.withPosition(position);
        }
        result.setDurationMillis(Math.max(m1.getDurationMillis(), m2.getDurationMillis()));
        return result;
    }
}
