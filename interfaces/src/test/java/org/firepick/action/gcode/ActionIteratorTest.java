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

import junit.framework.Assert;
import org.firepick.action.Action;
import org.firepick.action.ActionPlan;
import org.firepick.action.ConcurrentActionIterator;
import org.firepick.action.Positioner;
import org.junit.Test;

import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class ActionIteratorTest {
    Positioner xAxis = new Positioner("X", "tinyg");
    Positioner yAxis = new Positioner("Y", "tinyg");
    Positioner zAxis = new Positioner("Z", "tinyg");
    Positioner rotateAxis = new Positioner("A", "EasyDriver");

    @Test
    public void testConcurrentActionIterator() {
        ActionPlan plan0 = new ActionPlan();
        ConcurrentActionIterator iterator0 = new ConcurrentActionIterator(plan0);
        assertFalse(iterator0.hasNext());
        assertEquals(0, plan0.size());
        assertFalse(plan0.iterator() instanceof ListIterator);

        ActionPlan plan1 = new ActionPlan();
        plan1.add(new Action("S1").addPosition(xAxis, 1));
        assertEquals(1, plan1.size());
        ConcurrentActionIterator iterator1 = new ConcurrentActionIterator(plan1);
        assert (iterator1.hasNext());
        Action action1_0 = iterator1.next();
        assertFalse(iterator1.hasNext());
        assertEquals("S1", action1_0.getName());

        ActionPlan plan2 = new ActionPlan();
        plan2.add(new Action("S1").addPosition(xAxis, 1));
        plan2.add(new Action("S2").addPosition(yAxis, 2));
        assertEquals(2, plan2.size());
        ConcurrentActionIterator iterator2 = new ConcurrentActionIterator(plan2);
        assert (iterator2.hasNext());
        Action action2_0 = iterator2.next();
        assertEquals("S1; S2", action2_0.getName());
        assertFalse(iterator2.hasNext());

        ActionPlan plan3 = new ActionPlan();
        plan3.add(new Action("S1").addPosition(xAxis, 1));
        plan3.add(new Action("S2").addPosition(yAxis, 2));
        plan3.add(new Action("S3").addPosition(xAxis, 3));
        assertEquals(3, plan3.size());
        ConcurrentActionIterator iterator3 = new ConcurrentActionIterator(plan3);
        assert (iterator3.hasNext());
        Action action3_0 = iterator3.next();
        assertEquals("S1; S2", action3_0.getName());
        assert (iterator3.hasNext());
        Action action3_1 = iterator3.next();
        assertEquals("S3", action3_1.getName());
        assertFalse(iterator3.hasNext());
    }

    @Test
    public void testNonConcurrentActionIterator() {
        ActionPlan plan4 = new ActionPlan();
        plan4.add(new Action("S1").addPosition(xAxis, 1));
        plan4.add(new Action("S2").addPosition(yAxis, 2));
        plan4.add(new Action("S3").addPosition(xAxis, 3));
        plan4.add(new Action("S4").addPosition(yAxis, 4));
        assertEquals(4, plan4.size());
        ConcurrentActionIterator iterato44 = new ConcurrentActionIterator(plan4);
        assert (iterato44.hasNext());
        Action action3_0 = iterato44.next();
        assertEquals("S1; S2", action3_0.getName());
        assert (iterato44.hasNext());

        iterato44.setConcurrent(false);
        assert (iterato44.hasNext());
        Action action3_1 = iterato44.next();
        assertEquals("S3", action3_1.getName());
        assert (iterato44.hasNext());
        Action action3_2 = iterato44.next();
        assertEquals("S4", action3_2.getName());
        assertFalse(iterato44.hasNext());
    }

    @Test
    public void testConcurrentActionListIterator() {
        ActionPlan plan4 = new ActionPlan();
        plan4.add(new Action("S1").addPosition(xAxis, 1));
        plan4.add(new Action("S2").addPosition(yAxis, 2));
        plan4.add(new Action("S3").addPosition(xAxis, 3));
        plan4.add(new Action("S4").addPosition(yAxis, 4));
        assertEquals(4, plan4.size());
        ConcurrentActionIterator iterator4 = new ConcurrentActionIterator(plan4);
        testListIterator(iterator4);

        ConcurrentActionIterator iterator = (ConcurrentActionIterator) iterator4;
        iterator.setConcurrent(false);
        assert (iterator4.hasNext());
        Action action3_1 = iterator4.next();
        assertEquals("S3", action3_1.getName());
        assert (iterator4.hasNext());
        Action action3_2 = iterator4.next();
        assertEquals("S4", action3_2.getName());
        assertEquals(2, iterator4.previousIndex());
        assertEquals(3, iterator4.nextIndex());
        assert (iterator4.hasPrevious());
        assertFalse(iterator4.hasNext());
    }

    @Test
    public void testActionPlanIteratorConstructor() {
        ActionPlan plan4 = new ActionPlan();
        plan4.add(new Action("S1").addPosition(xAxis, 1));
        plan4.add(new Action("S2").addPosition(yAxis, 2));
        plan4.add(new Action("S3").addPosition(xAxis, 3));
        plan4.add(new Action("S4").addPosition(yAxis, 4));
        ActionPlan plan4Concurrent = new ActionPlan(new ConcurrentActionIterator(plan4));
        assertEquals(2, plan4Concurrent.size());
        ListIterator<Action> iterator4 = plan4Concurrent.listIterator();
        testListIterator(iterator4);

        assert (iterator4.hasNext());
        Action action3_1 = iterator4.next();
        assertEquals("S3; S4", action3_1.getName());
        assert (iterator4.hasPrevious());
        assertFalse(iterator4.hasNext());
        assertEquals(1, iterator4.previousIndex());
        assertEquals(2, iterator4.nextIndex());
    }

    private void testListIterator(ListIterator<Action> iterator4) {
        assertEquals(-1, iterator4.previousIndex());
        assertEquals(0, iterator4.nextIndex());
        assertFalse(iterator4.hasPrevious());
        assert (iterator4.hasNext());
        Exception caughtException = null;
        try {
            iterator4.previous();
        } catch (Exception e) {
            caughtException = e;
        }
        assert (caughtException instanceof NoSuchElementException);

        Action action3_0 = iterator4.next();
        assertEquals(0, iterator4.previousIndex());
        assertEquals(1, iterator4.nextIndex());
        assertEquals("S1; S2", action3_0.getName());
        assert (iterator4.hasPrevious());
        assert (iterator4.hasNext());
        assertEquals(action3_0, iterator4.previous());
        assertEquals(-1, iterator4.previousIndex());
        assertEquals(0, iterator4.nextIndex());
        assertFalse(iterator4.hasPrevious());
        assert (iterator4.hasNext());
        assertEquals(action3_0, iterator4.next());
    }

    @Test
    public void testGroups() {
        Action action = new Action("test")
                .addPosition(xAxis, 1)
                .addPosition(rotateAxis, 180);
        Set<String> groups = action.getGroups();
        Assert.assertEquals(2, groups.size());
        assert(groups.contains("tinyg"));
        assert(groups.contains("EasyDriver"));
    }

}
