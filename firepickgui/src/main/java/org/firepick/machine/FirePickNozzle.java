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

import org.openpnp.machine.reference.ReferenceNozzle;
import org.openpnp.model.Location;
import org.openpnp.spi.Head;
import org.openpnp.spi.NozzleTip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class FirePickNozzle extends ReferenceNozzle {
	Logger logger = LoggerFactory.getLogger(FirePickNozzle.class);

	@Override
	public void setPickDwellMilliseconds(int pickDwellMilliseconds) {
		logger.debug("{}.setPickDwellMilliseconds({})", getId(), pickDwellMilliseconds);
		super.setPickDwellMilliseconds(pickDwellMilliseconds);
	}

	@Override
	public void setPlaceDwellMilliseconds(int placeDwellMilliseconds) {
		logger.debug("{}.setPlaceDwellMilliseconds({})", getId(), placeDwellMilliseconds);
		super.setPlaceDwellMilliseconds(placeDwellMilliseconds);
	}

	@Override
	public NozzleTip getNozzleTip() {
		NozzleTip result = super.getNozzleTip();
		logger.debug("{}.getNozzleTip => {}", getId(), result);
		return result;
	}

	@Override
	public Location getLocation() {
		Location result = super.getLocation();
		logger.trace("{}.getLocation => {}", getId(), result);
		return result;
	}

	@Override
	public void setHead(Head head) {
		logger.debug("{}.setHead({})", getId(), head);
		super.setHead(head);
	}
}
