package ru.vadimka.nfswlauncher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.Main;

public class ClientConfigAction implements ActionListener, Linkable {
	private File fileConfig;
	
	private static ClientConfigAction INSTANCE = null;
	
	private static HashMap<String,String> STORAGE = new HashMap<String,String>();
	private static HashMap<String,Getter<String>> storageLinks = new HashMap<String,Getter<String>>();
	
	private static final List<String> SETTINGS_NAMES = (List<String>) Arrays.asList("audiomode",
			"audioquality",
			"brightness",
			"performancelevel",
			"pixelaspectratiooverride",
			"vsyncon",
			"motionblurenable",
			"overbrightenable",
			"particlesystemenable",
			"shaderdetail",
			"shadowdetail",
			"watersimenable");

	@Override
	public void actionPerformed(ActionEvent e) {
		save();
	}

	@Override
	public Linkable link(String key, Getter<String> g) {
		storageLinks.put(key, g);
		return this;
	}
	public static ClientConfigAction call() {
		if (INSTANCE == null) INSTANCE = new ClientConfigAction();
		return INSTANCE;
	}
	private ClientConfigAction() {
		super();
		fileConfig = new File(Main.getWorkDir()+File.separator+"Settings"+File.separator+"UserSettings.xml");
		load();
	}
	/**
	 * Загрузить настройки
	 */
	public void load() {
		STORAGE.clear();
		if (fileConfig.exists() && fileConfig.canRead()) {
			try {
				DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document document;
				document = documentBuilder.parse(fileConfig.getAbsolutePath());
				Node Root = document.getDocumentElement();
				NodeList items = Root.getChildNodes();
				for (int i = 0; i < items.getLength(); i++) {
					Node item = items.item(i);
					switch (item.getNodeName().trim()) {
					case "UI":
						NodeList iitems = item.getChildNodes();
						for (int ii = 0; ii < iitems.getLength(); ii++) {
							Node iitem = iitems.item(ii);
							if (iitem.getNodeName().equals("Language")) {
								STORAGE.put("lang", iitem.getTextContent());
							}
						}
						break;
					case "VideoConfig":
						NodeList iitems1 = item.getChildNodes();
						for (int ii = 0; ii < iitems1.getLength(); ii++) {
							Node iitem = iitems1.item(ii);
							if (SETTINGS_NAMES.contains(iitem.getNodeName())) {
								if (!STORAGE.containsKey(iitem.getNodeName().trim()))
									STORAGE.put(iitem.getNodeName().trim(), iitem.getTextContent());
							}
						}
						break;
					default:
						break;
					}
				}
				Log.print("Настройки клиента загружены.");
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				e.printStackTrace();
			}
		} else
			Log.print("Не найден файл настроек клиента");
	}
	/**
	 * Сохранить настройки
	 */
	public void save() {
		Log.print("Сохранение настроек клиента...");
		if (fileConfig.exists() && fileConfig.canRead() && fileConfig.canWrite()) {
			try {
				DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document document;
				document = documentBuilder.parse(fileConfig.getAbsolutePath());
				Node Root = document.getDocumentElement();
				NodeList items = Root.getChildNodes();
				for (int i = 0; i < items.getLength(); i++) {
					Node item = items.item(i);
					switch (item.getNodeName().trim()) {
					case "UI":
						NodeList iitems = item.getChildNodes();
						for (int ii = 0; ii < iitems.getLength(); ii++) {
							Node iitem = iitems.item(ii);
							if (iitem.getNodeName().equals("Language")) {
								if (STORAGE.containsKey("lang"))
									iitem.setTextContent(STORAGE.get("lang"));
							}
						}
						break;
					case "VideoConfig":
						NodeList iitems1 = item.getChildNodes();
						for (int ii = 0; ii < iitems1.getLength(); ii++) {
							Node iitem = iitems1.item(ii);
							
							if (SETTINGS_NAMES.contains(iitem.getNodeName())) {
								if (STORAGE.containsKey(iitem.getNodeName().trim()))
									iitem.setTextContent(STORAGE.get(iitem.getNodeName().trim()));
							}
						}
						break;
					default:
						break;
					}
				}
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				Result output = new StreamResult(fileConfig);
				Source input = new DOMSource(document);
				transformer.transform(input, output);
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		} else
			Log.print("Не найден файл настроек клиента");
	}
	public static void setLanguage(String lang) {
		if (!STORAGE.containsKey("lang")) return;
		STORAGE.replace("lang", lang);
	}
	/**
	 * Установить тип звука
	 * @param b true стерео, false вокруг
	 */
	public static void setAudiomode(boolean b) {
		// 0 = false
		if (!STORAGE.containsKey("audiomode")) return;
		if (b)
			STORAGE.replace("audiomode", "1");
		else
			STORAGE.replace("audiomode", "0");
	}
	/**
	 * Установить 
	 * @param b true звук будет чище
	 */
	public static void setAudioquality(boolean b) {
		// 0 = false
		if (!STORAGE.containsKey("audioquality")) return;
		if (b)
			STORAGE.replace("audioquality", "1");
		else
			STORAGE.replace("audioquality", "0");
	}
	/**
	 * Но на 100 будет выглядеть радиактивно
	 * @param i - от 0 до 100
	 */
	public static void setBrightness(int i) {
		if (i < 0) i = 0;
		if (i > 100) i = 100;
		if (!STORAGE.containsKey("brightness")) STORAGE.put("brightness", String.valueOf(i));
		STORAGE.replace("brightness", String.valueOf(i));
	}
	/**
	 * Настройка графики
	 * @param i - от 0-ля до 4
	 */
	public static void setPerformancelevel(int i) {
		if (i < 0) i = 0;
		if (i > 5) i = 5;
		if (!STORAGE.containsKey("performancelevel")) STORAGE.put("performancelevel", String.valueOf(i));
		STORAGE.replace("performancelevel", String.valueOf(i));
	}
	/**
	 * Множитель размеров окна
	 * @param i - от 0-ля до 100
	 */
	public static void setPixelaspectratiooverride(int i) {
		if (i < 0) i = 0;
		if (i > 100) i = 100;
		if (!STORAGE.containsKey("pixelaspectratiooverride")) STORAGE.put("pixelaspectratiooverride", String.valueOf(i));
		STORAGE.replace("pixelaspectratiooverride", String.valueOf(i));
	}
	/**
	 * Включить версикальную синхронизацию
	 * @param b
	 */
	public static void setVsync(boolean b) {
		// 0 = false
		if (!STORAGE.containsKey("vsyncon")) return;
		if (b)
			STORAGE.replace("vsyncon", "1");
		else
			STORAGE.replace("vsyncon", "0");
	}
	/**
	 * Motion Blur-effect (размытие в движении)
	 * @param i
	 */
	public static void setMotionblurenable(boolean b) {
		if (b) {
			if (STORAGE.containsKey("motionblurenable"))
				STORAGE.replace("motionblurenable", "1");
			else
				STORAGE.put("motionblurenable", "1");
		} else {
			if (STORAGE.containsKey("motionblurenable"))
				STORAGE.replace("motionblurenable", "0");
			else
				STORAGE.put("motionblurenable", "0");
		}
	}
	/**
	 * Overbright-effect (Это когда выходишь из гаража и твоя машина покрывается сеткой)
	 * @param i
	 */
	public static void setOverbrightenable(boolean b) {
		if (b) {
			if (STORAGE.containsKey("overbrightenable"))
				STORAGE.replace("overbrightenable", "1");
			else
				STORAGE.put("overbrightenable", "1");
		} else {
			if (STORAGE.containsKey("overbrightenable"))
				STORAGE.replace("overbrightenable", "0");
			else
				STORAGE.put("overbrightenable", "0");
		}
	}
	/**
	 * Включает систему частиц, отвечает за эффекты поверапов, дым, огонь из глушителя
	 * @param b
	 */
	public static void setParticlesystemenable(boolean b) {
		if (b) {
			if (STORAGE.containsKey("particlesystemenable"))
				STORAGE.replace("particlesystemenable", "1");
			else
				STORAGE.put("particlesystemenable", "1");
		} else {
			if (STORAGE.containsKey("particlesystemenable"))
				STORAGE.replace("particlesystemenable", "0");
			else
				STORAGE.put("particlesystemenable", "0");
		}
	}
	/**
	 * Отвечает за качество шейдеров
	 * @param b Параметр от 1 до 3.
	 */
	public static void setShaderdetail(int i) {
		if (i < 1) i = 1;
		if (i > 3) i = 3;
		if (i == 3) i = 4;
		if (STORAGE.containsKey("shaderdetail"))
			STORAGE.replace("shaderdetail", String.valueOf(i));
		else
			STORAGE.put("shaderdetail", String.valueOf(i));
	}
	/**
	 * Отвечает за качество теней
	 * @param b Параметр от 0 до 2.
	 */
	public static void setShadowdetail(int i) {
		if (i < 0) i = 0;
		if (i > 2) i = 2;
		if (STORAGE.containsKey("shadewdetail"))
			STORAGE.replace("shadewdetail", String.valueOf(i));
		else
			STORAGE.put("shadewdetail", String.valueOf(i));
	}
	/**
	 * Включает симуляцию воды
	 * @param b
	 */
	public static void setWatersimenable(boolean b) {
		if (b) {
			if (STORAGE.containsKey("watersimenable"))
				STORAGE.replace("watersimenable", "1");
			else
				STORAGE.put("watersimenable", "1");
		} else {
			if (STORAGE.containsKey("watersimenable"))
				STORAGE.replace("watersimenable", "0");
			else
				STORAGE.put("watersimenable", "0");
		}
	}
	/**
	 * Получить язык клиента из настроек
	 * @return
	 */
	public static String getLanguage() {
		if (STORAGE.containsKey("lang"))
			return STORAGE.get("lang");
		else
			return "";
	}
	/**
	 * Режим аудио
	 * @return true = стерео, false = вокруг
	 */
	public static boolean getAudiomode() {
		if (STORAGE.containsKey("audiomode") && STORAGE.get("audiomode").equalsIgnoreCase("1"))
			return true;
		else
			return false;
	}
	/**
	 * Качество аудио
	 * @return
	 */
	public static boolean getAudioquality() {
		if (STORAGE.containsKey("audioquality") && STORAGE.get("audioquality").equalsIgnoreCase("1"))
			return true;
		else
			return false;
	}
	/**
	 * Получить яркость
	 * @return
	 */
	public static int getBrightness() {
		if (!STORAGE.containsKey("brightness")) return 52;
		return Integer.valueOf(STORAGE.get("brightness"));
	}
	/**
	 * Получить множитель размеров окна
	 * @return
	 */
	public static int getPerformancelevel() {
		if (!STORAGE.containsKey("performancelevel")) return 1;
		return Integer.valueOf(STORAGE.get("performancelevel"));
	}
	/**
	 * Получить множитель размеров окна
	 * @return
	 */
	public static int getPixelaspectratiooverride() {
		if (!STORAGE.containsKey("pixelaspectratiooverride")) return 52;
		return Integer.valueOf(STORAGE.get("pixelaspectratiooverride"));
	}
	/**
	 * Включена ли вертикальная синхронизация
	 * @return
	 */
	public static boolean getVsync() {
		if (STORAGE.containsKey("vsyncon") && STORAGE.get("vsyncon").equalsIgnoreCase("1"))
			return true;
		else
			return false;
	}
	/**
	 * Motion Blur-effect (размытие в движении)
	 * @return
	 */
	public static boolean getMotionblurenable() {
		if (STORAGE.containsKey("motionblurenable") && STORAGE.get("motionblurenable").equalsIgnoreCase("1"))
			return true;
		else
			return false;
	}
	/**
	 * Overbright-effect (Это когда выходишь из гаража и твоя машина покрывается сеткой)
	 * @return
	 */
	public static boolean getOverbrightenable() {
		if (STORAGE.containsKey("overbrightenable") && STORAGE.get("overbrightenable").equalsIgnoreCase("1"))
			return true;
		else
			return false;
	}
	/**
	 * Включает систему частиц, отвечает за эффекты поверапов, дым, огонь из глушителя
	 * @return
	 */
	public static boolean getparticlesystemenable() {
		if (STORAGE.containsKey("particlesystemenable") && STORAGE.get("particlesystemenable").equalsIgnoreCase("1"))
			return true;
		else
			return false;
	}
	/**
	 * Отвечает за качество шейдеров
	 * @return
	 */
	public static int getShaderdetail() {
		if (!STORAGE.containsKey("shaderdetail")) return 0;
		return Integer.valueOf(STORAGE.get("shaderdetail"));
	}
	/**
	 * Отвечает за качество теней
	 * @return
	 */
	public static int getShadewdetail() {
		if (!STORAGE.containsKey("shadewdetail")) return 0;
		return Integer.valueOf(STORAGE.get("shadewdetail"));
	}
	/**
	 * Включает симуляцию воды
	 * @return
	 */
	public static boolean getWatersimenable() {
		if (STORAGE.containsKey("watersimenable") && STORAGE.get("watersimenable").equalsIgnoreCase("1"))
			return true;
		else
			return false;
	}
}
