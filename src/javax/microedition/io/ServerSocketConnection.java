/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javax.microedition.io;

import java.io.IOException;

/**
 *
 * None block serversocket
 * 
 * <pre>
 *
 *   void t12() {
 *       new Thread(new Runnable() {
 *
 *           @Override
 *           public void run() {
 *               try {
 *                   ServerSocket srvsock = (ServerSocket) Connector.open("serversocket://:8080");
 *                   System.out.println("server socket listen...");
 *                   srvsock.listen();
 *                   while (true) {
 *                       try {
 *                           Socket cltsock = srvsock.accept();
 *                           cltsock.setOption(Socket.OP_TYPE_NON_BLOCK, Socket.OP_VAL_NON_BLOCK);
 *                           System.out.println("accepted client socket:" + cltsock);
 *                           byte[] buf = new byte[256];
 *                           StringBuffer tmps = new StringBuffer();
 *                           int rlen;
 *                           while ((rlen = cltsock.read(buf, 0, 256)) != -1) {
 *                               String s = new String(buf, 0, rlen);
 *                               tmps.append(s);
 *                               String s1 = tmps.toString();
 *                               if (s1.indexOf("\n\n") >= 0 || s1.indexOf("\r\n\r\n") >= 0) {
 *                                   break;
 *                               }
 *                           }
 *                           //System.out.println("RECV: " + tmps.toString());
 *                           String sbuf = "HTTP/1.0 200 OK\r\nContent-Type: text/html\r\n\r\nFor mini_jvm test. ( EGLS Beijing co.,ltd)" + Calendar.getInstance().getTime().toString();
 *                           int sent = 0;
 *                           while ((sent) < sbuf.length()) {
 *                               int wlen = cltsock.write(sbuf.getBytes(), sent, sbuf.length() - sent);
 *                               if (wlen == -1) {
 *                                   break;
 *                               }
 *                               sent += wlen;
 *                           }
 *                           cltsock.close();
 *                           if (false) {
 *                               break;
 *                           }
 *                       } catch (Exception e) {
 *                           System.out.println(e);
 *                       }
 *                   }
 *                   srvsock.close();
 *               } catch (Exception e) {
 *                   System.out.println(e);
 *               }
 *           }
 *       }).start();
 *
 *   }
 *
 * </pre>
 *
 *
 * @author gust
 */
public interface ServerSocketConnection extends Connection {


    /**
     * 开始接受连接
     *
     * @return
     * @throws IOException
     */
    SocketConnection accept() throws IOException;
}
