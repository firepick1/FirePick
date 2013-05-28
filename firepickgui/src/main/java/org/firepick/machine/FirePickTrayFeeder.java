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
import org.openpnp.machine.reference.feeder.ReferenceTrayFeeder;
import org.openpnp.model.Location;
import org.openpnp.model.Part;
import org.openpnp.spi.Nozzle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class FirePickTrayFeeder extends ReferenceTrayFeeder {
    Logger logger = LoggerFactory.getLogger(FirePickTrayFeeder.class);
	private FirePickMachine firepick = FirePickMachine.getInstance();

    @Override
    public boolean canFeedToNozzle(Nozzle nozzle) {
        logger.info("canFeedToNozzle({})", nozzle);
        return super.canFeedToNozzle(nozzle);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Location getPickLocation() throws Exception {
        Location location = super.getPickLocation();
        logger.info("getPickLocation => {}", location);
        return location;
    }

    @Override
    public void feed(Nozzle nozzle) throws Exception {
        logger.info("feed({})", nozzle);
        super.feed(nozzle);
    }

    @Override
    public Wizard getConfigurationWizard() {
        return super.getConfigurationWizard();
    }

    @Override
    public int getTrayCountX() {
        int result = super.getTrayCountX();
        logger.info("getTracyCountX => {}", result);
        return result;
    }

    @Override
    public void setTrayCountX(int trayCountX) {
        logger.info("setTrayCountX({})", trayCountX);
        super.setTrayCountX(trayCountX);
    }

    @Override
    public int getTrayCountY() {
        int result = super.getTrayCountX();
        logger.info("getTracyCountY => {}", result);
        return result;
    }

    @Override
    public void setTrayCountY(int trayCountY) {
        logger.info("setTrayCountY({})", trayCountY);
        super.setTrayCountY(trayCountY);
    }

    @Override
    public Location getOffsets() {
        return super.getOffsets();
    }

    @Override
    public void setOffsets(Location offsets) {
        logger.info("setOffsets({})", offsets);
        super.setOffsets(offsets);
    }

    @Override
    public int getFeedCount() {
        int result = super.getFeedCount();
        logger.info("getFeedCount => {}", result);
        return result;
    }

    @Override
    public void setFeedCount(int feedCount) {
        logger.info("setFeedCount({})", feedCount);
        super.setFeedCount(feedCount);
    }

    @Override
    public Location getLocation() {
        Location result = super.getLocation();
        logger.info("getLocation => {}", result);
        return result;
    }

    @Override
    public void setLocation(Location location) {
        logger.info("setLocation({})", location);
        super.setLocation(location);
    }

    @Override
    public void setId(String id) {
        super.setId(id);
    }

    @Override
    public String getId() {
        return super.getId();
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        logger.info("setEnabled({})", enabled);
        super.setEnabled(enabled);
    }

    @Override
    public void setPart(Part part) {
        logger.info("setPart({})", part);
        super.setPart(part);
    }

    @Override
    public Part getPart() {
        return super.getPart();
    }
}
