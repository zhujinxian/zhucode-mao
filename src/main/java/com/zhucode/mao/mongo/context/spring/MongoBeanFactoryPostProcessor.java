package com.zhucode.mao.mongo.context.spring;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.paoding.rose.scanning.ResourceRef;
import net.paoding.rose.scanning.RoseScanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import com.zhucode.mao.mongo.dataaccess.DataSourceFactory;

public class MongoBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	
	private final Log logger = LogFactory
			.getLog(MongoBeanFactoryPostProcessor.class);

	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		doPostProcessBeanFactory(beanFactory);
	}

	private void doPostProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) {
		// 记录开始
		if (logger.isInfoEnabled()) {
			logger.info("[MAO] starting ...");
		}

		// 1、获取标注mao标志的资源(ResourceRef)，即classes目录、在/META-INF/mao.properties或/META-INF/MENIFEST.MF配置了mao属性的jar包
		final List<ResourceRef> resources = findMAOResources();

		// 2、从获取的资源(resources)中，把mao=*、mao=MAO、mao=mao的筛选出来，并以URL的形式返回
		List<String> urls = findMongoResources(resources);

		// 3、从每个URL中找出符合规范的MAO接口，并将之以MaogoFactoryBean的形式注册到Spring容器中
		findMAODefinitions(beanFactory, urls);

		// 记录结束
		if (logger.isInfoEnabled()) {
			logger.info("[MAO] exits");
		}
	}

	/*
	 * 找出含有MAO标帜的目录或jar包
	 */
	private List<ResourceRef> findMAOResources() {
		final List<ResourceRef> resources;
		try {
			resources = RoseScanner.getInstance()
					.getJarOrClassesFolderResources();
		} catch (IOException e) {
			throw new ApplicationContextException(
					"error on getJarResources/getClassesFolderResources", e);
		}
		return resources;
	}

	/*
	 * 找出含有mao、MAO标识的url
	 */
	private List<String> findMongoResources(final List<ResourceRef> resources) {
		List<String> urls = new LinkedList<String>();
		for (ResourceRef ref : resources) {
			if (ref.hasModifier("mao") || ref.hasModifier("MAO")) {
				try {
					Resource resource = ref.getResource();
					File resourceFile = resource.getFile();
					if (resourceFile.isFile()) {
						urls.add("jar:file:" + resourceFile.toURI().getPath()
								+ ResourceUtils.JAR_URL_SEPARATOR);
					} else if (resourceFile.isDirectory()) {
						urls.add(resourceFile.toURI().toString());
					}
				} catch (IOException e) {
					throw new ApplicationContextException(
							"error on resource.getFile", e);
				}
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("[MAO] found " + urls.size() + " MAO urls: " + urls);
		}
		return urls;
	}

	/*
	 * 从获得的目录或jar包中寻找出符合规范的MAO接口，并注册到Spring容器中
	 */
	private void findMAODefinitions(
			ConfigurableListableBeanFactory beanFactory, List<String> urls) {
		MongoComponentProvider provider = new MongoComponentProvider();
		Set<String> maoClassNames = new HashSet<String>();

		for (String url : urls) {
			if (logger.isInfoEnabled()) {
				logger.info("[MAO] call 'MAO/find'");
			}

			Set<BeanDefinition> dfs = provider.findCandidateComponents(url);
			if (logger.isInfoEnabled()) {
				logger.info("[MAO] found " + dfs.size()
						+ " beanDefinition from '" + url + "'");
			}

			for (BeanDefinition beanDefinition : dfs) {
				String maoClassName = beanDefinition.getBeanClassName();
				if (maoClassNames.contains(maoClassName)) {
					if (logger.isDebugEnabled()) {
						logger.debug("[MAO] ignored replicated mao class: "
								+ maoClassName + "  [" + url + "]");
					}
					continue;
				}
				maoClassNames.add(maoClassName);

				registerMAODefinition(beanFactory, beanDefinition);
			}
		}
	}

	/*
	 * 将找到的一个MAO接口注册到Spring容器中
	 */
	private void registerMAODefinition(
			ConfigurableListableBeanFactory beanFactory,
			BeanDefinition beanDefinition) {
		final String maoClassName = beanDefinition.getBeanClassName();
		MutablePropertyValues propertyValues = beanDefinition
				.getPropertyValues();
		/*
		 * 属性及其设置要按 MongoFactoryBean 的要求来办
		 */
		propertyValues.addPropertyValue("objectType", maoClassName);
		propertyValues.addPropertyValue("dataSourceFactory", getDataSourceFactory(beanFactory));
	
		
		ScannedGenericBeanDefinition scannedBeanDefinition = (ScannedGenericBeanDefinition) beanDefinition;
		scannedBeanDefinition.setPropertyValues(propertyValues);
		scannedBeanDefinition.setBeanClass(MongoFactoryBean.class);

		DefaultListableBeanFactory defaultBeanFactory = (DefaultListableBeanFactory) beanFactory;
		defaultBeanFactory.registerBeanDefinition(maoClassName, beanDefinition);

		if (logger.isDebugEnabled()) {
			logger.debug("[MAO] register MAO: " + maoClassName);
		}
	}

	private Object getDataSourceFactory(ConfigurableListableBeanFactory beanFactory) {
		
		if (beanFactory.containsBeanDefinition("mongo.dataSourceFactory")) {
			
			DataSourceFactory dataSourceFactory = (DataSourceFactory) beanFactory.getBean(
                     "mongo.dataSourceFactory", DataSourceFactory.class);
			
			return dataSourceFactory;
		}
		
		return null;
	}

}
