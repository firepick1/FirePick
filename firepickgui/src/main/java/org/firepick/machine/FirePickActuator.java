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

import org.openpnp.machine.reference.ReferenceActuator;
import org.openpnp.model.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirePickActuator extends ReferenceActuator {
	static Logger logger = LoggerFactory.getLogger(FirePickActuator.class);

	@Override
	public void setHeadOffsets(Location headOffsets) {
		logger.debug("{}.setHeadOffsets({})", new Object[]{getId(), headOffsets});
		super.setHeadOffsets(headOffsets);
	}

	@Override
	public Location getLocation() {
		Location result = super.getLocation();
		logger.trace("{}.getLocation => {}", result);
		return result;
	}
}
