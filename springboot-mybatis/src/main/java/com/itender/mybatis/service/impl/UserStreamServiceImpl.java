package com.itender.mybatis.service.impl;

import com.itender.mybatis.entity.User;
import com.itender.mybatis.mapper.mysql.UserStreamMapper;
import com.itender.mybatis.service.UserStreamService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuanhewei
 * @date 2024/1/20 11:49
 * @description
 */
@Slf4j
@Service
public class UserStreamServiceImpl implements UserStreamService {

    private final UserStreamMapper userStreamMapper;


    private final SqlSessionFactory sqlSessionFactory;

    @Autowired
    public UserStreamServiceImpl(UserStreamMapper userStreamMapper, SqlSessionFactory sqlSessionFactory) {
        this.userStreamMapper = userStreamMapper;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Override
    public void getUsers() {
        new Thread(() -> {
            //使用sqlSessionFactory打开一个sqlSession，在没有读取完数据之前不要提交事务或关闭sqlSession
            log.info("----开启sqlSession");
            SqlSession sqlSession = sqlSessionFactory.openSession();
            try {
                //获取到指定mapper
                UserStreamMapper mapper = sqlSession.getMapper(UserStreamMapper.class);
                //调用指定mapper的方法，返回一个cursor
                Cursor<User> cursor = mapper.findAll();
                //查询数据总量
                Integer total = mapper.queryCount();
                //定义一个list，用来从cursor中读取数据，每读取够1000条的时候，开始处理这批数据；
                //当前批数据处理完之后，清空list，准备接收下一批次数据；直到大量的数据全部处理完；
                List<User> personList = new ArrayList<>();
                int i = 0;
                if (cursor != null) {
                    for (User person : cursor) {
                        if (personList.size() < 1000) {
                            personList.add(person);
                        } else if (personList.size() == 1000) {
                            ++i;
                            log.info("----{}、从cursor取数据达到1000条，开始处理数据", i);
                            log.info("----处理数据中...");
                            Thread.sleep(1000);//休眠1s模拟处理数据需要消耗的时间；
                            log.info("----{}、从cursor中取出的1000条数据已经处理完毕", i);
                            personList.clear();
                            personList.add(person);
                        }
                        if (total == (cursor.getCurrentIndex() + 1)) {
                            ++i;
                            log.info("----{}、从cursor取数据达到1000条，开始处理数据", i);
                            log.info("----处理数据中...");
                            Thread.sleep(1000);//休眠1s模拟处理数据需要消耗的时间；
                            log.info("----{}、从cursor中取出的1000条数据已经处理完毕", i);
                            personList.clear();
                        }
                    }
                    if (cursor.isConsumed()) {
                        log.info("----查询sql匹配中的数据已经消费完毕！");
                    }
                }
                sqlSession.commit();
                log.info("----提交事务");
            } catch (Exception e) {
                e.printStackTrace();
                sqlSession.rollback();
            } finally {
                if (sqlSession != null) {
                    //全部数据读取并且做好其他业务操作之后，提交事务并关闭连接；
                    sqlSession.close();
                    log.info("----关闭sqlSession");
                }
            }

        }).start();
    }

    @Override
    public User getUserById(Integer id) {
        return null;
    }

    @Override
    public void addUser(User user) {
        userStreamMapper.addUser(user);
    }

    @Override
    public void updateUser(User user) {

    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public Integer queryCount() {
        return userStreamMapper.queryCount();
    }
}
