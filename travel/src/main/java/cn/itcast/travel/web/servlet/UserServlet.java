package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.Service;
import cn.itcast.travel.service.impl.Serviceimpl;
import cn.itcast.travel.util.Md5Util;
import cn.itcast.travel.util.UuidUtil;
import org.apache.commons.beanutils.BeanUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Random;

@WebServlet("/user/*")
public class UserServlet extends BaseServlet {
    Service service = new Serviceimpl();
    ResultInfo info = new ResultInfo();

    protected void check(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //服务器通知浏览器不要缓存
        response.setHeader("pragma", "no-cache");
        response.setHeader("cache-control", "no-cache");
        response.setHeader("expires", "0");

        //在内存中创建一个长80，宽30的图片，默认黑色背景
        //参数一：长
        //参数二：宽
        //参数三：颜色
        int width = 80;
        int height = 30;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        //获取画笔
        Graphics g = image.getGraphics();
        //设置画笔颜色为灰色
        g.setColor(Color.GRAY);
        //填充图片
        g.fillRect(0, 0, width, height);

        //产生4个随机验证码，12Ey
        String checkCode = getCheckCode();
        //将验证码放入HttpSession中
        request.getSession().setAttribute("CHECKCODE_SERVER", checkCode);

        //设置画笔颜色为黄色
        g.setColor(Color.YELLOW);
        //设置字体的小大
        g.setFont(new Font("黑体", Font.BOLD, 24));
        //向图片上写入验证码
        g.drawString(checkCode, 15, 25);

        //将内存中的图片输出到浏览器
        //参数一：图片对象
        //参数二：图片的格式，如PNG,JPG,GIF
        //参数三：图片输出到哪里去
        ImageIO.write(image, "PNG", response.getOutputStream());
    }

    /**
     * 产生4位随机字符串
     */
    private String getCheckCode() {
        String base = "0123456789ABCDEFGabcdefg";
        int size = base.length();
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i <= 4; i++) {
            //产生0到size-1的随机值
            int index = r.nextInt(size);
            //在base字符串中获取下标为index的字符
            char c = base.charAt(index);
            //将c放入到StringBuffer中去
            sb.append(c);
        }
        return sb.toString();
    }

    protected void checkName(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        Boolean flag = service.checkname(username);
        if (flag) {
            info.setFlag(true);
            info.setErrorMsg("已有相同用户名");
        } else {
            info.setFlag(false);
            info.setErrorMsg("用户名可用");
        }
        writeValue(info, response);

    }

    protected void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String check = request.getParameter("check");
        String checkcode_server = (String) request.getSession().getAttribute("CHECKCODE_SERVER");
        if (checkcode_server != null && checkcode_server.equalsIgnoreCase(check)) {
            Map<String, String[]> map = request.getParameterMap();
            User user = new User();
            try {
                BeanUtils.populate(user, map);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            //加盐操作
            String saltStr = "s%^";
            String s = saltStr + user.getPassword();
            try {
                s = Md5Util.encodeByMd5(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
            user.setPassword(s);
            user.setCode(UuidUtil.getUuid());
            user.setStatus("N");
            Boolean flag = service.save(user);
            if (flag) {
                info.setFlag(true);
                System.out.println("<a href='http://localhost/travel/user/active?code=\"+user.getCode()+\"'>点击激活【黑马旅游网】</a>");
            } else {
                info.setFlag(false);
                info.setErrorMsg("注册失败");
            }
            writeValue(info, response);

        } else {
            info.setFlag(false);
            info.setErrorMsg("验证码错误");
            writeValue(info, response);
        }
    }
    protected void active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{}
}