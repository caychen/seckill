package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.ISeckillDao;
import org.seckill.entity.Seckill;
import org.seckill.service.impl.SeckillRedisServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring.xml" })
public class ISeckillRedisServiceTest {

	@Autowired
	private SeckillRedisServiceImpl seckillRedis;

	@Autowired
	private ISeckillDao seckillDao;

	@Test
	public void testSeckillRedis() {
		long id = 1000;
		Seckill seckill = seckillRedis.getSeckill(id);
		if (seckill == null) {
			seckill = seckillDao.queryById(id);
			if (seckill != null) {
				String result = seckillRedis.putSeckill(seckill);
				System.out.println(result);

				//再取出来验证
				Seckill other = seckillRedis.getSeckill(id);
				System.out.println(other);
			}
		}
	}
}
