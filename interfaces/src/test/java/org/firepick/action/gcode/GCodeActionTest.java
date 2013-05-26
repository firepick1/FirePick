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
import org.firepick.action.Positioner;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GCodeActionTest {
    Positioner xAxis;
    Positioner yAxis;
    Positioner zAxis;
    Positioner aAxis;
    Positioner bAxis;
    Positioner cAxis;

    @Before
    public void setup() throws Exception {
        xAxis = new Positioner("X");
        yAxis = new Positioner("Y");
        zAxis = new Positioner("Z");
        aAxis = new Positioner("A");
        bAxis = new Positioner("B");
        cAxis = new Positioner("C");
    }

    @Test
    public void testGCodeRapicMove() throws Exception {
        GCodeActionFactory factory = new GCodeActionFactory();

        IAction action = factory.createMove(true)
                .withPosition(zAxis, 3.3)
                .withPosition(yAxis, 2.2)
                .withPosition(xAxis, 1.1);
        assertEquals("G0 X1.1Y2.2Z3.3", action.toString());
    }

    @Test
    public void testGCodeActionFactory() throws Exception {
        GCodeActionFactory factory = new GCodeActionFactory();
        IAction a1 = factory.createMove(false).withPosition(xAxis, 1.1).withPosition(zAxis, 3.3).withDurationMillis(100);
        IAction a2 = factory.createMove(false).withPosition(yAxis, 2.2).withDurationMillis(200);
        IAction a3 = factory.createMove(false).withPosition(xAxis, 9.9).withDurationMillis(300);

        IAction a1a2 = factory.compose(a1, a2);
        assertEquals("G1 X1.1Y2.2Z3.3", a1a2.toString());
        assertEquals(200, a1a2.getDurationMillis());
        IAction a1a3 = factory.compose(a1, a3);
        assertNull(a1a3);
    }

}
