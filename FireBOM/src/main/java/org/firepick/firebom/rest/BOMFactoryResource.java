package org.firepick.firebom.rest;

import org.firepick.firebom.BOM;
import org.firepick.firebom.BOMFactory;
import org.firepick.firebom.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import java.io.*;
import java.net.URL;

@Path("/build")
public class BOMFactoryResource {
    private static Logger logger = LoggerFactory.getLogger(BOMFactoryResource.class);

    @Context
    private javax.servlet.http.HttpServletRequest request;

    @GET
    @Produces("text/html; charset=UTF-8")
    public String createBOM(@QueryParam("url") String url) throws IOException, InterruptedException {
        BOMFactory bomFactory = new BOMFactory();
        BOM bom = bomFactory.createBOM(new URL(url));

        ByteArrayOutputStream bosHtml = new ByteArrayOutputStream();
        PrintStream psHtml = new PrintStream(bosHtml);
        InputStream is;
        logger.info("{} {}", request.getContextPath(), request.getServletPath());
        if (request.getContextPath().contains("/firebom")) {
            is = Main.class.getResourceAsStream("/index.html");
        } else {
            is = Main.class.getResourceAsStream("/app-engine/index.html");
        }
        InputStreamReader isr = new InputStreamReader(is);
        bomFactory.setOutputType(BOMFactory.OutputType.HTML_TABLE);
        BufferedReader br = new BufferedReader(isr);
        while (br.ready()) {
            String line = br.readLine();
            if (line.contains("<!--BOM-->")) {
                boolean isResolved = bom.isResolved();
                bomFactory.printBOM(psHtml, bom);
                psHtml.println("<script>$('#url').val('" + url + "')</script>");
                if (isResolved) {
                    psHtml.print("<div class='firebom_copylink'>");
                    psHtml.print("<button type='button' onclick='copyLink()'>");
                    psHtml.print("Link this <img style='position:relative;top:2px;' src='http://upload.wikimedia.org/wikipedia/commons/4/45/FireBOM.JPG' height=12px/>");
                    psHtml.print("</button>");
                    psHtml.print("</div>");
                    psHtml.println();
                } else {
                    psHtml.println("<script>setTimeout(function() {location.reload();}, 1000)</script>");
                }
            } else {
                psHtml.println(line);
            }
        }

        return bosHtml.toString();
    }
}