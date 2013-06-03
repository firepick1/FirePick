package org.firepick.machine;
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

import org.openpnp.gui.support.Wizard;
import org.openpnp.machine.reference.ReferenceMachine;
import org.openpnp.model.Part;
import org.openpnp.spi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FirePickMachine extends ReferenceMachine {
    static private Logger logger = LoggerFactory.getLogger(FirePickMachine.class);
    static private FirePickMachine instance;

    public FirePickMachine() {
        if (instance == null) {
            instance = this;
        }
    }

    public static FirePickMachine getInstance() {
        if (instance == null) {
            instance = new FirePickMachine();
        }
        return instance;
    }

	public boolean hasAlternateNozzle(Nozzle nozzle1, Part part) {
		for (Head head: heads) {
			if (head != nozzle1.getHead()) {
				for (Nozzle nozzle2: head.getNozzles()) {
					if (nozzle2.getNozzleTip().canHandle(part)) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
