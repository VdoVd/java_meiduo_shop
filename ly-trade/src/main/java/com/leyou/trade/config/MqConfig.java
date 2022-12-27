package com.leyou.trade.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.leyou.common.constants.MQConstants.ExchangeConstants.DEAD_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.ExchangeConstants.ORDER_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.QueueConstants.DEAD_ORDER_QUEUE;
import static com.leyou.common.constants.MQConstants.QueueConstants.EVICT_ORDER_QUEUE;
import static com.leyou.common.constants.MQConstants.RoutingKeyConstants.EVICT_ORDER_KEY;


@Configuration
public class MqConfig {
    /**
     * 订单超时未支付的时间
     */
    private static final long ORDER_QUEUE_DELAY_TIME = 30000;

    /**
     * 订单业务交换机
     */
    @Bean
    public TopicExchange orderTopicExchange(){
        return new TopicExchange(ORDER_EXCHANGE_NAME, true, false);
    }
    /**
     * 死信交换机
     */
    @Bean
    public TopicExchange deadTopicExchange(){
        return new TopicExchange(DEAD_EXCHANGE_NAME, true, false);
    }
    /**
     * 死信队列
     */
    @Bean
    public Queue deadOrderQueue(){
        Map<String, Object> args = new HashMap<>(2);
        // x-message-ttl 声明队列TTL值
        args.put("x-message-ttl", ORDER_QUEUE_DELAY_TIME);
        // x-dead-letter-exchange 声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", DEAD_EXCHANGE_NAME);
        return QueueBuilder.durable(DEAD_ORDER_QUEUE).withArguments(args).build();
    }
    /**
     * 清理订单业务队列
     */
    @Bean
    public Queue evictOrderQueue(){
        return new Queue(EVICT_ORDER_QUEUE, true);
    }
    /**
     * 将死信队列与ly.order.exchange交换机绑定
     */
    @Bean
    public Binding bindingDeadQueue(){
        return BindingBuilder.bind(deadOrderQueue()).to(orderTopicExchange()).with(EVICT_ORDER_KEY);
    }
    /**
     * 将清理订单业务队列与死信交换机绑定
     */
    @Bean
    public Binding bindingEvictQueue(){
        return BindingBuilder.bind(evictOrderQueue()).to(deadTopicExchange()).with(EVICT_ORDER_KEY);
    }

    //消息转换器，默认使用jdk，要换，生产者和消费者都要换
    @Bean
    public Jackson2JsonMessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}