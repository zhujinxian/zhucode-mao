package com.zhucode.mao.example;

import java.net.UnknownHostException;

import net.paoding.rose.RoseWebApplication;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.zhucode.mao.mongo.context.spring.MongoBeanFactoryPostProcessor;
import com.zhucode.mao.mongo.dataaccess.DataSourceFactory;

@SpringBootApplication
public class Application extends RoseWebApplication {
	
    @Bean
    BeanFactoryPostProcessor getMongoBeanBeanFactoryPostProcessor() {
    	return new MongoBeanFactoryPostProcessor();
    }
    
    
	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
	    TomcatEmbeddedServletContainerFactory tf = new TomcatEmbeddedServletContainerFactory();
	    tf.setPort(8080);
	    return tf;
	}
	
	@Bean(name="mongo.dataSourceFactory")
	public DataSourceFactory getDsf() {
		return new DataSourceFactory() {
			@Override
			public DB getDataSource(String db) {
				MongoClientOptions mco = MongoClientOptions.builder()
						.socketKeepAlive(true) // 是否保持长链接
						.connectTimeout(5000) // 链接超时时间
						.socketTimeout(5000) // read数据超时时间
						.connectionsPerHost(30) // 每个地址最大请求数
						.maxWaitTime(1000 * 60 * 2)
						.threadsAllowedToBlockForConnectionMultiplier(100)
						.maxConnectionIdleTime(5*60*1000).build();// 长链接的最大等待时间

				
				MongoClient mclient = null;
				try {
					mclient = new MongoClient("10.0.75.201:27017", mco);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				return mclient.getDB(db);
			}
			
		};
	}
	
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}