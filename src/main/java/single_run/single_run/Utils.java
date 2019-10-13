package single_run.single_run;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Utils {

    private static Logger logger = LoggerFactory.getLogger(Utils.class);
    private static Random r = new SecureRandom();
    /**
     * genPwd:(). <br/>
     * <p>
     * 生成随机密码
     *
     * @author chiwei
     * @param len
     * @return
     * @since JDK 1.6
     */
    private static String letter = "0123456789abcdefjhijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String base = "ABCDEFGHKMNPRWX2345689defhijkmnpqwxyz";

    /**
     * filterStr:(). <br/>
     * <p>
     * 过滤字符串前后空格以及空赋值
     *
     * @param str
     * @return
     * @author chiwei
     * @since JDK 1.6
     */
    public static String filterStr(String str) {
        return str = (str == null ? "" : str.trim());
    }

    /**
     * lenOk:(). <br/>
     *
     * @param str
     * @param minLen
     * @param maxLen
     * @return
     * @author chiwei
     * @since JDK 1.6
     */
    public static boolean lenOk(String str, int minLen, int maxLen) {
        return str.length() >= minLen && str.length() <= maxLen;
    }

    /**
     * splitStr2List:(). <br/>
     *
     * @param str
     * @param seperator
     * @return
     * @author chiwei
     * @since JDK 1.6
     */
    public static List<String> splitStr2List(String str, String seperator) {
        Iterator<String> iter = Splitter.on(seperator).trimResults().omitEmptyStrings().split(str)
            .iterator();
        List<String> list = new ArrayList<String>();
        while (iter.hasNext()) {
            list.add(iter.next());
        }
        return list;
    }

    /**
     * genRandomNum:(). <br/>
     * <p>
     * 生成指定长度的随机数字
     *
     * @param len
     * @return
     * @author chiwei
     * @since JDK 1.6
     */
    public static String genRandomNum(int len) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * @param len
     * @return
     */
    public static String genRandomStr(int len) {
        StringBuffer sb = new StringBuffer();
        int letterLen = letter.length();
        for (int i = 0; i < len; i++) {
            sb.append(letter.charAt(r.nextInt(letterLen)));
        }
        return sb.toString();
    }

    /**
     * uuid:(). <br/>
     *
     * @param len
     * @return
     * @author chiwei
     * @since JDK 1.6
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * genRandomHexString:().<br/>
     * <p>
     * 获取16进制随机数
     *
     * @param len
     * @return
     * @author whb
     * @since JDK 1.8
     */
    public static String genRandomHexString(int len) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            sb.append(Integer.toHexString(r.nextInt(16)));
        }
        return sb.toString().toUpperCase();

    }

    /**
     * randomNum:(). <br/>
     *
     * @param begin
     * @param end
     * @return
     * @author chiwei
     * @since JDK 1.6
     */
    public static int randomNum(int begin, int end) {
        return r.nextInt(end - begin) + begin;
    }

    /**
     * genCode:(). <br/>
     * <p>
     * 图形验证码
     *
     * @param len
     * @return
     */
    public static String genCode(int len) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            sb.append(base.charAt(r.nextInt(base.length() - 1)));
        }
        return sb.toString();
    }

    /**
     * code:(). <br/>
     *
     * @param content
     * @param out
     * @throws Exception
     * @author chiwei
     * @since JDK 1.6
     */
    public static void code(String content, OutputStream out) throws Exception {
        int width = 80;
        int height = 30;
        // 干扰颜色
        Color[] interColor = new Color[]{new Color(176, 196, 222), new Color(245, 222, 179),
            new Color(255, 228, 225), new Color(192, 192, 192)};
        // 字符颜色
        Color[] wordColor = new Color[]{new Color(47, 79, 79), new Color(0, 128, 128),
            new Color(95, 158, 160), new Color(70, 130, 180), new Color(105, 105, 105)};
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // 背景画图
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        // 边框画图
        g.setColor(Color.WHITE);
        g.drawRect(1, 1, width - 2, height - 2);
        // 干扰画图
        for (int i = 0; i < 20; i++) {
            int x1 = r.nextInt(width);
            int y1 = r.nextInt(height);
            int x2 = r.nextInt(width);
            int y2 = r.nextInt(height);
            g.setColor(interColor[r.nextInt(interColor.length - 1)]);
            g.drawLine(x1, y1, x2, y2);
        }
        Graphics2D g2 = (Graphics2D) g;
        Font[] fonts = new Font[]{new Font("宋体", Font.BOLD, 30), new Font("黑体", Font.PLAIN, 30),
            new Font("楷体", Font.ITALIC, 30),};
        int x = 5;
        for (int i = 0; i < content.length(); i++) {
            g.setFont(fonts[r.nextInt(fonts.length - 1)]);
            g.setColor(wordColor[r.nextInt(wordColor.length - 1)]);
            int degree = r.nextInt() % 30;
            String ch = String.valueOf(content.charAt(i));
            // 设置旋转角度
            g2.rotate(degree * Math.PI / 180, x, 5);
            g2.drawString(ch, x, randomNum(20, 30));
            // 旋转回来
            g2.rotate(-degree * Math.PI / 180, x, 5);
            x += randomNum(15, 20);
        }
        ImageIO.write(image, "jpg", out);
        out.flush();
        out.close();
    }

    /**
     * htmlFilter:(). <br/>
     * <p>
     * 过滤HTML标签
     *
     * @param content
     * @return
     * @author chiwei
     * @since JDK 1.6
     */
    public static String htmlFilter(String content) {
        if (StringUtils.isEmpty(content)) {
            return "";
        } else {
            String html = content.trim();
            html = StringUtils.replace(html, "'", "&apos;");
            html = StringUtils.replace(html, "\"", "&quot;");
            // 替换跳格
            html = StringUtils.replace(html, "\t", "&nbsp;&nbsp;");
            //html = StringUtils.replace(html, " ", "&nbsp;");// 替换空格
            html = StringUtils.replace(html, "<", "&lt;");
            html = StringUtils.replace(html, ">", "&gt;");
            return html;
        }
    }

    /**
     * @param str
     * @return
     */
    public static boolean urlIsReachable(String str) {
        URL url = null;
        try {
            url = new URL(str);
            URLConnection co = url.openConnection();
            co.setConnectTimeout(1000);
            co.connect();
            return true;
        } catch (Exception e) {
            logger.error("URL " + str + " 不可达", e);
            return false;
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1; i++) {
            System.out.println(genRandomStr(64));
        }
    }

}
