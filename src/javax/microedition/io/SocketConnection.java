/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javax.microedition.io;

import java.io.IOException;

/**
 * block and non block socket, setoption() to set it attribute  <code>
 *    void t13() {
 *        try {
 *            Socket conn = (Socket) Connector.open("socket://baidu.com:80");
 *            conn.setOption(Socket.OP_TYPE_NON_BLOCK, Socket.OP_VAL_NON_BLOCK);
 *            String request = "GET / HTTP/1.1\r\n\r\n";
 *            conn.write(request.getBytes(), 0, request.length());
 *            byte[] rcvbuf = new byte[256];
 *            int len = 0;
 *            while (len != -1) {
 *                len = conn.read(rcvbuf, 0, 256);
 *                for (int i = 0; i < len; i++) {
 *                    System.out.print((char) rcvbuf[i]);
 *                }
 *                System.out.print("\n");
 *            };
 *        } catch (Exception e) {
 *
 *        }
 *    }
 * </code>
 *
 * @author gust
 */
public interface SocketConnection extends StreamConnection {
    public static byte	DELAY = 0;
    //Socket option for the small buffer writing delay (0).
    public static byte	KEEPALIVE = 1;
    //Socket option for the keep alive feature (2).
    public static byte	LINGER = 2;
    //Socket option for the linger time to wait in seconds before closing a connection with pending data output (1).
    public static byte	RCVBUF = 3;
    //Socket option for the size of the receiving buffer (3).
    public static byte	SNDBUF = 4;
    //Socket option for the size of the sending buffer (4).
    public static byte	TIMEOUT = 5;
    //Socket option for a timeout on a blocking read or write operation.
    public static byte	NONBLOCK = 6;
    /**
     * 非阻塞写，返回写长度
     *
     * @param b
     * @param off
     * @param len
     * @return
     * @throws IOException
     */
    public int write(byte b[], int off, int len)
            throws IOException;
    
    
    /**
     * 非阻塞读，返回读长度
     *
     * @param b
     * @param off
     * @param len
     * @return
     * @throws IOException
     */
    int read(byte b[], int off, int len)
            throws IOException;

    /**
     * 设置阻塞或非阻塞属性
     *
     * @param option
     * @param value
     */
    void setSocketOption(byte option, int value);


    /**
     * get current value
     *
     * @param option
     */
    int getSocketOption(byte option);


    int getLocalPort();

    int getPort();


    String getAddress();

    String getLocalAddress();
}
