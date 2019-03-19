package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
@Component
public class ItemSearchListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        System.out.println("监听收到的消息");
        TextMessage testMessage = (TextMessage) message;
        try {
            String text = testMessage.getText();//json字符串
            List<TbItem> List = JSON.parseArray(text,TbItem.class);//转化成List数值
            itemSearchService.importList(List);//导入
            System.out.println("成功导入索引库");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
