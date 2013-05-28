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
import org.openpnp.spi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SuppressWarnings("unused")
public class FirePickMachine extends ReferenceMachine {
    static private Logger logger = LoggerFactory.getLogger(FirePickMachine.class);
    static private FirePickMachine instance;

    public FirePickMachine() {
        logger.info("isDebugEnabled:{}", logger.isDebugEnabled());
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

    @Override
    public List<Head> getHeads() {
        logger.trace("getHeads");
        return super.getHeads();
    }

    @Override
    public Head getHead(String s) {
        logger.info("getHead({})", s);
        return super.getHead(s);
    }

    @Override
    public List<Feeder> getFeeders() {
        logger.trace("getFeeders");
        return super.getFeeders();
    }

    @Override
    public Feeder getFeeder(String s) {
        logger.info("getFeeder({})", s);
        return super.getFeeder(s);
    }

    @Override
    public List<Camera> getCameras() {
        logger.trace("getCameras");
        return super.getCameras();
    }

    @Override
    public Camera getCamera(String s) {
        logger.info("getCamera({})", s);
        return super.getCamera(s);
    }

    @Override
    public void home() throws Exception {
        logger.info("home");
        super.home();
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    public void setEnabled(boolean b) throws Exception {
        logger.info("setEnabled({})", b);
        super.setEnabled(b);
    }

    @Override
    public void addListener(MachineListener machineListener) {
        logger.info("addListener({})", machineListener);
        super.addListener(machineListener);
    }

    @Override
    public void removeListener(MachineListener machineListener) {
        logger.info("removeListener({})", machineListener);
        super.removeListener(machineListener);
    }

    @Override
    public List<Class<? extends Feeder>> getCompatibleFeederClasses() {
        logger.info("getCompatibleFeederClasses");
        return super.getCompatibleFeederClasses();
    }

    @Override
    public List<Class<? extends Camera>> getCompatibleCameraClasses() {
        logger.info("getCompatibleCameraClasses");
        return super.getCompatibleCameraClasses();
    }

    @Override
    public void addFeeder(Feeder feeder) throws Exception {
        logger.info("addFeeder({})", feeder);
        super.addFeeder(feeder);
    }

    @Override
    public void removeFeeder(Feeder feeder) {
        logger.info("removeFeeder({})", feeder);
        super.removeFeeder(feeder);
    }

    @Override
    public void addCamera(Camera camera) throws Exception {
        logger.info("addCamera({})", camera);
        super.addCamera(camera);
    }

    @Override
    public void removeCamera(Camera camera) {
        logger.info("removeCamera({})", camera);
        super.removeCamera(camera);
    }

    @Override
    public JobPlanner getJobPlanner() {
        logger.info("getJobPlanner");
        return super.getJobPlanner();
    }

    @Override
    public Wizard getConfigurationWizard() {
        logger.info("getConigurationWizard");
        return super.getConfigurationWizard();
    }
}
