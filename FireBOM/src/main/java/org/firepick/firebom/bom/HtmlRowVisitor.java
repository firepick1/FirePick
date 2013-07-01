package org.firepick.firebom.bom;

import org.firepick.relation.IRow;
import org.firepick.relation.IRowVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlRowVisitor implements IRowVisitor {
    private static Logger logger = LoggerFactory.getLogger(HtmlRowVisitor.class);

    private boolean isResolved = true;
    private boolean lastResolved;

    public boolean isResolved() {
        return isResolved;
    }

    @Override
    public void visit(IRow row) {
        BOMRow bomRow = (BOMRow) row;
        logger.debug("visit BOMRow {}", bomRow.getPart().getUrl());
        lastResolved = bomRow.isResolved();
        isResolved = lastResolved && isResolved;
    }

    public boolean isVisitedRowResolved() {
        return lastResolved;
    }
}