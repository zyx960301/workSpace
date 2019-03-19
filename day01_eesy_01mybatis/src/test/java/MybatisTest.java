
import com.itheima.dao.UserDao;
import com.itheima.domain.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MybatisTest {
    public static void main(String[] args) throws IOException {
        //1.读取配置配置文件
        InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");
        // 创建生产SqlSessionFactory工厂的对象
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        //3.使用生产工厂对象 生产SqlSessionFactory对象
        SqlSessionFactory factory = builder.build(is);
        //使用工厂生产SqlSession对象
        SqlSession session = factory.openSession();
        //5.使用 SqlSession 创建 dao 接口的代理
        UserDao userDao = session.getMapper(UserDao.class);
        //6.使用代理对象执行查询所有
        List<User> users = userDao.findAll();
        for (User user : users) {
            System.out.println(user);
        }
        session.close();
        is.close();
    }
}
