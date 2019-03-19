package com.pinyougou.search.service.impl;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;
@Component
public class ItemDeleteListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {

        try {
            ObjectMessage objectMessage = (ObjectMessage) message;
          Long[] goodsIds = (Long[]) objectMessage.getObject();
            System.out.println("监听到消息数据"+goodsIds);
          itemSearchService.deleteByGoodsIds(Arrays.asList(goodsIds));
            System.out.println("成功删除索引库");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
