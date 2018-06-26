package org.seckill.service.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.seckill.dao.ISeckillDao;
import org.seckill.dao.ISuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillClosedException;
import org.seckill.exception.SeckillException;
import org.seckill.service.ISeckillRedisService;
import org.seckill.service.ISeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

@Service
public class SeckillServiceImpl implements ISeckillService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	// 用于混淆md5，随便写
	private final String salt = "faaNfa48efd02fe557jtdAN7ndFA548FAfJ55fiEJ";

	@Autowired
	private ISeckillDao seckillDao;

	@Autowired
	private RedisTemplate<Serializable, Serializable> redisTemplate;

	@Autowired
	private ISuccessKilledDao successKilledDao;

	@Autowired
	private ISeckillRedisService seckillRedisService;

	@Override
	public List<Seckill> getSeckillList() {
		// TODO Auto-generated method stub
		return seckillDao.queryAll(0, 10);
	}

	@Override
	public Seckill getById(long seckillId) {
		// TODO Auto-generated method stub
		return seckillDao.queryById(seckillId);
	}

	@Override
	public Exposer exportSeckillUrl(long seckillId) {
		// TODO Auto-generated method stub
		// 优化：缓存优化
		Seckill seckill = null;
		/*
		 * //1、通过redisTemplate来进行缓存操作 ValueOperations<Serializable,
		 * Serializable> forValue = redisTemplate.opsForValue(); try{ seckill =
		 * (Seckill) forValue.get("seckill:" + seckillId); }catch(Exception e){
		 * e.printStackTrace(); logger.error(e.getMessage(), e); }
		 * 
		 * if(seckill != null){ return getExposerFromSeckillId(seckillId); }
		 * 
		 * //如果redis中不存在，则查询数据库 seckill = seckillDao.queryById(seckillId);
		 * 
		 * if (seckill == null) { return new Exposer(false, seckillId); }
		 * 
		 * //存入redis缓存中 try{ forValue.set("seckill:" + seckillId, seckill);
		 * }catch(Exception e){ e.printStackTrace();
		 * logger.error(e.getMessage(), e); }
		 */

		// 2、通过SeckillRedisService进行redis缓存
		seckill = seckillRedisService.getSeckill(seckillId);
		if (seckill == null) {
			seckill = seckillDao.queryById(seckillId);
		}

		if (seckill == null) {
			return new Exposer(false, seckillId);
		}

		seckillRedisService.putSeckill(seckill);

		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();

		Date now = new Date();
		if (now.getTime() < startTime.getTime() || now.getTime() > endTime.getTime()) {
			return new Exposer(false, seckillId, now.getTime(), startTime.getTime(), endTime.getTime());
		}

		return getExposerFromSeckillId(seckillId);
	}

	private Exposer getExposerFromSeckillId(long seckillId) {
		// 转换
		String md5 = getMD5(seckillId);
		return new Exposer(true, md5, seckillId);
	}

	private String getMD5(long seckillId) {
		String base = seckillId + "/" + salt;
		return DigestUtils.md5DigestAsHex(base.getBytes());
	}

	// 秒杀是否成功，成功:减库存，增加明细；失败:抛出异常，事务回滚
	@Override
	@Transactional
	/**
	 * 使用注解控制事务方法的优点: 1.开发团队达成一致约定，明确标注事务方法的编程风格
	 * 2.保证事务方法的执行时间尽可能短，不要穿插其他网络操作RPC/HTTP请求或者剥离到事务方法外部
	 * 3.不是所有的方法都需要事务，如只有一条修改操作、只读操作不要事务控制
	 */
	// exportSeckillUrl暴露杀地址的同时，要把md5传递给executeSeckill
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillException, SeckillClosedException {
		// TODO Auto-generated method stub
		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			throw new SeckillException("seckill data rewrite");
		}

		// 优化执行顺序
		// 执行秒杀逻辑
		try {
			// 记录秒杀行为
			int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
			if (insertCount <= 0) {
				// 重复秒杀
				throw new RepeatKillException("seckill repeated");
			} else {
				// 减库存，资源竞争点
				int updateCount = seckillDao.reduceNumber(seckillId, new Date());
				if (updateCount <= 0) {
					// 秒杀结束，rollback
					throw new SeckillClosedException("seckill is closed");
				} else {
					// 秒杀成功， commit
					SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
				}
			}
		} catch (SeckillClosedException sce) {
			throw sce;
		} catch (RepeatKillException re) {
			throw re;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			// 所有编译期异常，转化为运行期异常
			throw new SeckillException("seckill inner error: " + e.getMessage());
		}

	}

	@Override
	public SeckillExecution executeSeckillProceduer(long seckillId, long userPhone, String md5) {
		// TODO Auto-generated method stub
		if (null == md5 || !md5.equals(getMD5(seckillId))) {
			return new SeckillExecution(seckillId, SeckillStateEnum.DATE_REWRITE);
		}

		Date killTime = new Date();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("seckillId", seckillId);
		paramMap.put("phone", userPhone);
		paramMap.put("killTime", killTime);
		paramMap.put("result", null);
		try {
			seckillDao.killByProceduer(paramMap);

			int result = MapUtils.getInteger(paramMap, "result", -2);// -2表示如果没有值，则默认为-2
			if (result == 1) {
				SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);

				return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
			} else {
				return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));

			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
		}
	}

}
