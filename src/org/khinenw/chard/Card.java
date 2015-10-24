package org.khinenw.chard;

public class Card {
	private Number number;
	private Shape shape;
	
	public Card(Shape s, Number n){
		if(n == Number.JOKER){
			if((s != Shape.RED_JOKER) && (s != Shape.BLACK_JOKER)) s = Shape.RED_JOKER;
		}
		
		number = n;
		shape = s;
	}
	
	public Shape getShape(){
		return shape;
	}
	
	public Number getNumber(){
		return number;
	}
}

enum Shape{
	SPADE, DIAMOND, HEART, CLUB, RED_JOKER, BLACK_JOKER
}

enum Number{
	ACE, N2, N3, N4, N5, N6, N7, N8, N9, N10, JACK, QUEEN, KING, JOKER
}
