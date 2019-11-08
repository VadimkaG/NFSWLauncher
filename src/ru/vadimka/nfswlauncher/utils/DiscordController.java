package ru.vadimka.nfswlauncher.utils;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Log;

public abstract class DiscordController {
	
	private static DiscordRPC lib;
	private static boolean is_started = false;
	private static DiscordRichPresence presence;
	
	/**
	 * Загрузить discord RPC
	 */
	public static void load() {
		if (!Config.DISCORD_ALLOW) return;
		lib = DiscordRPC.INSTANCE;
		presence = new DiscordRichPresence();
		String applicationId = "";
		DiscordEventHandlers handlers = new DiscordEventHandlers();
		is_started = true;
		
		handlers.ready = (user) -> {
			Log.getLogger().info("Discord RPC инициализирован.");
		};
		
		handlers.disconnected = (i, str) -> {
			Log.getLogger().info("Discord RPC завершен.");
		};
		
		lib.Discord_Initialize(applicationId, handlers, true, null);
		presence.startTimestamp = System.currentTimeMillis() / 1000;
		presence.details = "Игра не запущена";
		presence.largeImageKey = "rw_logo";
		lib.Discord_UpdatePresence(presence);
	}
	/**
	 * Обновить статус
	 * @param str
	 */
	public static void updateState(String state, String detail) {
		if (!Config.DISCORD_ALLOW) return;
		presence.state = state;
		presence.details = detail;
		lib.Discord_UpdatePresence(presence);
	}
	/**
	 * Запущен ли discord RPC
	 * @return
	 */
	public static boolean isStarted() {
		return is_started;
	}
	/**
	 * Остановить discord RPC
	 */
	public static void shutdown() {
		if (!is_started) return;
		lib.Discord_Shutdown();
		is_started = false;
	}
}
