package org.seckill.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/spring-dao.xml", "classpath:spring/spring-service.xml","classpath:spring/spring-redis.xml" })
public class ISeckillServiceTest {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ISeckillService seckillService;

	@Test
	public void testGetSeckillList() {
		List<Seckill> seckillList = seckillService.getSeckillList();
		logger.info("seckillList={}", seckillList);
	}

	@Test
	public void testGetById() {
		Seckill seckill = seckillService.getById(1000);
		logger.info("seckill={}", seckill);
	}

	@Test
	public void testExportSeckillUrl() {
		Exposer exposer = seckillService.exportSeckillUrl(1000);
		logger.info("exposer={}", exposer);
	}

	@Test
	public void testExecuteSeckill() {
		SeckillExecution seckillExecution = seckillService.executeSeckill(1000, 159357,
				"399e8fc7cd4f6b3f9eace9518263181c");
		logger.info("seckillExecution={}", seckillExecution);
	}

	@Test
	public void testSeckillLogic() {
		long seckillId = 1002;
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		if (exposer.isExposed()) {
			logger.warn("exposer={}",exposer);
			SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, 159357, exposer.getMd5());
			logger.info("seckillExecution={}", seckillExecution);
		}else{
			//秒杀未开启
			logger.warn("exposer={}",exposer);
		}
	}
	
	@Test
	public void testKillByProceduer(){
		long seckillId = 1003;
		long phone = 15694785631L;
		
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		if(exposer.isExposed()){
			String md5 = exposer.getMd5();
			SeckillExecution execution = seckillService.executeSeckillProceduer(seckillId, phone, md5);
			logger.info(execution.getStateInfo());
		}
	}

}
