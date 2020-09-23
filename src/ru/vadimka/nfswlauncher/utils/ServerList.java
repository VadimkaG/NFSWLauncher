package ru.vadimka.nfswlauncher.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.Main;
import ru.vadimka.nfswlauncher.ValueObjects.ServerVO;
import ru.vadimka.nfswlauncher.ValueObjects.StoragedRunnable;

public abstract class ServerList {
	protected static final HashMap<String,ServerVO> servers = new HashMap<String,ServerVO>();
	
	/**
	 * Получить список серверов, слитый из онлайн версии и кэша
	 * @return
	 */
	public static Collection<ServerVO> getList() {
		
		// Подгрузка списка серверов из онлайн
		
		StoragedRunnable<List<ServerVO>> serversOnline_run = new StoragedRunnable<List<ServerVO>>() {
			@Override
			public void run() {
				setData(getListOnline());
			}
		};
		AsyncTasksUtils.addTask(serversOnline_run);
		
		// ======= end =========
		
		// Подгрузка серверов из xml на компьютере
		
		AsyncTasksUtils.addTask(() -> {
			loadChache();
		});
		
		// ======= end ========
		
		try {
			AsyncTasksUtils.waitTasks(10);
		} catch (InterruptedException e) {}
		
		List<ServerVO> serversOnline = serversOnline_run.getData();
		
		boolean needUpdate = false;
		if (serversOnline.size() > 0 && servers.size() < 1)
			needUpdate = true;
		for (ServerVO server : serversOnline) {
			if (servers.containsKey(server.getName())) {
				ServerVO chacheServer = servers.get(server.getName());
				if (!chacheServer.getIP().equalsIgnoreCase(server.getIP())) {
					chacheServer.setIP(server.getIP());
					needUpdate = true;
				}
			} else {
				servers.put(server.getName(), server);
				needUpdate = true;
			}
		}
		
		if (needUpdate) {
			AsyncTasksUtils.addTask(() -> {
				saveChache();
			});
		}

		
		return servers.values();
	}
	/**
	 * Получить спсок серверов, загруженных из кэша
	 * Для корректной работы требуется взывать метод loadChache()
	 * @return HashMap<String,ServerVO>
	 */
	public static HashMap<String,ServerVO> getListChached() {
		return servers;
	}
	/**
	 * Подтянуть список серверов из кэша на компьютере
	 */
	public static void loadChache() {
		try {
			
			File custom_servers = new File(Main.getConfigDir()+File.separator+"servers.xml");
			
			if (custom_servers.exists() && custom_servers.canRead()) {
				DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document document = documentBuilder.parse("file:///"+custom_servers.getAbsolutePath());
				
				Element Servers = document.getDocumentElement();
				NodeList items = Servers.getChildNodes();
				
				for (int i = 0; i < items.getLength(); i++) {
					Node server = items.item(i);
					if (server.getNodeName() == "server") {
						if (server.getAttributes().getNamedItem("ip") == null || server.getAttributes().getNamedItem("protocol").getTextContent() == null)
							continue;
						boolean isHttps = false;
						if (
								server.getAttributes().getNamedItem("https") != null
									&&
								server.getAttributes().getNamedItem("https").getTextContent().equalsIgnoreCase("true")
							) isHttps = true;
						ServerVO vo = new ServerVO(server.getAttributes().getNamedItem("ip").getTextContent(),server.getTextContent(),isHttps);
						vo.setProtocol(Main.genProtocolByName(server.getAttributes().getNamedItem("protocol").getTextContent(),vo));
						servers.put(vo.getName(),vo);
					}
				}
			}
		} catch (SAXException e) {
			Log.getLogger().warning("Ошибка разбора синтаксиса, при попытке обновить список серверов.");
		} catch (ParserConfigurationException e) {
			Log.getLogger().warning("Ошибка разбора данных, при попытке обновить список серверов.");
		} catch (UnknownHostException e) {
			Log.getLogger().warning("Не удалось соединиться с сервером, чтобы получить список серверов.");
		} catch (IOException e) {
			Log.getLogger().log(Level.WARNING,"Ошибка при попытке получить список серверов.",e);
		}
	}
	/**
	 * Сохранить кэш в файл
	 */
	public static void saveChache() {
		String xml = "<servers>\n";
		for (Entry<String, ServerVO> server: servers.entrySet()) {
			xml += "\t<server\n"
				+  "\t\tip=\""+server.getValue().getIP()+"\"\n"
				+  "\t\tprotocol=\""+server.getValue().getProtocol().getNameProtocol()+"\"\n"
				+  "\t\thttps=\""+(server.getValue().isHttps()?"true":"false")+"\"\n"
				+  "\t\t>"+server.getValue().getName()+"</server>\n";
		}
		xml += "</servers>";
		File custom_servers = new File(Main.getConfigDir()+File.separator+"servers.xml");
		if ((custom_servers.exists() && custom_servers.canWrite()) || custom_servers.getParentFile().canWrite()) {
			try {
				FileOutputStream fos = new FileOutputStream(custom_servers);
				fos.write(xml.getBytes(StandardCharsets.UTF_8));
				fos.flush();
				fos.close();
			} catch (IOException e) {
				Log.getLogger().warning("Не удалось записать список серверов в кэш");
			}
		} else {
			Log.getLogger().warning("Кэш серверов не сохранен. Нет прав на обновление/запись файла: "+custom_servers.getAbsolutePath());
		}
	}
	/**
	 * Загрузить список серверов из интернета
	 * @return List<ServerVO>
	 */
	public static List<ServerVO> getListOnline() {
		final List<ServerVO> serversOnline = new ArrayList<ServerVO>();
		try {
			HTTPRequest.ActionAutoContainer response = new HTTPRequest.ActionAutoContainer();
			HTTPRequest request = new HTTPRequest(Config.SERVERS_LIST_LINK,response);
			
			request.proc();
			request.waitResponse();
			
			String xml = response.toString();
			
			InputSource source = new InputSource(new StringReader(xml));
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(source);
			
			Node Servers = document.getDocumentElement();
			NodeList items = Servers.getChildNodes();
			
			for (int i = 0; i < items.getLength(); i++) {
				Node server = items.item(i);
				if (
						server.getNodeName() == "server" && 
						server.getAttributes().getNamedItem("protocol") != null && 
						server.getAttributes().getNamedItem("ip") != null) {
					
					final String protocol = new String(server.getAttributes().getNamedItem("protocol").getTextContent());
					final String ip = new String(server.getAttributes().getNamedItem("ip").getTextContent());
					final String name = new String(server.getTextContent());
					
					boolean isHttps = false;
					if (server.getAttributes().getNamedItem("https") != null  && server.getAttributes().getNamedItem("https").getTextContent().equalsIgnoreCase("true"))
						isHttps = true;
					
					ServerVO vo = new ServerVO(ip,name,isHttps);
					vo.setProtocol(Main.genProtocolByName(protocol,vo));
					serversOnline.add(vo);
				}
			}
		} catch (SAXException e) {
			Log.getLogger().warning("Ошибка разбора синтаксиса, при попытке обновить список серверов.");
		} catch (ParserConfigurationException e) {
			Log.getLogger().warning("Ошибка разбора данных, при попытке обновить список серверов.");
		} catch (UnknownHostException e) {
			Log.getLogger().warning("Не удалось соединиться с сервером, чтобы получить список серверов.");
		} catch (IOException e) {
			Log.getLogger().log(Level.WARNING,"Ошибка при попытке получить список серверов.",e);
		}
		return serversOnline;
	}
	/**
	 * Удалить сервер из кэша
	 * @param name Имя сервера
	 */
	public static void deleteServerFromChache(String name) {
		if (servers.containsKey(name))
			servers.remove(name);
	}
	public static void addServerToChache(ServerVO server) {
		if (!servers.containsKey(server.getName()))
			servers.put(server.getName(), server);
	}
}
