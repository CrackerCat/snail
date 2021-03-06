package com.acgist.snail.utils;

import java.util.BitSet;

/**
 * <p>Piece位图工具</p>
 * <p>每个Piece占一位，每个字节的高位（末尾）表示八个Piece中的第一块，没有下载的Piece使用{@code 0}占位。</p>
 * 
 * @author acgist
 * @since 1.0.0
 */
public final class BitfieldUtils {

	/**
	 * <p>工具类禁止实例化</p>
	 */
	private BitfieldUtils() {
	}
	
	/**
	 * <p>Piece位图转为Piece位图字节数组</p>
	 * 
	 * @param pieceSize Piece数量
	 * @param pieces Piece位图
	 * 
	 * @return Piece位图字节数组
	 */
	public static final byte[] toBytes(final int pieceSize, final BitSet pieces) {
		final int byteSize = NumberUtils.ceilDiv(pieceSize, 8);
		final byte[] bitfield = new byte[byteSize];
		final byte[] value = pieces.toByteArray();
		for (int index = 0; index < value.length; index++) {
			value[index] = reverse(value[index]);
		}
		System.arraycopy(value, 0, bitfield, 0, value.length);
		return bitfield;
	}
	
	/**
	 * <p>Piece位图字节数组转为Piece位图</p>
	 * 
	 * @param bitfield Piece位图字节数组
	 * 
	 * @return Piece位图
	 */
	public static final BitSet toBitSet(final byte[] bitfield) {
		for (int index = 0; index < bitfield.length; index++) {
			bitfield[index] = reverse(bitfield[index]);
		}
		return BitSet.valueOf(bitfield);
	}
	
	/**
	 * <p>大端小端互转：{@code 01000001} -&gt; {@code 10000010}</p>
	 * 
	 * @param value 数据
	 * 
	 * @return 结果
	 */
	private static final byte reverse(final byte value) {
		int result = value;
		result = (result & 0B11110000) >> 4 | (result & 0B00001111) << 4;
		result = (result & 0B11001100) >> 2 | (result & 0B00110011) << 2;
		result = (result & 0B10101010) >> 1 | (result & 0B01010101) << 1;
		return (byte) result;
	}

}
