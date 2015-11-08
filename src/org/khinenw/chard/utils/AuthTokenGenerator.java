package org.khinenw.chard.utils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AuthTokenGenerator{
	public static final String AVAILABLE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	public static String generate(int size){
		List<Integer> chars = AVAILABLE_CHARACTERS.chars().boxed().collect(Collectors.toList());
		Collections.shuffle(chars);
		
		StringBuilder tokenBuilder = new StringBuilder();
		IntStream.range(0, size).mapToObj(i -> new Character((char) chars.get(i).intValue())).forEach((v) -> {
			tokenBuilder.append(v.charValue());
		});
		return tokenBuilder.toString();
	}
}
