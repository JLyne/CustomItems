package uk.co.notnull.CustomItems.messages;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Message {
	private final String id;
	private final boolean prefixed;
	private final MessageType type;
	private final Map<String, String> stringReplacements;
	private final Map<String, ComponentLike> componentReplacements;

	public Message(@NotNull String id, MessageType type, boolean prefixed,
				   Map<String, String> stringReplacements, Map<String, ComponentLike> componentReplacements) {
		this.id = id;
		this.type = type;
		this.prefixed = prefixed;
		this.stringReplacements = stringReplacements;
		this.componentReplacements = componentReplacements;
	}

	public String getString() {
		String result = prefixed ? Messages.getPrefix(type) + Messages.get(id) : Messages.get(id);

		for(Map.Entry<String, String> replacement: stringReplacements.entrySet()) {
			result.replaceAll(replacement.getKey(), replacement.getValue());
		}

		return result;
	}

	public ComponentLike getComponent() {
		String result = prefixed ? Messages.getPrefix(type) + Messages.get(id) : Messages.get(id);

		TagResolver.Builder placeholders = TagResolver.builder();

		for(Map.Entry<String, String> replacement: stringReplacements.entrySet()) {
			placeholders.resolver(Placeholder.parsed(replacement.getKey(), replacement.getValue()));
		}

		for(Map.Entry<String, ComponentLike> replacement: componentReplacements.entrySet()) {
			placeholders.resolver(Placeholder.component(replacement.getKey(), replacement.getValue()));
		}

		return Messages.miniMessage.deserialize(result, placeholders.build());
	}

	public void send(Audience audience) {
		audience.sendMessage(getComponent());
	}

	public static Message.Builder builder(String id) {
		return new Message.Builder(id);
	}

	public enum MessageType {
		INFO,
		ERROR,
		WARNING
	}

	public static class Builder {
		private final String id;
		private boolean prefixed = false;
		private MessageType type = MessageType.INFO;
		private Map<String, String> stringReplacements = new HashMap<>();
		private Map<String, ComponentLike> componentReplacements = new HashMap<>();

		public Builder(String id) {
			this.id = id;
		}

		public Builder prefixed() {
			this.prefixed = true;
			return this;
		}

		public Builder prefixed(boolean prefixed) {
			this.prefixed = prefixed;
			return this;
		}

		public Builder type(MessageType type) {
			this.type = type;
			return this;
		}

		public Builder replacement(String find, String replace) {
			stringReplacements.put(find, replace);
			return this;
		}

		public Builder replacement(String find, ComponentLike replace) {
			componentReplacements.put(find, replace);
			return this;
		}

		public Message build() {
			return new Message(id, type, prefixed, stringReplacements, componentReplacements);
		}
	}
}
