package com.acgist.snail.system.bencode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acgist.snail.system.bencode.BEncodeDecoder.Type;

/**
 * <p>B编码</p>
 * <p>put系列方法配合flush使用。</p>
 * <p>支持数据类型：Number、String、byte[]。</p>
 * 
 * @author acgist
 * @since 1.0.0
 */
public class BEncodeEncoder {

	private static final Logger LOGGER = LoggerFactory.getLogger(BEncodeEncoder.class);
	
	private List<Object> list;
	private Map<String, Object> map;
	private BEncodeDecoder.Type type;
	private ByteArrayOutputStream outputStream;
	
	private BEncodeEncoder() {
		this.outputStream = new ByteArrayOutputStream();
	}
	
	public static final BEncodeEncoder newInstance() {
		return new BEncodeEncoder();
	}

	/**
	 * 新建Map
	 */
	public BEncodeEncoder newMap() {
		this.type = Type.map;
		this.map = new LinkedHashMap<>();
		return this;
	}

	/**
	 * 新建List
	 */
	public BEncodeEncoder newList() {
		this.type = Type.list;
		this.list = new ArrayList<>();
		return this;
	}
	
	/**
	 * 向List中添加数据
	 */
	public BEncodeEncoder put(Object value) {
		if(this.type == Type.list) {
			this.list.add(value);
		}
		return this;
	}
	
	/**
	 * 向List中添加数据
	 */
	public BEncodeEncoder put(List<?> list) {
		if(this.type == Type.list) {
			this.list.addAll(list);
		}
		return this;
	}
	
	/**
	 * 向Map中添加数据
	 */
	public BEncodeEncoder put(String key, Object value) {
		if(this.type == Type.map) {
			this.map.put(key, value);
		}
		return this;
	}
	
	/**
	 * 向Map中添加数据
	 */
	public BEncodeEncoder put(Map<String, Object> map) {
		if(this.type == Type.map) {
			this.map.putAll(map);
		}
		return this;
	}

	/**
	 * 将List和Map中的数据刷入字符流，配合put系列方法使用。
	 */
	public BEncodeEncoder flush() {
		if(this.type == Type.map) {
			this.build(this.map);
		} else if(this.type == Type.list) {
			this.build(this.list);
		} else {
			LOGGER.warn("B编码刷新时不存在类型");
		}
		return this;
	}
	
	/**
	 * 添加Map
	 */
	public BEncodeEncoder build(Map<?, ?> map) {
		if(map == null) {
			return this;
		}
		this.outputStream.write(BEncodeDecoder.TYPE_D);
		map.forEach((key, value) -> {
			final String keyValue = key.toString();
			final byte[] keyValues = keyValue.getBytes();
			this.write(String.valueOf(keyValues.length).getBytes());
			this.outputStream.write(BEncodeDecoder.SEPARATOR);
			this.write(keyValues);
			if(value instanceof Number) {
				this.outputStream.write(BEncodeDecoder.TYPE_I);
				this.write(value.toString().getBytes());
				this.outputStream.write(BEncodeDecoder.TYPE_E);
			} else if(value instanceof Map) {
				build((Map<?, ?>) value);
			} else if(value instanceof List) {
				build((List<?>) value);
			} else {
				byte[] bytes = null;
				if(value instanceof byte[]) {
					bytes = (byte[]) value;
				} else if(value instanceof String) {
					bytes = ((String) value).getBytes();
				} else {
					LOGGER.warn("BCode不支持的类型，key：{}，value：{}", key, value);
				}
				if(bytes != null) {
					this.write(String.valueOf(bytes.length).getBytes());
					this.outputStream.write(BEncodeDecoder.SEPARATOR);
					this.write(bytes);
				}
			}
		});
		this.outputStream.write(BEncodeDecoder.TYPE_E);
		return this;
	}

	/**
	 * 添加List
	 */
	public BEncodeEncoder build(List<?> list) {
		if(list == null) {
			return this;
		}
		this.outputStream.write(BEncodeDecoder.TYPE_L);
		list.forEach(value -> {
			if(value instanceof Number) {
				this.outputStream.write(BEncodeDecoder.TYPE_I);
				this.write(value.toString().getBytes());
				this.outputStream.write(BEncodeDecoder.TYPE_E);
			} else if(value instanceof Map) {
				build((Map<?, ?>) value);
			} else if(value instanceof List) {
				build((List<?>) value);
			} else {
				byte[] bytes = null;
				if(value instanceof byte[]) {
					bytes = (byte[]) value;
				} else if(value instanceof String) {
					bytes = ((String) value).getBytes();
				} else {
					LOGGER.warn("BCode不支持的类型，value：{}", value);
				}
				if(bytes != null) {
					this.write(String.valueOf(bytes.length).getBytes());
					this.outputStream.write(BEncodeDecoder.SEPARATOR);
					this.write(bytes);
				}
			}
		});
		this.outputStream.write(BEncodeDecoder.TYPE_E);
		return this;
	}
	
	/**
	 * 添加字符数组
	 */
	public BEncodeEncoder build(byte[] bytes) {
		write(bytes);
		return this;
	}
	
	/**
	 * 写入字符数组
	 */
	private void write(byte[] bytes) {
		if(bytes == null) {
			return;
		}
		try {
			this.outputStream.write(bytes);
		} catch (IOException e) {
			LOGGER.error("B编码输出异常", e);
		}
	}
	
	/**
	 * 获取字符流，获取后将关闭流。
	 */
	public byte[] bytes() {
		try {
			return this.outputStream.toByteArray();
		} finally {
			try {
				this.outputStream.close();
			} catch (IOException e) {
				LOGGER.error("关闭字符流异常", e);
			}
		}
	}

	/**
	 * 获取字符串，将关闭字符流。
	 */
	@Override
	public String toString() {
		return new String(bytes());
	}
	
	/**
	 * Map转为B编码字符
	 */
	public static final byte[] encodeMap(Map<?, ?> map) {
		return newInstance().build(map).bytes();
	}
	
	/**
	 * List转为B编码字符
	 */
	public static final byte[] encodeList(List<?> list) {
		return newInstance().build(list).bytes();
	}

}