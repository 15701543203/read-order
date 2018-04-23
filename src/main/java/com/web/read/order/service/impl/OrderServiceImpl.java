package com.web.read.order.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.web.read.bean.TbOrder;
import com.web.read.bean.TbOrderItem;
import com.web.read.bean.TbOrderShipping;
import com.web.read.common.bean.ReadResult;
import com.web.read.mapper.TbOrderItemMapper;
import com.web.read.mapper.TbOrderMapper;
import com.web.read.mapper.TbOrderShippingMapper;
import com.web.read.order.redis.JedisDao;
import com.web.read.order.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private TbOrderShippingMapper orderShippingMapper ;
	@Autowired
	private JedisDao jedisClient;
	
	@Value("${ORDER_GEN_KEY}")
	private String ORDER_GEN_KEY;
	@Value("${ORDER_INIT_ID}")
	private String ORDER_INIT_ID;
	@Value("${ORDER_DETAIL_GEN_KEY}")
	private String ORDER_DETAIL_GEN_KEY;
	@Override
	public ReadResult createOrder(TbOrder order, List<TbOrderItem> itemList, TbOrderShipping orderShipping) {
		//向订单表中插入记录
		//获取订单号
		String string = jedisClient.get(ORDER_GEN_KEY);
		if(StringUtils.isEmpty(string)){
			jedisClient.set(ORDER_GEN_KEY,ORDER_INIT_ID);
		}
		long orderId = jedisClient.incr(ORDER_GEN_KEY);
		//补全bean属性
		order.setOrderId(orderId+"");
		/**
		 * 状态：
		 * 1、未付款
		 * 2、已付款
		 * 3、未发货
		 * 4、已发货，
		 * 5、交易成功
		 * 6、交易关闭
		 */
		order.setStatus(1);
		Date date = new Date();
		order.setCreateTime(date);
		order.setUpdateTime(date);
		/**
		 * 0：未评价
		 * 1：已评价
		 */
		order.setBuyerRate(0);
		//向订单表插入数据
		orderMapper.insert(order);
		for (TbOrderItem tbOrderItem : itemList) {
			//补全订单明细
			//取订单明细id
			long orderDetailId = jedisClient.incr(ORDER_DETAIL_GEN_KEY);
			tbOrderItem.setId(orderDetailId+"");
			tbOrderItem.setOrderId(orderId + "");
			//向订单明细表中插入数据
			orderItemMapper.insert(tbOrderItem);
		}
		//补全物流表信息，并插入到数据库表
		orderShipping.setOrderId(orderId + "");
		orderShipping.setCreated(date);
		orderShipping.setUpdated(date);
		orderShippingMapper.insert(orderShipping);

		return ReadResult.ok(orderId);
	}

}
