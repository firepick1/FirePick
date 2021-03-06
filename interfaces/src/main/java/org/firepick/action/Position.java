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

public class Position implements IActorState {


    private Positioner positioner;
    private double position;

    public Position(Positioner positioner, double position) {
        this.position = position;
        this.positioner = positioner;
    }

    public double getPosition() {
        return position;
    }

    public Positioner getPositioner() {
        return positioner;
    }

    public String toString() {
        return positioner.getName() + position;
    }

    @Override
    public int compareTo(IActorState that) {
        if (that instanceof Position) {
            Position thatPosition = (Position) that;
            int cmp = getPositioner().compareTo(thatPosition.getPositioner());
            if (cmp == 0) {
                cmp = Double.compare(getPosition(), thatPosition.getPosition());
            }
            return cmp;
        }
        return getClass().toString().compareTo(that.getClass().toString());
    }

    @Override
    public IActor getActor() {
        return positioner;
    }

    @Override
    public Object getValue() {
        return position;
    }
}
