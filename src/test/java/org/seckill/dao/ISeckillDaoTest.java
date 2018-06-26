package org.seckill.dao;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class ISeckillDaoTest {

	@Autowired
	private ISeckillDao seckillDao;
	
	@Test
	public void testReduceNumber() {
		int count = seckillDao.reduceNumber(1000, new Date());
		System.out.println(count);
	}

	@Test
	public void testQueryById() {
		System.out.println(seckillDao.queryById(1000));
	}

	@Test
	public void testQueryAll() {
		List<Seckill> queryAll = seckillDao.queryAll(0, 10);
		queryAll.stream().forEach(seckill -> {
			System.out.println(seckill);
		});
	}

}
