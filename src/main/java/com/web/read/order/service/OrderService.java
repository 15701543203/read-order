package com.web.read.order.service;

import java.util.List;

import com.web.read.bean.TbOrder;
import com.web.read.bean.TbOrderItem;
import com.web.read.bean.TbOrderShipping;
import com.web.read.common.bean.ReadResult;

/**
 * 订单
 * 
 * @Title OrderService.java
 *        <p>
 *        Description:
 *        </p>
 *        <p>
 *        Company:
 *        </p>
 * @Package com.web.read.order
 * @Author Administrator
 * @Date 2018年4月23日上午10:22:10
 * @version v1.0
 */
public interface OrderService {
	/**
	 * 
	 * Description: 下订单
	 * @param order 订单信息
	 * @param itemList 订单详情
	 * @param orderShipping 物流信息
	 * @return 
	 */
	ReadResult createOrder(TbOrder order, List<TbOrderItem> itemList, TbOrderShipping orderShipping);
}
