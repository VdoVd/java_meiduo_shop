package com.leyou.search.listener;

import com.leyou.search.service.IndexService;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemListener {

    @Autowired
    private IndexService indexService;

    @RabbitListener(bindings = @QueueBinding(
            value =  @Queue(name = "item.up.queue"),
            exchange =  @Exchange(name = "jhj",type = "topic"),
            key ="item.up"
    ))
    public void itemUp(Long id){

        this.indexService.createIndexById(id);
    }


    @RabbitListener(bindings = @QueueBinding(
            value =  @Queue(name = "item.down.queue"),
            exchange =  @Exchange(name = "jhj",type = "topic"),
            key ="item.down"
    ))
    public void itemDown(Long id){

        this.indexService.deleteById(id);
    }

}
