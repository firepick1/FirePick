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


import org.firepick.action.Action;
import org.firepick.action.Positioner;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GCodeActionTest {

    @Before
    public void setup() throws Exception {
    }

    @Test
    public void testGCodeRapicMove() throws Exception {
        Positioner xAxis = new Positioner("X", "tinyg");
        Positioner yAxis = new Positioner("Y", "tinyg");
        Positioner zAxis = new Positioner("Z", "tinyg");
        GCodeFactory factory = new GCodeFactory();

        Action action = new Action("Move gantry")
                .addPosition(zAxis, 3.3)
                .addPosition(yAxis, 2.2)
                .addPosition(xAxis, 1.1);
        assertEquals("G0 X1.1Y2.2Z3.3", factory.rapidMove(action, "tinyg"));
    }

    @Test
    public void testGCodeActionFactory() throws Exception {
        Positioner feeder1 = new Positioner("X", "tinyg");
        Positioner gantry1 = new Positioner("Y", "tinyg");
        Positioner gantry2 = new Positioner("Z", "tinyg");
        GCodeFactory factory = new GCodeFactory();
        Action a1 = new Action("Move gantry1 to feeder1").addPosition(feeder1, 1).addPosition(gantry1, 2).setDurationMillis(100);
        Action a2 = new Action("Move gantry2 to feeder1").addPosition(feeder1, 1).addPosition(gantry2, 3).setDurationMillis(200);
        Action a3 = new Action("Move feeder1").addPosition(feeder1, 9).setDurationMillis(300);

        // Actions a1 and a2 can be combined into a single action
        Action a1a2 = a1.withAction(a2);
        assertEquals(3, a1a2.getActors().size());
        assert(a1a2.getActors().contains(feeder1));
        assert(a1a2.getActors().contains(gantry1));
        assert(a1a2.getActors().contains(gantry2));
        assertNotNull(a1a2);
        assertEquals("G0 X1.0Y2.0Z3.0", factory.rapidMove(a1a2, "tinyg"));
        assertEquals(200, a1a2.getDurationMillis());

        // Actions a1 and a3 cannot be combined because they use feeder1 in different ways
        Action a1a3 = a1.withAction(a3);
        assertNull(a1a3);
    }

}
