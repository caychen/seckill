package org.seckill.service;

import org.seckill.entity.Seckill;

public interface ISeckillRedisService {

	Seckill getSeckill(long seckillId);

	String putSeckill(Seckill seckill);
}
