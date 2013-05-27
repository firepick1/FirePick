package org.firepick;
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

/**
 * A resource that can only participate in one Action at a time
 */
public interface IActor extends Comparable<IActor>{
    /**
     * The name of an actor should correspond to its GCode name where applicable
     * For example, a Positioner for the x-axis should have a name, "X".
     * @return the action name
     */
    String getName();

    /**
     * The group of an actor is a name shared by all actors in the group.
     * The group corresponds to the driver (e.g., "TinyG")
     * @return
     */
    String getGroup();
}
