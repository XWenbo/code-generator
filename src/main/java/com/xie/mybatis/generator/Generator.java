package com.xie.mybatis.generator;

import com.xie.mybatis.generator.core.Configure;
import com.xie.mybatis.generator.model.JerseyModel;
import com.xie.mybatis.generator.model.Table;
import com.xie.mybatis.generator.utils.BeanUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
public class Generator {

	protected Configure config;

	public Generator() {
		config = new Configure();
	}

	public Generator(Configure config) {
		this.config = config;
	}

	public void generate(Table table) {
		generateModel(table);
		generateQuery(table);
//		generateBeanJson(table);
		//generateExample(table);
		generateManager(table);
		generateDao(table);
		generateXml(table);
	}

	public void generateJerseyModel(JerseyModel jerseyModel) {

		VelocityEngine velocityEngine = createVelocityEngine();
		VelocityContext context = createContext1(jerseyModel);
		Writer writer = createWriter(jerseyModel.getPackageName().concat(".resource").replace(".", "/")
				+ "/" + jerseyModel.getClassName() + "Resource.java");
		velocityEngine.mergeTemplate(config.templeteBase + "resourceTemplate.vm",
				context, writer);
		flushWriter(writer);
	}
	public void generateFeignClient(JerseyModel jerseyModel) {
		VelocityEngine velocityEngine = createVelocityEngine();
		VelocityContext context = createContext1(jerseyModel);
		Writer writer = createWriter(jerseyModel.getPackageName().concat(".feign").replace(".", "/")
				+ "/" + jerseyModel.getClassName() + "FeignClient.java");
		velocityEngine.mergeTemplate(config.templeteBase + "feignTemplate.vm",
				context, writer);
		flushWriter(writer);
	}

	public Writer createWriter(String path) {
		path = config.getTargetDir() + path;
		File file = new File(path);
		String dir = file.getParent();
		File pd = new File(dir);
		if (!pd.exists()) {
			pd.mkdirs();
		}
		FileWriter writer = null;
		try {
			writer = new FileWriter(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		// StringWriter writer=new StringWriter();
		return writer;
	}

	private VelocityEngine createVelocityEngine() {
		Properties props = new Properties();
		props.setProperty("resource.loader", "class");
		props.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		// properties.setProperty("input.encoding", "UTF-8");
		// properties.setProperty("output.encoding", "UTF-8");
		props.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
		props.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
		props.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
		VelocityEngine velocityEngine = new VelocityEngine(props);
		return velocityEngine;
	}

	public void generateModel(Table table) {
		VelocityEngine velocityEngine = createVelocityEngine();
		VelocityContext context = createContext(table);
		Writer writer = createWriter(config.getModelPackage().replace(".", "/")
				+ "/" + table.getBeanName() + "DO.java");
		velocityEngine.mergeTemplate(config.templeteBase + "beanTemplate.vm",
				context, writer);
		flushWriter(writer);
	}
	public void generateQuery(Table table) {
		VelocityEngine velocityEngine = createVelocityEngine();
		VelocityContext context = createContext(table);
		Writer writer = createWriter(config.getQueryPackage().replace(".", "/")
				+ "/" + table.getBeanName() + "Query.java");
		velocityEngine.mergeTemplate(config.templeteBase + "queryTemplate.vm",
				context, writer);
		flushWriter(writer);
	}

	public void generateBeanJson(Table table) {
		VelocityEngine velocityEngine = createVelocityEngine();
		VelocityContext context = createContext(table);
		Writer writer = createWriter(config.getBeanJsonPackage().replace(".", "/")
				+ "/" + table.getBeanName() + "ListJson.java");
		velocityEngine.mergeTemplate(config.templeteBase + "beanJsonTemplate.vm",
				context, writer);
		flushWriter(writer);
	}

	private void flushWriter(Writer writer) {
		try {
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void generateExample(Table table) {
		VelocityEngine velocityEngine = createVelocityEngine();
		VelocityContext context = createContext(table);
		Writer writer = createWriter(config.getExamplePackage().replace(".",
				"/")
				+ "/" + table.getBeanName() + "Example.java");
		velocityEngine.mergeTemplate(
				config.templeteBase + "exampleTemplate.vm", context, writer);
		flushWriter(writer);
	}

	public void generateManager(Table table) {
		VelocityEngine velocityEngine = createVelocityEngine();

		VelocityContext context = createContext(table);

		Writer writer = createWriter(config.getManagerPackage()
				.replace(".", "/") + "/" + table.getBeanName() + "Manager.java");
		velocityEngine.mergeTemplate(config.templeteBase
				+ "managerTemplate.vm","UTF-8", context, writer);

		Writer writer_1 = createWriter(config.getManagerPackage()
				.replace(".", "/") + "/impl/" + table.getBeanName() + "ManagerImpl.java");
		velocityEngine.mergeTemplate(config.templeteBase
				+ "managerImplTemplate.vm","UTF-8", context, writer_1);
		flushWriter(writer);
		flushWriter(writer_1);

	}

	public void generateDao(Table table) {
		VelocityEngine velocityEngine = createVelocityEngine();
		VelocityContext context = createContext(table);
		Writer writer = createWriter(config.getDaoPackage()
				.replace(".", "/") + "/" + table.getBeanName() + "Dao.java");
		velocityEngine.mergeTemplate(config.templeteBase
				+ "daoTemplate.vm","UTF-8", context, writer);
		flushWriter(writer);

	}

	public void generateXml(Table table) {
		VelocityEngine velocityEngine = createVelocityEngine();
		VelocityContext context = createContext(table);
		Writer writer = createWriter(config.getXmlPackage()
				.replace(".", "/") + "/" + "sqlmap_"+table.getBeanName() + ".xml");
		velocityEngine.mergeTemplate(config.templeteBase + "xmlTemplate.vm","UTF-8",
				context, writer);
		flushWriter(writer);

	}

	private VelocityContext createContext(Table table) {
		return createContext1(table);
	}
	private VelocityContext createContext1(Object object) {
		Map map = BeanUtils.getValueMap(object);
		Map configMap = BeanUtils.getValueMap(config);
		configMap.put("author", System.getProperty("user.name"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		configMap.put("dateTime", sdf.format(new Date()));
		map.putAll(configMap);
		VelocityContext context = new VelocityContext(map);

		return context;
	}


}
