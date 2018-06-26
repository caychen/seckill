package org.seckill.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

public interface ISeckillDao {

	/**
	 * 减库存
	 * @param seckillId
	 * @param killTime
	 * @return 如果影响行数大于1, 表示更新的记录行数
	 */
	int reduceNumber(@Param("seckillId")long seckillId, @Param("killTime") Date killTime);
	
	/**
	 * 查找id对应的商品信息
	 * @param seckillId
	 * @return 
	 */
	Seckill queryById(long seckillId);
	
	/**
	 * 根据偏移量查询商品列表
	 * @param offset
	 * @param limit
	 * @return
	 */
	List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
	
	void killByProceduer(Map<String, Object> paramMap);
}
